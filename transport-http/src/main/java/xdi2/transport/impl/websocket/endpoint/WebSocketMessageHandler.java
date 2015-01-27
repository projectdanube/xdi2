package xdi2.transport.impl.websocket.endpoint;

import java.io.Reader;

import javax.websocket.Session;

import xdi2.transport.impl.websocket.WebSocketRequest;
import xdi2.transport.impl.websocket.WebSocketResponse;
import xdi2.transport.impl.websocket.WebSocketTransport;

public class WebSocketMessageHandler implements javax.websocket.MessageHandler.Whole<Reader> {

	private Session session;
	private WebSocketTransport webSocketTransport;

	public WebSocketMessageHandler(Session session, WebSocketTransport webSocketTransport) {

		this.session = session;
		this.webSocketTransport = webSocketTransport;
	}

	@Override
	public void onMessage(Reader reader) {

		WebSocketRequest request = WebSocketRequest.create(this.getSession(), reader);
		WebSocketResponse response = WebSocketResponse.create(this.getSession());

		this.getWebSocketTransport().doMessage(request, response);
	}

	/*
	 * Getters and setters
	 */

	public Session getSession() {

		return this.session;
	}

	public void setSession(Session session) {

		this.session = session;
	}

	public WebSocketTransport getWebSocketTransport() {

		return this.webSocketTransport;
	}

	public void setWebSocketTransport(WebSocketTransport webSocketTransport) {

		this.webSocketTransport = webSocketTransport;
	}
}
