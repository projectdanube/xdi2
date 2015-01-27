package xdi2.transport.impl.websocket;

import java.io.Reader;

import javax.websocket.Session;
import javax.websocket.server.ServerEndpointConfig;

import xdi2.transport.Request;

/**
 * This class represents a WebSocket request to the server.
 * This is used by the WebSocketTransport.
 * 
 * @author markus
 */
public class WebSocketRequest implements Request {

	private String requestPath;
	private String subprotocol;
	private Reader reader;

	public WebSocketRequest(String requestPath, String subprotocol, Reader reader) {

		this.requestPath = requestPath;
		this.subprotocol = subprotocol;
		this.reader = reader;
	}

	public WebSocketRequest() {

		this(null, null, null);
	}

	public static WebSocketRequest create(ServerEndpointConfig serverEndpointConfig, Session session, Reader reader) {

		WebSocketRequest webSocketRequest = new WebSocketRequest();
		webSocketRequest.setRequestPath(serverEndpointConfig.getPath());
		webSocketRequest.setSubprotocol(session.getNegotiatedSubprotocol());
		webSocketRequest.setReader(reader);

		return webSocketRequest;
	}

	/*
	 * Getters and setters
	 */

	public String getRequestPath() {

		return this.requestPath;
	}

	public void setRequestPath(String requestPath) {

		this.requestPath = requestPath;
	}

	public String getSubprotocol() {

		return this.subprotocol;
	}

	public void setSubprotocol(String subprotocol) {

		this.subprotocol = subprotocol;
	}

	public Reader getReader() {

		return this.reader;
	}

	public void setReader(Reader reader) {

		this.reader = reader;
	}
}
