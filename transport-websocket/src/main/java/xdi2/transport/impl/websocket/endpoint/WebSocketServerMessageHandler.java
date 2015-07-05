package xdi2.transport.impl.websocket.endpoint;

import java.io.IOException;
import java.io.Reader;

import javax.websocket.CloseReason;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.transport.impl.websocket.WebSocketRequest;
import xdi2.transport.impl.websocket.WebSocketResponse;
import xdi2.transport.impl.websocket.WebSocketTransport;

public class WebSocketServerMessageHandler implements javax.websocket.MessageHandler.Whole<Reader> {

	private static final Logger log = LoggerFactory.getLogger(WebSocketServerMessageHandler.class);

	private Session session;
	private WebSocketTransport webSocketTransport;
	private String contextPath;
	private String endpointPath;

	public WebSocketServerMessageHandler(Session session, WebSocketTransport webSocketTransport, String contextPath, String endpointPath) {

		this.session = session;
		this.webSocketTransport = webSocketTransport;
		this.contextPath = contextPath;
		this.endpointPath = endpointPath;
	}

	@Override
	public void onMessage(Reader reader) {

		WebSocketRequest request = WebSocketRequest.create(this, this.getSession(), this.getContextPath(), this.getEndpointPath(), reader);
		WebSocketResponse response = WebSocketResponse.create(this, this.getSession());

		try {

			this.getWebSocketTransport().execute(request, response);
		} catch (IOException ex) {

			try {

				log.error("I/O exception: " + ex.getMessage(), ex);
				this.getSession().close(new CloseReason(CloseCodes.UNEXPECTED_CONDITION, "I/O exception: " + ex.getMessage()));
			} catch (IOException ex2) {

				throw new Xdi2RuntimeException(ex2.getMessage(), ex2);
			}
		}
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
}
