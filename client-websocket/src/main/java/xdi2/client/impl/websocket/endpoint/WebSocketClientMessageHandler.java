package xdi2.client.impl.websocket.endpoint;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import xdi2.core.syntax.XDIAddress;
import xdi2.core.util.CopyUtil;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.response.FullMessagingResponse;
import xdi2.messaging.response.FutureMessagingResponse;
import xdi2.messaging.response.LightMessagingResponse;
import xdi2.messaging.response.TransportMessagingResponse;
import xdi2.messaging.util.MessagingCloneUtil;

public class WebSocketClientMessageHandler implements javax.websocket.MessageHandler.Whole<Reader> {

	private static final Logger log = LoggerFactory.getLogger(WebSocketClientMessageHandler.class);

	private Session session;

	public WebSocketClientMessageHandler(Session session) {

		this.session = session;
	}

	@Override
	public void onMessage(Reader reader) {

		if (log.isDebugEnabled()) log.debug("Incoming WebSocket message on session " + this.getSession().getId());

		// read properties

		XDIWebSocketClient webSocketClient = (XDIWebSocketClient) this.getSession().getUserProperties().get("xdiWebSocketClient");

		// construct graph from reader

		Graph graph;

		try {

			graph = read(this.getSession(), reader);
			if (graph == null) return;
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

		// message envelope? messaging response?

		MessageEnvelope graphMessageEnvelope = MessageEnvelope.fromGraph(graph);

		MessageEnvelope callbackMessageEnvelope = null;
		TransportMessagingResponse callbackMessagingResponse = null;
		List<FutureMessagingResponse> callbackFutureMessagingResponses = new ArrayList<FutureMessagingResponse> ();
		Map<FutureMessagingResponse, XDIAddress> callbackFutureMessageXDIAddresses = new HashMap<FutureMessagingResponse, XDIAddress> ();
		Map<FutureMessagingResponse, FullMessagingResponse> callbackFutureFullMessagingResponses = new HashMap<FutureMessagingResponse, FullMessagingResponse> ();

		if (graphMessageEnvelope.getMessageCount() > 0) {

			Graph callbackMessageEnvelopeGraph = MemoryGraphFactory.getInstance().openGraph();
			Graph callbackFullMessagingResponseGraph = MemoryGraphFactory.getInstance().openGraph();

			for (Message message : graphMessageEnvelope.getMessages()) {

				if (message.getCorrelationXDIAddress() == null) {

					// this message should be sent to WebSocketClient.onMessageEnvelope() callback

					CopyUtil.copyContextNode(message.getContextNode(), callbackMessageEnvelopeGraph, null);
				} else {

					// this message should be sent to WebSocketClient.onMessagingResponse() callback

					CopyUtil.copyContextNode(message.getContextNode(), callbackFullMessagingResponseGraph, null);

					FutureMessagingResponse futureMessagingResponse = webSocketClient.getFutureMessagingResponses().get(message.getCorrelationXDIAddress());

					if (futureMessagingResponse != null) {

						webSocketClient.getFutureMessagingResponses().remove(message.getCorrelationXDIAddress());

						// this message should be sent to FutureMessagingResponse.onMessagingResponse() callback

						FullMessagingResponse fullMessagingResponse = FullMessagingResponse.fromMessageEnvelope(MessagingCloneUtil.cloneMessage(message, false).getMessageEnvelope());

						callbackFutureMessagingResponses.add(futureMessagingResponse);
						callbackFutureMessageXDIAddresses.put(futureMessagingResponse, message.getCorrelationXDIAddress());
						callbackFutureFullMessagingResponses.put(futureMessagingResponse, fullMessagingResponse);
					}
				}
			}

			if (! callbackMessageEnvelopeGraph.isEmpty()) callbackMessageEnvelope = MessageEnvelope.fromGraph(callbackMessageEnvelopeGraph);
			if (! callbackFullMessagingResponseGraph.isEmpty()) callbackMessagingResponse = FullMessagingResponse.fromGraph(callbackFullMessagingResponseGraph);
		} else {

			// this graph should be sent to WebSocketClient.onMessagingResponse() callback

			callbackMessagingResponse = LightMessagingResponse.fromGraph(graph);
		}

		// callbacks

		Callback callback = webSocketClient.getCallback();

		if (callback != null && callbackMessageEnvelope != null) {

			if (log.isDebugEnabled()) log.debug("Calling WebSocketClient.onMessageEnvelope() with " + callbackMessageEnvelope);
			callback.onMessageEnvelope(callbackMessageEnvelope);
		}

		if (callback != null && callbackMessagingResponse != null) {

			if (log.isDebugEnabled()) log.debug("Calling WebSocketClient.onMessagingResponse() with " + callbackMessagingResponse.getClass().getSimpleName() + ": " + callbackMessagingResponse.getGraph());
			callback.onMessagingResponse(callbackMessagingResponse);
		}

		for (FutureMessagingResponse callbackFutureMessagingResponse : callbackFutureMessagingResponses) {

			XDIAddress callbackFutureMessageXDIAddress = callbackFutureMessageXDIAddresses.get(callbackFutureMessagingResponse);
			FullMessagingResponse callbackFutureFullMessagingResponse = callbackFutureFullMessagingResponses.get(callbackFutureMessagingResponse);

			if (log.isDebugEnabled()) log.debug("Calling FutureMessagingResponse.onMessagingResponse() with " + callbackFutureMessageXDIAddress + " and " + callbackFutureFullMessagingResponse.getClass().getSimpleName() + ": " + callbackFutureFullMessagingResponse.getGraph());
			callbackFutureMessagingResponse.onMessagingResponse(callbackFutureMessageXDIAddress, callbackFutureFullMessagingResponse);
		}
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

	private static Graph read(Session session, Reader reader) throws IOException {

		// try to find an appropriate reader for the provided mime type

		XDIReader xdiReader = null;

		String contentType = session.getNegotiatedSubprotocol();
		MimeType recvMimeType = contentType != null ? new MimeType(contentType) : null;
		xdiReader = recvMimeType != null ? XDIReaderRegistry.forMimeType(recvMimeType) : null;

		if (xdiReader == null) xdiReader = XDIReaderRegistry.getDefault();

		// read everything into an in-memory XDI graph (a message envelope)

		if (log.isDebugEnabled()) log.debug("Reading message in " + recvMimeType + " with reader " + xdiReader.getClass().getSimpleName() + ".");

		Graph graph;

		try {

			graph = MemoryGraphFactory.getInstance().openGraph();

			xdiReader.read(graph, reader);
		} catch (IOException ex) {

			throw ex;
		} catch (Exception ex) {

			log.error("Cannot parse XDI graph: " + ex.getMessage(), ex);
			throw new IOException("Cannot parse XDI graph: " + ex.getMessage(), ex);
		} finally {

			reader.close();
		}

		if (log.isDebugEnabled()) log.debug("Graph received: " + graph);

		// done

		return graph;
	}
}
