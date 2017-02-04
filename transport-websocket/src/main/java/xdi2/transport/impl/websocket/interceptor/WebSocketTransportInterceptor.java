package xdi2.transport.impl.websocket.interceptor;

import java.io.IOException;

import xdi2.messaging.container.interceptor.Interceptor;
import xdi2.transport.Transport;
import xdi2.transport.exceptions.Xdi2TransportException;
import xdi2.transport.impl.websocket.WebSocketTransport;
import xdi2.transport.impl.websocket.WebSocketTransportRequest;
import xdi2.transport.impl.websocket.WebSocketTransportResponse;
import xdi2.transport.registry.impl.uri.UriMessagingContainerMount;

/**
 * Interceptor that is executed when it receives an incoming message.
 * 
 * @author markus
 */
public interface WebSocketTransportInterceptor extends Interceptor<Transport<?, ?>> {

	/**
	 * Run when the WebSocket transport receives a message.
	 * @return True, if the request has been fully handled.
	 */
	public boolean processMessage(WebSocketTransport webSocketTransport, WebSocketTransportRequest request, WebSocketTransportResponse response, UriMessagingContainerMount messagingContainerMount) throws Xdi2TransportException, IOException;
}
