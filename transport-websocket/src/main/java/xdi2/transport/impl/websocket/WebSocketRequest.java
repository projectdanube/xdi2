package xdi2.transport.impl.websocket;

import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.websocket.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.transport.TransportRequest;
import xdi2.transport.impl.websocket.endpoint.WebSocketServerMessageHandler;

/**
 * This class represents a WebSocket request to the server.
 * This is used by the WebSocketTransport.
 * 
 * @author markus
 */
public class WebSocketRequest implements TransportRequest {

	private static final Logger log = LoggerFactory.getLogger(WebSocketRequest.class);

	private WebSocketServerMessageHandler webSocketMessageHandler;
	private String requestPath;
	private String subprotocol;
	private Reader reader;

	private WebSocketRequest(WebSocketServerMessageHandler webSocketMessageHandler, String requestPath, String subprotocol, Reader reader) {

		this.webSocketMessageHandler = webSocketMessageHandler;
		this.requestPath = requestPath;
		this.subprotocol = subprotocol;
		this.reader = reader;
	}

	public static WebSocketRequest create(WebSocketServerMessageHandler webSocketMessageHandler, Session session, String contextPath, String endpointPath, Reader reader) {

		String requestUri = session.getRequestURI().getPath();
		if (log.isDebugEnabled()) log.debug("Request URI: " + requestUri);

		String requestPath = requestUri.substring(contextPath.length() + endpointPath.length());
		if (! requestPath.startsWith("/")) requestPath = "/" + requestPath;

		try {

			requestPath = URLDecoder.decode(requestPath, "UTF-8");
		} catch (UnsupportedEncodingException ex) {

			throw new RuntimeException(ex.getMessage(), ex);
		}

		String subprotocol = session.getNegotiatedSubprotocol();

		return new WebSocketRequest(webSocketMessageHandler, requestPath, subprotocol, reader);
	}

	/*
	 * Getters and setters
	 */

	public WebSocketServerMessageHandler getWebSocketMessageHandler() {

		return this.webSocketMessageHandler;
	}

	public String getRequestPath() {

		return this.requestPath;
	}

	public String getSubprotocol() {

		return this.subprotocol;
	}

	public Reader getReader() {

		return this.reader;
	}
}
