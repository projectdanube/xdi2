package xdi2.agent.routing.impl.websocket;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.agent.routing.XDIAgentRouter;
import xdi2.client.exceptions.Xdi2AgentException;
import xdi2.client.impl.websocket.XDIWebSocketClient;
import xdi2.client.impl.websocket.XDIWebSocketClientRoute;
import xdi2.core.syntax.XDIArc;

public class XDIWebSocketAgentRouter implements XDIAgentRouter<XDIWebSocketClientRoute, XDIWebSocketClient> {

	private static final Logger log = LoggerFactory.getLogger(XDIWebSocketAgentRouter.class);

	private XDIArc toPeerRootXDIArc;
	private URI xdiWebSocketEndpointUri;

	public XDIWebSocketAgentRouter(XDIArc toPeerRootXDIArc, URI xdiWebSocketEndpointUri) {

		this.toPeerRootXDIArc = toPeerRootXDIArc;
		this.xdiWebSocketEndpointUri = xdiWebSocketEndpointUri;
	}

	public XDIWebSocketAgentRouter() {

		this.toPeerRootXDIArc = null;
		this.xdiWebSocketEndpointUri = null;
	}

	@Override
	public XDIWebSocketClientRoute route(XDIArc toPeerRootXDIArc) throws Xdi2AgentException {

		// check if we can provide the TO peer root

		if (! "wss".equalsIgnoreCase(this.getXdiWebSocketEndpointUri().getScheme()) && ! "ws".equalsIgnoreCase(this.getXdiWebSocketEndpointUri().getScheme())) {

			if (log.isDebugEnabled()) log.debug("No WS(S) URL: " + this.getXdiWebSocketEndpointUri() + ". Skipping.");
			return null;
		}

		if (this.getToPeerRootXDIArc() != null) {

			if (! this.getToPeerRootXDIArc().equals(toPeerRootXDIArc)) {

				if (log.isDebugEnabled()) log.debug("WS(S) URL " + this.getXdiWebSocketEndpointUri() + " does not have target peer root " + toPeerRootXDIArc + " (" + this.getToPeerRootXDIArc() + "). Skipping.");
				return null;
			}
		}

		// construct the route

		XDIWebSocketClientRoute route = new XDIWebSocketClientRoute(toPeerRootXDIArc, null, this.getXdiWebSocketEndpointUri());

		// done

		return route;
	}

	/*
	 * Getters and setters
	 */

	public XDIArc getToPeerRootXDIArc() {

		return this.toPeerRootXDIArc;
	}

	public void setToPeerRootXDIArc(XDIArc toPeerRootXDIArc) {

		this.toPeerRootXDIArc = toPeerRootXDIArc;
	}

	public URI getXdiWebSocketEndpointUri() {

		return this.xdiWebSocketEndpointUri;
	}

	public void setXdiWebSocketEndpointUri(URI xdiWebSocketEndpointUri) {

		this.xdiWebSocketEndpointUri = xdiWebSocketEndpointUri;
	}
}
