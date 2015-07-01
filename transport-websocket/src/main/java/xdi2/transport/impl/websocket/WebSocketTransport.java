package xdi2.transport.impl.websocket;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.Graph;
import xdi2.core.constants.XDIConstants;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.io.MimeType;
import xdi2.core.io.XDIReader;
import xdi2.core.io.XDIReaderRegistry;
import xdi2.core.io.XDIWriter;
import xdi2.core.io.XDIWriterRegistry;
import xdi2.core.syntax.XDIAddress;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.response.MessagingResponse;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.impl.AbstractMessagingTarget;
import xdi2.messaging.target.interceptor.impl.WriteListenerInterceptor;
import xdi2.messaging.target.interceptor.impl.WriteListenerInterceptor.WriteListener;
import xdi2.transport.exceptions.Xdi2TransportException;
import xdi2.transport.impl.AbstractTransport;
import xdi2.transport.impl.http.HttpTransportRequest;
import xdi2.transport.impl.http.HttpTransportResponse;
import xdi2.transport.impl.http.registry.HttpMessagingTargetRegistry;
import xdi2.transport.impl.http.registry.MessagingTargetMount;

public class WebSocketTransport extends AbstractTransport<WebSocketRequest, WebSocketResponse> {

	private static final Logger log = LoggerFactory.getLogger(WebSocketTransport.class);

	private HttpMessagingTargetRegistry httpMessagingTargetRegistry;
	private String endpointPath;

	public WebSocketTransport(HttpMessagingTargetRegistry httpMessagingTargetRegistry, String endpointPath) {

		this.httpMessagingTargetRegistry = httpMessagingTargetRegistry;
		this.endpointPath = endpointPath;
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
	}

	@Override
	public void execute(WebSocketRequest request, WebSocketResponse response) {

		if (log.isInfoEnabled()) log.info("Incoming message to " + request.getRequestPath() + ". Subprotocol: " + request.getSubprotocol());

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

		final MessagingTarget messagingTarget = messagingTargetMount == null ? null : messagingTargetMount.getMessagingTarget();

		// execute interceptors

		// TODO: no interceptors

		// no messaging target?

		if (messagingTarget == null) {

			// TODO: what to do here?
			log.warn("No XDI messaging target configured. TODO: what to do here?");

			return;
		}

		// construct message envelope from reader

		try {

			MessageEnvelope messageEnvelope = read(request, response);
			if (messageEnvelope == null) return;
		} catch (IOException ex) {

			throw new Xdi2TransportException("Invalid message envelope: " + ex.getMessage(), ex);
		}

		// execute the message envelope against our message target, save result

		MessagingResponse messagingResponse = this.execute(messageEnvelope, messagingTarget, request, response);
		if (messagingResponse == null || messagingResponse.getGraph() == null) return;

		// EXPERIMENTAL: install a write listener

		WriteListenerInterceptor writeListenerInterceptor = ((AbstractMessagingTarget) messagingTarget).getInterceptors().findInterceptor(WriteListenerInterceptor.class);
		WebSocketWriteListener webSocketWriteListener = request.getWebSocketMessageHandler().getWebSocketWriteListener();

		if (writeListenerInterceptor != null && webSocketWriteListener == null) {

			webSocketWriteListener = new WebSocketWriteListener(messageEnvelope, messagingTarget, request, response);
			request.getWebSocketMessageHandler().setWebSocketWriteListener(webSocketWriteListener);

			writeListenerInterceptor.addWriteListener(XDIConstants.XDI_ADD_ROOT, webSocketWriteListener);
		}

		// send out messaging response

		sendMessagingResponse(messagingResponse, request, response);
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
	 * Helper methods
	 */

	private static void sendMessagingResponse(MessagingResponse messagingResponse, WebSocketRequest request, WebSocketResponse response) throws IOException {

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

	private static void sendError(HttpTransportRequest request, HttpTransportResponse response, Exception ex) throws IOException {

		log.error("Internal server error: " + ex.getMessage() + ". Sending " + HttpTransportResponse.SC_NOT_FOUND + ".", ex);
		response.sendError(HttpTransportResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error: " + ex.getMessage());
	}

	/*
	 * Helper classes
	 */

	public class WebSocketWriteListener implements WriteListener {

		private MessageEnvelope messageEnvelope;
		private MessagingTarget messagingTarget;
		private WebSocketRequest request;
		private WebSocketResponse response;

		private WebSocketWriteListener(MessageEnvelope messageEnvelope, MessagingTarget messagingTarget, WebSocketRequest request, WebSocketResponse response) {

			this.messageEnvelope = messageEnvelope;
			this.messagingTarget = messagingTarget;
			this.request = request;
			this.response = response;
		}

		@Override
		public void onWrite(List<XDIAddress> writeXDIAddresses) {

			try {

				// execute the message envelope against our message target, save result

				MessagingResponse messagingResponse = WebSocketTransport.this.execute(this.messageEnvelope, this.messagingTarget, this.request, this.response);
				if (messagingResponse == null || messagingResponse.getGraph() == null) return;

				// send out result

				sendMessagingResponse(messagingResponse, this.request, this.response);
			} catch (Exception ex) {

				log.error("Unexpected exception: " + ex.getMessage(), ex);
				handleInternalException(this.request, this.response, ex);
				return;
			}
		}
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

	public String getEndpointPath() {

		return this.endpointPath;
	}

	public void setEndpointPath(String endpointPath) {

		this.endpointPath = endpointPath;
	}
}
