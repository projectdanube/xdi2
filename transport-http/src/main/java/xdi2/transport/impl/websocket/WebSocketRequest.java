package xdi2.transport.impl.websocket;

import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.websocket.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.transport.Request;

/**
 * This class represents a WebSocket request to the server.
 * This is used by the WebSocketTransport.
 * 
 * @author markus
 */
public class WebSocketRequest implements Request {

	private static final Logger log = LoggerFactory.getLogger(WebSocketRequest.class);

	private String requestPath;
	private String subprotocol;
	private Reader reader;

	private WebSocketRequest(String requestPath, String subprotocol, Reader reader) {

		this.requestPath = requestPath;
		this.subprotocol = subprotocol;
		this.reader = reader;
	}

	public static WebSocketRequest create(Session session, Reader reader) {

		String requestUri = session.getRequestURI().toString();
		if (log.isDebugEnabled()) log.debug("Request URI: " + requestUri);

		String requestPath = requestUri;

		try {

			requestPath = URLDecoder.decode(requestPath, "UTF-8");
		} catch (UnsupportedEncodingException ex) {

			throw new RuntimeException(ex.getMessage(), ex);
		}

		String subprotocol = session.getNegotiatedSubprotocol();

		return new WebSocketRequest(requestPath, subprotocol, reader);
	}

	/*
	 * Getters and setters
	 */

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
