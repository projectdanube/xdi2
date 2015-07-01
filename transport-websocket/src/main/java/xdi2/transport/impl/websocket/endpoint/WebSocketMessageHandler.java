package xdi2.transport.impl.websocket.endpoint;

import java.io.Reader;

import javax.websocket.Session;

import xdi2.transport.impl.websocket.WebSocketRequest;
import xdi2.transport.impl.websocket.WebSocketResponse;
import xdi2.transport.impl.websocket.WebSocketTransport;
import xdi2.transport.impl.websocket.WebSocketTransport.WebSocketWriteListener;

public class WebSocketMessageHandler implements javax.websocket.MessageHandler.Whole<Reader> {

	private Session session;
	private WebSocketTransport webSocketTransport;
	private String contextPath;
	private String endpointPath;

	private WebSocketWriteListener webSocketWriteListener;

	public WebSocketMessageHandler(Session session, WebSocketTransport webSocketTransport, String contextPath, String endpointPath) {

		this.session = session;
		this.webSocketTransport = webSocketTransport;
		this.contextPath = contextPath;
		this.endpointPath = endpointPath;

		this.webSocketWriteListener = null;
	}

	@Override
	public void onMessage(Reader reader) {

		WebSocketRequest request = WebSocketRequest.create(this, this.getSession(), this.getContextPath(), this.getEndpointPath(), reader);
		WebSocketResponse response = WebSocketResponse.create(this, this.getSession());

		this.getWebSocketTransport().doMessage(request, response);
	}

	/*
	 * Getters and setters
	 */

	public Session getSession() {

		return this.session;
	}

	public WebSocketTransport getWebSocketTransport() {

		return this.webSocketTransport;
	}

	public String getContextPath() {

		return this.contextPath;
	}

	public String getEndpointPath() {

		return this.endpointPath;
	}

	public WebSocketWriteListener getWebSocketWriteListener() {

		return this.webSocketWriteListener;
	}

	public void setWebSocketWriteListener(WebSocketWriteListener webSocketWriteListener) {

		this.webSocketWriteListener = webSocketWriteListener;
	}
}
