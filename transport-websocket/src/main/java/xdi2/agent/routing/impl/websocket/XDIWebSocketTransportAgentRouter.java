package xdi2.agent.routing.impl.websocket;

import javax.websocket.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.agent.routing.XDIAgentRouter;
import xdi2.agent.routing.impl.XDIAbstractAgentRouter;
import xdi2.client.exceptions.Xdi2AgentException;
import xdi2.client.impl.websocket.XDIWebSocketClient;
import xdi2.client.impl.websocket.XDIWebSocketClientRoute;
import xdi2.core.syntax.XDIArc;
import xdi2.transport.impl.websocket.WebSocketTransport;

public class XDIWebSocketTransportAgentRouter extends XDIAbstractAgentRouter<XDIWebSocketClientRoute, XDIWebSocketClient> implements XDIAgentRouter<XDIWebSocketClientRoute, XDIWebSocketClient> {

	private static final Logger log = LoggerFactory.getLogger(XDIWebSocketTransportAgentRouter.class);

	private WebSocketTransport webSocketTransport;

	@Override
	protected XDIWebSocketClientRoute routeInternal(XDIArc toPeerRootXDIArc) throws Xdi2AgentException {

		// check if we can provide the TO peer root

		if (toPeerRootXDIArc == null) {

			if (log.isDebugEnabled()) log.debug("Cannot route to unknown peer root. Skipping.");
			return null;
		}

		Session session = this.getWebSocketTransport().findSession(toPeerRootXDIArc);

		if (session == null) {

			log.debug("WebSocket sessions have no route to peer root " + toPeerRootXDIArc + ". Skipping.");
			return null;
		}

		// construct the route

		XDIWebSocketClientRoute route = new XDIWebSocketClientRoute(toPeerRootXDIArc, session, null);

		// done

		return route;
	}

	/*
	 * Getters and setters
	 */

	public WebSocketTransport getWebSocketTransport() {

		return this.webSocketTransport;
	}

	public void setWebSocketTransport(WebSocketTransport messagingTargetRegistry) {

		this.webSocketTransport = messagingTargetRegistry;
	}
}
