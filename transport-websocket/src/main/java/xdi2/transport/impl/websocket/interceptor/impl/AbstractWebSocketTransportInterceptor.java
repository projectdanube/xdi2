package xdi2.transport.impl.websocket.interceptor.impl;

import java.io.IOException;

import xdi2.transport.exceptions.Xdi2TransportException;
import xdi2.transport.impl.websocket.WebSocketTransport;
import xdi2.transport.impl.websocket.WebSocketTransportRequest;
import xdi2.transport.impl.websocket.WebSocketTransportResponse;
import xdi2.transport.impl.websocket.interceptor.WebSocketTransportInterceptor;
import xdi2.transport.interceptor.impl.AbstractTransportInterceptor;
import xdi2.transport.registry.impl.uri.UriMessagingTargetMount;

public abstract class AbstractWebSocketTransportInterceptor extends AbstractTransportInterceptor implements WebSocketTransportInterceptor {

	@Override
	public boolean processMessage(WebSocketTransport webSocketTransport, WebSocketTransportRequest request, WebSocketTransportResponse response, UriMessagingTargetMount messagingTargetMount) throws Xdi2TransportException, IOException {

		return false;
	}
}
