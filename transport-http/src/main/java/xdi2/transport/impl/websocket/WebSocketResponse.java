package xdi2.transport.impl.websocket;

import javax.websocket.RemoteEndpoint.Async;
import javax.websocket.Session;

import xdi2.transport.Response;

/**
 * This class represents a WebSocket response from the server.
 * This is used by the WebSocketTransport.
 * 
 * @author markus
 */
public class WebSocketResponse implements Response {

	private Async async;

	public WebSocketResponse(Async async) {

		this.async = async;
	}

	public WebSocketResponse() {

		this(null);
	}

	public static WebSocketResponse create(Session session) {

		WebSocketResponse webSocketResponse = new WebSocketResponse();
		webSocketResponse.setAsync(session.getAsyncRemote());

		return webSocketResponse;
	}

	/*
	 * Getters and setters
	 */

	public Async getAsync() {

		return this.async;
	}

	public void setAsync(Async async) {

		this.async = async;
	}
}
