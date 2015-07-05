package xdi2.transport.impl.websocket;

import java.io.IOException;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.messaging.target.interceptor.InterceptorList;
import xdi2.transport.Transport;
import xdi2.transport.exceptions.Xdi2TransportException;
import xdi2.transport.impl.websocket.interceptor.WebSocketTransportInterceptor;
import xdi2.transport.registry.impl.uri.UriMessagingTargetMount;

public class InterceptorExecutor {

	private static final Logger log = LoggerFactory.getLogger(InterceptorExecutor.class);

	private InterceptorExecutor() {

	}

	/*
	 * Methods for executing interceptors
	 */

	public static boolean executeWebSocketTransportInterceptorsMessage(InterceptorList<? extends Transport<?, ?>> interceptorList, WebSocketTransport webSocketTransport, WebSocketTransportRequest request, WebSocketTransportResponse response, UriMessagingTargetMount messagingTargetMount) throws Xdi2TransportException, IOException {

		for (Iterator<WebSocketTransportInterceptor> webSocketTransportInterceptors = findWebSocketTransportInterceptors(interceptorList); webSocketTransportInterceptors.hasNext(); ) {

			WebSocketTransportInterceptor webSocketTransportInterceptor = webSocketTransportInterceptors.next();

			if (webSocketTransportInterceptor.skip(null)) {

				if (log.isDebugEnabled()) log.debug("Skipping WebSocket transport interceptor " + webSocketTransportInterceptor.getClass().getSimpleName() + " (MESSAGE).");
				continue;
			}

			if (log.isDebugEnabled()) log.debug("Executing WebSocket transport interceptor " + webSocketTransportInterceptor.getClass().getSimpleName() + " (MESSAGE).");

			if (webSocketTransportInterceptor.processMessage(webSocketTransport, request, response, messagingTargetMount)) {

				if (log.isDebugEnabled()) log.debug("MESSAGE request has been fully handled by interceptor " + webSocketTransportInterceptor.getClass().getSimpleName() + ".");
				return true;
			}
		}

		return false;
	}

	/*
	 * Methods for finding interceptors
	 */

	public static Iterator<WebSocketTransportInterceptor> findWebSocketTransportInterceptors(InterceptorList<? extends Transport<?, ?>> interceptorList) {

		return interceptorList.findInterceptors(WebSocketTransportInterceptor.class);
	}
}
