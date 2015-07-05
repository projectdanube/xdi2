package xdi2.client.impl.websocket.endpoint;

import java.io.IOException;
import java.io.Reader;

import javax.websocket.CloseReason;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.client.impl.websocket.XDIWebSocketClient;
import xdi2.client.impl.websocket.XDIWebSocketClient.Callback;
import xdi2.core.Graph;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.io.MimeType;
import xdi2.core.io.XDIReader;
import xdi2.core.io.XDIReaderRegistry;
import xdi2.messaging.MessageEnvelope;

public class WebSocketClientMessageHandler implements javax.websocket.MessageHandler.Whole<Reader> {

	private static final Logger log = LoggerFactory.getLogger(WebSocketClientMessageHandler.class);

	private Session session;

	public WebSocketClientMessageHandler(Session session) {

		this.session = session;
	}

	@Override
	public void onMessage(Reader reader) {

		if (log.isDebugEnabled()) log.debug("Incoming message on session " + this.getSession().getId());

		// read properties

		XDIWebSocketClient webSocketClient = (XDIWebSocketClient) this.getSession().getUserProperties().get("xdiWebSocketClient");

		// construct message envelope from reader

		MessageEnvelope messageEnvelope;

		try {

			messageEnvelope = read(this.getSession(), reader);
			if (messageEnvelope == null) return;
		} catch (IOException ex) {

			try {

				log.error("I/O exception: " + ex.getMessage(), ex);
				this.getSession().close(new CloseReason(CloseCodes.UNEXPECTED_CONDITION, "I/O exception: " + ex.getMessage()));
				return;
			} catch (IOException ex2) {

				log.error("Cannot close session: " + ex.getMessage(), ex);
				return;
			}
		}

		// callback

		Callback callback = webSocketClient.getCallback();
		if (callback != null) callback.onMessageEnvelope(messageEnvelope);
	}

	/*
	 * Getters and setters
	 */

	public Session getSession() {

		return this.session;
	}

	/*
	 * Helper methods
	 */

	private static MessageEnvelope read(Session session, Reader reader) throws IOException {

		// try to find an appropriate reader for the provided mime type

		XDIReader xdiReader = null;

		String contentType = session.getNegotiatedSubprotocol();
		MimeType recvMimeType = contentType != null ? new MimeType(contentType) : null;
		xdiReader = recvMimeType != null ? XDIReaderRegistry.forMimeType(recvMimeType) : null;

		if (xdiReader == null) xdiReader = XDIReaderRegistry.getDefault();

		// read everything into an in-memory XDI graph (a message envelope)

		if (log.isDebugEnabled()) log.debug("Reading message in " + recvMimeType + " with reader " + xdiReader.getClass().getSimpleName() + ".");

		MessageEnvelope messageEnvelope;

		try {

			Graph graph = MemoryGraphFactory.getInstance().openGraph();

			xdiReader.read(graph, reader);
			messageEnvelope = MessageEnvelope.fromGraph(graph);
		} catch (IOException ex) {

			throw ex;
		} catch (Exception ex) {

			log.error("Cannot parse XDI graph: " + ex.getMessage(), ex);
			throw new IOException("Cannot parse XDI graph: " + ex.getMessage(), ex);
		} finally {

			reader.close();
		}

		if (log.isDebugEnabled()) log.debug("Message envelope received (" + messageEnvelope.getMessageCount() + " messages). Executing...");

		// done

		return messageEnvelope;
	}
}
