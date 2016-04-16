package xdi2.transport.impl.websocket;

import javax.websocket.RemoteEndpoint.Async;
import javax.websocket.RemoteEndpoint.Basic;
import javax.websocket.Session;

import xdi2.transport.TransportResponse;
import xdi2.transport.impl.uri.UriTransportResponse;
import xdi2.transport.impl.websocket.endpoint.WebSocketServerMessageHandler;

/**
 * This class represents a WebSocket response from the server.
 * This is used by the WebSocketTransport.
 * 
 * @author markus
 */
public class WebSocketTransportResponse extends UriTransportResponse implements TransportResponse {

	private WebSocketServerMessageHandler webSocketMessageHandler;
	private Basic basic;
	private Async async;

	private WebSocketTransportResponse(WebSocketServerMessageHandler webSocketMessageHandler, Basic basic, Async async) {

		this.webSocketMessageHandler = webSocketMessageHandler;
		this.basic = basic;
		this.async = async;
	}

	public static WebSocketTransportResponse create(WebSocketServerMessageHandler webSocketMessageHandler, Session session) {

		Basic basic = session.getBasicRemote();
		Async async = session.getAsyncRemote();

		return new WebSocketTransportResponse(webSocketMessageHandler, basic, async);
	}

	/*
	 * Getters and setters
	 */

	public WebSocketServerMessageHandler getWebSocketMessageHandler() {

		return this.webSocketMessageHandler;
	}

	public Basic getBasic() {

		return this.basic;
	}

	public Async getAsync() {

		return this.async;
	}
}
