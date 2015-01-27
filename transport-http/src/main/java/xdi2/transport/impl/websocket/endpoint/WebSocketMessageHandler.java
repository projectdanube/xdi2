package xdi2.transport.impl.websocket.endpoint;

import java.io.Reader;

import javax.websocket.Session;
import javax.websocket.server.ServerEndpointConfig;

import xdi2.transport.impl.http.registry.MessagingTargetMount;
import xdi2.transport.impl.websocket.WebSocketRequest;
import xdi2.transport.impl.websocket.WebSocketResponse;
import xdi2.transport.impl.websocket.WebSocketTransport;

public class WebSocketMessageHandler implements javax.websocket.MessageHandler.Whole<Reader> {

	private ServerEndpointConfig serverEndpointConfig;
	private Session session;
	private MessagingTargetMount messagingTargetMount;
	private WebSocketTransport webSocketTransport;

	public WebSocketMessageHandler(ServerEndpointConfig serverEndpointConfig, Session session, MessagingTargetMount messagingTargetMount, WebSocketTransport webSocketTransport) {

		this.serverEndpointConfig = serverEndpointConfig;
		this.session = session;
		this.messagingTargetMount = messagingTargetMount;
		this.webSocketTransport = webSocketTransport;
	}

	@Override
	public void onMessage(Reader reader) {

		WebSocketRequest request = WebSocketRequest.create(this.getServerEndpointConfig(), this.getSession(), reader);
		WebSocketResponse response = WebSocketResponse.create(this.getServerEndpointConfig(), this.getSession());

		this.getWebSocketTransport().doMessage(request, response);
	}

	/*
	 * Getters and setters
	 */

	public ServerEndpointConfig getServerEndpointConfig() {

		return this.serverEndpointConfig;
	}

	public void setServerEndpointConfig(ServerEndpointConfig serverEndpointConfig) {

		this.serverEndpointConfig = serverEndpointConfig;
	}

	public Session getSession() {

		return this.session;
	}

	public void setSession(Session session) {

		this.session = session;
	}

	public MessagingTargetMount getMessagingTargetMount() {

		return this.messagingTargetMount;
	}

	public void setMessagingTargetMount(MessagingTargetMount messagingTargetMount) {

		this.messagingTargetMount = messagingTargetMount;
	}

	public WebSocketTransport getWebSocketTransport() {

		return this.webSocketTransport;
	}

	public void setWebSocketTransport(WebSocketTransport webSocketTransport) {

		this.webSocketTransport = webSocketTransport;
	}
}
