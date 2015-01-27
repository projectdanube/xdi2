package xdi2.transport.impl.websocket;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.Graph;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.io.MimeType;
import xdi2.core.io.XDIReader;
import xdi2.core.io.XDIReaderRegistry;
import xdi2.core.io.XDIWriter;
import xdi2.core.io.XDIWriterRegistry;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;
import xdi2.messaging.error.ErrorMessageResult;
import xdi2.messaging.target.MessagingTarget;
import xdi2.transport.exceptions.Xdi2TransportException;
import xdi2.transport.impl.AbstractTransport;
import xdi2.transport.impl.http.registry.HttpMessagingTargetRegistry;
import xdi2.transport.impl.http.registry.MessagingTargetMount;

public class WebSocketTransport extends AbstractTransport<WebSocketRequest, WebSocketResponse> {

	private static final Logger log = LoggerFactory.getLogger(WebSocketTransport.class);

	private HttpMessagingTargetRegistry httpMessagingTargetRegistry;

	public WebSocketTransport(HttpMessagingTargetRegistry httpMessagingTargetRegistry) {

		this.httpMessagingTargetRegistry = httpMessagingTargetRegistry;
	}

	public WebSocketTransport() {

		this(null);
	}

	@Override
	public void init() throws Exception {

		super.init();
	}

	@Override
	public void shutdown() throws Exception {

		super.shutdown();
	}

	public void doMessage(WebSocketRequest request, WebSocketResponse response) throws IOException {

		if (log.isInfoEnabled()) log.info("Incoming message to " + request.getSubprotocol() + ". Subprotocol: " + request.getSubprotocol());

		try {

			MessagingTargetMount messagingTargetMount = this.getHttpMessagingTargetRegistry().lookup(request.getRequestPath());

			this.processMessage(request, response, messagingTargetMount);
		} catch (Exception ex) {

			log.error("Unexpected exception: " + ex.getMessage(), ex);
			handleInternalException(request, response, ex);
			return;
		}

		if (log.isDebugEnabled()) log.debug("Successfully processed message.");
	}

	protected void processMessage(WebSocketRequest request, WebSocketResponse response, MessagingTargetMount messagingTargetMount) throws Xdi2TransportException, IOException {

		MessagingTarget messagingTarget = messagingTargetMount == null ? null : messagingTargetMount.getMessagingTarget();

		// execute interceptors

		// TODO: no interceptors

		// no messaging target?

		if (messagingTarget == null) {

			log.warn("No XDI messaging target configured at " + request.getRequestPath() + ".");

			return;
		}

		// construct message envelope from reader

		MessageEnvelope messageEnvelope = read(request, response);
		if (messageEnvelope == null) return;

		// execute the message envelope against our message target, save result

		MessageResult messageResult = this.execute(messageEnvelope, messagingTarget, request, response);
		if (messageResult == null || messageResult.getGraph() == null) return;

		// send out result

		sendMessageResult(messageResult, request, response);
	}

	private MessageEnvelope read(WebSocketRequest request, WebSocketResponse response) throws IOException {

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
		} catch (Exception ex) {

			log.error("Cannot parse XDI graph: " + ex.getMessage(), ex);
			this.handleException(request, response, new Exception("Cannot parse XDI graph: " + ex.getMessage(), ex));
			return null;
		} finally {

			reader.close();
		}

		if (log.isDebugEnabled()) log.debug("Message envelope received (" + messageCount + " messages). Executing...");

		return messageEnvelope;
	}

	/*
	 * Helper methods
	 */

	private static void sendMessageResult(MessageResult messageResult, WebSocketRequest request, WebSocketResponse response) throws IOException {

		// use default writer

		XDIWriter writer = null;

		MimeType sendMimeType = null;
		writer = sendMimeType != null ? XDIWriterRegistry.forMimeType(sendMimeType) : null;

		if (writer == null) writer = XDIWriterRegistry.getDefault();

		// send out the message result

		if (log.isDebugEnabled()) log.debug("Sending result in " + sendMimeType + " with writer " + writer.getClass().getSimpleName() + ".");

		StringWriter buffer = new StringWriter();
		writer.write(messageResult.getGraph(), buffer);

		if (buffer.getBuffer().length() > 0) {

			response.getAsync().sendText(buffer.getBuffer().toString());
		}

		if (log.isDebugEnabled()) log.debug("Output complete.");
	}

	private static void handleInternalException(WebSocketRequest request, WebSocketResponse response, Exception ex) throws IOException {

		log.error("Unexpected exception: " + ex.getMessage(), ex);
	}

	@Override
	protected void handleException(WebSocketRequest request, WebSocketResponse response, ErrorMessageResult errorMessageResult) throws IOException {

		// send error result

		sendMessageResult(errorMessageResult, request, response);
	}

	/*
	 * Getters and setters
	 */

	public HttpMessagingTargetRegistry getHttpMessagingTargetRegistry() {

		return this.httpMessagingTargetRegistry;
	}

	public void setHttpMessagingTargetRegistry(HttpMessagingTargetRegistry httpMessagingTargetRegistry) {

		this.httpMessagingTargetRegistry = httpMessagingTargetRegistry;
	}
}
