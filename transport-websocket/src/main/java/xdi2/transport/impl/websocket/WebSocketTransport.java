package xdi2.transport.impl.websocket;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.websocket.CloseReason;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.Graph;
import xdi2.core.features.nodetypes.XdiPeerRoot;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.io.MimeType;
import xdi2.core.io.XDIReader;
import xdi2.core.io.XDIReaderRegistry;
import xdi2.core.io.XDIWriter;
import xdi2.core.io.XDIWriterRegistry;
import xdi2.core.syntax.XDIArc;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.response.MessagingResponse;
import xdi2.messaging.target.MessagingTarget;
import xdi2.transport.exceptions.Xdi2TransportException;
import xdi2.transport.impl.AbstractTransport;
import xdi2.transport.registry.impl.uri.UriMessagingTargetMount;
import xdi2.transport.registry.impl.uri.UriMessagingTargetRegistry;

public class WebSocketTransport extends AbstractTransport<WebSocketTransportRequest, WebSocketTransportResponse> {

	private static final Logger log = LoggerFactory.getLogger(WebSocketTransport.class);

	private UriMessagingTargetRegistry uriMessagingTargetRegistry;
	private String endpointPath;

	private Map<String, Session> sessionIdToSessions;
	private Map<XDIArc, Session> toPeerRootXDIArcSessions;
	private Map<String, XDIArc> sessionIdToPeerRootXDIArcs;

	public WebSocketTransport(UriMessagingTargetRegistry uriMessagingTargetRegistry, String endpointPath) {

		this.uriMessagingTargetRegistry = uriMessagingTargetRegistry;
		this.endpointPath = endpointPath;

		this.sessionIdToSessions = new HashMap<String, Session> ();
		this.toPeerRootXDIArcSessions = new HashMap<XDIArc, Session> ();
		this.sessionIdToPeerRootXDIArcs = new HashMap<String, XDIArc> ();
	}

	public WebSocketTransport() {

		this(null, null);
	}

	@Override
	public void init() throws Exception {

		super.init();
	}

	@Override
	public void shutdown() throws Exception {

		super.shutdown();

		// unregister sessions

		for (Session session : this.sessionIdToSessions.values()) {

			session.close(new CloseReason(CloseCodes.GOING_AWAY, "Shuttind down."));
		}

		this.sessionIdToSessions.clear();
		this.toPeerRootXDIArcSessions.clear();
		this.sessionIdToPeerRootXDIArcs.clear();
	}

	@Override
	public void execute(WebSocketTransportRequest request, WebSocketTransportResponse response) throws IOException {

		if (log.isInfoEnabled()) log.info("Incoming message to " + request.getRequestPath() + ". Subprotocol: " + request.getSubprotocol());

		try {

			UriMessagingTargetMount uriMessagingTargetMount = this.getUriMessagingTargetRegistry().lookup(request.getRequestPath());

			this.processMessage(request, response, uriMessagingTargetMount);
		} catch (IOException ex) {

			throw ex;
		} catch (Exception ex) {

			sendCloseCannotAccept(request, response, ex);
			return;
		}

		if (log.isDebugEnabled()) log.debug("Successfully processed message.");
	}

	protected void processMessage(WebSocketTransportRequest request, WebSocketTransportResponse response, UriMessagingTargetMount uriMessagingTargetMount) throws Xdi2TransportException, IOException {

		final MessagingTarget messagingTarget = uriMessagingTargetMount == null ? null : uriMessagingTargetMount.getMessagingTarget();
		MessageEnvelope messageEnvelope;
		MessagingResponse messagingResponse;

		// execute interceptors

		boolean result = InterceptorExecutor.executeWebSocketTransportInterceptorsMessage(this.getInterceptors(), this, request, response, uriMessagingTargetMount);

		if (result) {

			if (log.isDebugEnabled()) log.debug("Skipping request according to HTTP transport interceptor (GET).");
			return;
		}

		// no messaging target?

		if (messagingTarget == null) {

			sendCloseViolatedPolicy(request, response);
			return;
		}

		// construct message envelope from reader

		try {

			messageEnvelope = read(request, response);
			if (messageEnvelope == null) return;
		} catch (IOException ex) {

			throw new Xdi2TransportException("Invalid message envelope: " + ex.getMessage(), ex);
		}

		// TODO HACK: register session to message senders

		for (Message message : messageEnvelope.getMessages()) {

			XDIArc toPeerRootXDIArc = XdiPeerRoot.createPeerRootXDIArc(message.getSenderXDIAddress());
			this.registerSession(request.getWebSocketMessageHandler().getSession(), toPeerRootXDIArc);
		}

		// execute the message envelope against our message target, save result

		messagingResponse = this.execute(messageEnvelope, messagingTarget, request, response);
		if (messagingResponse == null || messagingResponse.getGraph() == null) return;

		// done

		sendText(request, response, messagingResponse);
	}

	private MessageEnvelope read(WebSocketTransportRequest request, WebSocketTransportResponse response) throws IOException {

		// try to find an appropriate reader for the provided mime type

		XDIReader xdiReader = null;

		String contentType = request.getSubprotocol();
		MimeType recvMimeType = contentType != null ? new MimeType(contentType) : null;
		xdiReader = recvMimeType != null ? XDIReaderRegistry.forMimeType(recvMimeType) : null;

		if (xdiReader == null) xdiReader = XDIReaderRegistry.getDefault();

		// read everything into an in-memory XDI graph (a message envelope)

		if (log.isDebugEnabled()) log.debug("Reading message in " + recvMimeType + " with reader " + xdiReader.getClass().getSimpleName() + ".");

		Graph graph = MemoryGraphFactory.getInstance().openGraph();
		MessageEnvelope messageEnvelope;
		long messageCount;

		Reader reader = request.getReader();

		try {

			xdiReader.read(graph, reader);
			messageEnvelope = MessageEnvelope.fromGraph(graph);
			messageCount = messageEnvelope.getMessageCount();
		} catch (IOException ex) {

			throw ex;
		} catch (Exception ex) {

			log.error("Cannot parse XDI graph: " + ex.getMessage(), ex);
			throw new IOException("Cannot parse XDI graph: " + ex.getMessage(), ex);
		} finally {

			reader.close();
		}

		if (log.isDebugEnabled()) log.debug("Message envelope received (" + messageCount + " messages). Executing...");

		return messageEnvelope;
	}

	/*
	 * Sessions
	 */

	public void registerSession(Session session, XDIArc toPeerRootXDIArc) {

		if (log.isDebugEnabled()) log.debug("Registering session " + session.getId() + " for TO peer root " + toPeerRootXDIArc);

		this.sessionIdToSessions.put(session.getId(), session);

		if (toPeerRootXDIArc != null) {

			this.toPeerRootXDIArcSessions.put(toPeerRootXDIArc, session);
			this.sessionIdToPeerRootXDIArcs.put(session.getId(), toPeerRootXDIArc);
		}
	}

	public void unregisterSession(Session session) {

		if (log.isDebugEnabled()) log.debug("Unregistering session " + session.getId());

		this.sessionIdToSessions.remove(session.getId());

		XDIArc toPeerRootXDIArc = this.sessionIdToPeerRootXDIArcs.get(session.getId());
		if (toPeerRootXDIArc != null) this.toPeerRootXDIArcSessions.remove(toPeerRootXDIArc);
		this.sessionIdToPeerRootXDIArcs.remove(session.getId());
	}

	public Session findSession(XDIArc toPeerRootXDIArc) {

		Session session = this.toPeerRootXDIArcSessions.get(toPeerRootXDIArc);

		if (log.isDebugEnabled()) log.debug("Found session " + (session == null ? null : session.getId()) + " for TO peer root " + toPeerRootXDIArc);

		return session;
	}

	/*
	 * Helper methods
	 */

	private static void sendText(WebSocketTransportRequest request, WebSocketTransportResponse response, MessagingResponse messagingResponse) throws IOException {

		// use default writer

		XDIWriter writer = null;

		MimeType sendMimeType = null;
		writer = sendMimeType != null ? XDIWriterRegistry.forMimeType(sendMimeType) : null;

		if (writer == null) writer = XDIWriterRegistry.getDefault();

		// send out the message result

		if (log.isDebugEnabled()) log.debug("Sending result in " + sendMimeType + " with writer " + writer.getClass().getSimpleName() + ".");

		StringWriter buffer = new StringWriter();
		writer.write(messagingResponse.getGraph(), buffer);

		if (buffer.getBuffer().length() > 0) {

			response.getAsync().sendText(buffer.getBuffer().toString());
		}

		if (log.isDebugEnabled()) log.debug("Output complete.");
	}

	private static void sendCloseViolatedPolicy(WebSocketTransportRequest request, WebSocketTransportResponse response) throws IOException {

		log.error("Violated policy: " + request.getRequestPath() + ". Sending " + CloseCodes.VIOLATED_POLICY + ".");
		request.getWebSocketMessageHandler().getSession().close(new CloseReason(CloseCodes.VIOLATED_POLICY, "Violated policy: " + request.getRequestPath()));
	}

	private static void sendCloseCannotAccept(WebSocketTransportRequest request, WebSocketTransportResponse response, Exception ex) throws IOException {

		log.error("Cannot accept: " + ex.getMessage() + ". Sending " + CloseCodes.CANNOT_ACCEPT + ".", ex);
		request.getWebSocketMessageHandler().getSession().close(new CloseReason(CloseCodes.CANNOT_ACCEPT, "Cannot accept: " + ex.getMessage()));
	}

	/*
	 * Getters and setters
	 */

	public UriMessagingTargetRegistry getUriMessagingTargetRegistry() {

		return this.uriMessagingTargetRegistry;
	}

	public void setUriMessagingTargetRegistry(UriMessagingTargetRegistry uriMessagingTargetRegistry) {

		this.uriMessagingTargetRegistry = uriMessagingTargetRegistry;
	}

	public String getEndpointPath() {

		return this.endpointPath;
	}

	public void setEndpointPath(String endpointPath) {

		this.endpointPath = endpointPath;
	}
}
