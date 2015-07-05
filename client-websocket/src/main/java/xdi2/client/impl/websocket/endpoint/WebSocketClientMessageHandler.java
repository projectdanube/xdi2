package xdi2.client.impl.websocket.endpoint;

import java.io.Reader;

import javax.websocket.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

// TODO		XDIWebSocketClient webSocketClient = (XDIWebSocketClient) this.getSession().getUserProperties().get("xdiWebSocketClient");
	}

	/*
	 * Getters and setters
	 */

	public Session getSession() {

		return this.session;
	}
}
