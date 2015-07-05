package xdi2.transport.impl.websocket;

import javax.websocket.RemoteEndpoint.Async;
import javax.websocket.Session;

import xdi2.transport.TransportResponse;
import xdi2.transport.impl.websocket.endpoint.WebSocketServerMessageHandler;

/**
 * This class represents a WebSocket response from the server.
 * This is used by the WebSocketTransport.
 * 
 * @author markus
 */
public class WebSocketTransportResponse implements TransportResponse {

	private WebSocketServerMessageHandler webSocketMessageHandler;
	private Async async;

	private WebSocketTransportResponse(WebSocketServerMessageHandler webSocketMessageHandler, Async async) {

		this.webSocketMessageHandler = webSocketMessageHandler;
		this.async = async;
	}

	public static WebSocketTransportResponse create(WebSocketServerMessageHandler webSocketMessageHandler, Session session) {

		Async async = session.getAsyncRemote();

		return new WebSocketTransportResponse(webSocketMessageHandler, async);
	}

	/*
	 * Getters and setters
	 */

	public WebSocketServerMessageHandler getWebSocketMessageHandler() {

		return this.webSocketMessageHandler;
	}

	public Async getAsync() {

		return this.async;
	}
}
