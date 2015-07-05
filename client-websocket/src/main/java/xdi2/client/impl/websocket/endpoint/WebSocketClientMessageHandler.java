package xdi2.client.impl.websocket.endpoint;

import java.io.Reader;
import java.net.URL;

import javax.websocket.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.client.impl.websocket.XDIWebSocketClient;

public class WebSocketClientMessageHandler implements javax.websocket.MessageHandler.Whole<Reader> {

	private static final Logger log = LoggerFactory.getLogger(WebSocketClientMessageHandler.class);

	private Session session;
	private XDIWebSocketClient xdiWebSocketClient;
	private URL xdiWebSocketEndpointUrl;

	public WebSocketClientMessageHandler(Session session, XDIWebSocketClient xdiWebSocketClient, URL xdiWebSocketEndpointUrl) {

		this.session = session;
		this.xdiWebSocketClient = xdiWebSocketClient;
		this.xdiWebSocketEndpointUrl = xdiWebSocketEndpointUrl;
	}

	@Override
	public void onMessage(Reader reader) {

	}

	/*
	 * Getters and setters
	 */

	public Session getSession() {

		return this.session;
	}

	public XDIWebSocketClient getXdiWebSocketClient() {

		return this.xdiWebSocketClient;
	}

	public URL getXdiWebSocketEndpointUrl() {

		return this.xdiWebSocketEndpointUrl;
	}
}
