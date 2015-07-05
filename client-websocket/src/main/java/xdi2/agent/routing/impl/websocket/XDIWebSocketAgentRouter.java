package xdi2.agent.routing.impl.websocket;

import java.net.URL;

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
	private URL xdiWebSocketEndpointUrl;

	public XDIWebSocketAgentRouter(XDIArc toPeerRootXDIArc, URL xdiWebSocketEndpointUrl) {

		this.toPeerRootXDIArc = toPeerRootXDIArc;
		this.xdiWebSocketEndpointUrl = xdiWebSocketEndpointUrl;
	}

	public XDIWebSocketAgentRouter() {

		this.toPeerRootXDIArc = null;
		this.xdiWebSocketEndpointUrl = null;
	}

	@Override
	public XDIWebSocketClientRoute route(XDIArc toPeerRootXDIArc) throws Xdi2AgentException {

		// check if we can provide the TO peer root

		if (! "wss".equalsIgnoreCase(this.getXdiWebSocketEndpointUrl().getProtocol()) && ! "ws".equalsIgnoreCase(this.getXdiWebSocketEndpointUrl().getProtocol())) {

			if (log.isDebugEnabled()) log.debug("No WS(S) URL: " + this.getXdiWebSocketEndpointUrl() + ". Skipping.");
			return null;
		}

		if (! this.getToPeerRootXDIArc().equals(toPeerRootXDIArc)) {

			if (log.isDebugEnabled()) log.debug("WS(S) URL " + this.getXdiWebSocketEndpointUrl() + " does not have target peer root " + toPeerRootXDIArc + ". Skipping.");
			return null;
		}

		// construct the route

		XDIWebSocketClientRoute route = new XDIWebSocketClientRoute(this.toPeerRootXDIArc, null, this.xdiWebSocketEndpointUrl);

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

	public URL getXdiWebSocketEndpointUrl() {

		return this.xdiWebSocketEndpointUrl;
	}

	public void setXdiWebSocketEndpointUrl(URL xdiWebSocketEndpointUrl) {

		this.xdiWebSocketEndpointUrl = xdiWebSocketEndpointUrl;
	}
}
