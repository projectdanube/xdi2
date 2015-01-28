package xdi2.transport.impl.websocket;

import javax.websocket.RemoteEndpoint.Async;
import javax.websocket.Session;

import xdi2.transport.Response;
import xdi2.transport.impl.websocket.endpoint.WebSocketMessageHandler;

/**
 * This class represents a WebSocket response from the server.
 * This is used by the WebSocketTransport.
 * 
 * @author markus
 */
public class WebSocketResponse implements Response {

	private WebSocketMessageHandler webSocketMessageHandler;
	private Async async;

	private WebSocketResponse(WebSocketMessageHandler webSocketMessageHandler, Async async) {

		this.webSocketMessageHandler = webSocketMessageHandler;
		this.async = async;
	}

	public static WebSocketResponse create(WebSocketMessageHandler webSocketMessageHandler, Session session) {

		Async async = session.getAsyncRemote();

		return new WebSocketResponse(webSocketMessageHandler, async);
	}

	/*
	 * Getters and setters
	 */

	public WebSocketMessageHandler getWebSocketMessageHandler() {

		return this.webSocketMessageHandler;
	}

	public Async getAsync() {

		return this.async;
	}
}
