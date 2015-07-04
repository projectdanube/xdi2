package xdi2.agent.routing.impl;

import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.agent.routing.XDIAgentRouter;
import xdi2.client.exceptions.Xdi2AgentException;
import xdi2.client.impl.http.XDIHttpClient;
import xdi2.client.impl.http.XDIHttpClientRoute;
import xdi2.core.syntax.XDIArc;

public class XDIWebSocketAgentRouter implements XDIAgentRouter<XDIHttpClientRoute, XDIHttpClient> {

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
	public XDIHttpClientRoute route(XDIArc toPeerRootXDIArc) throws Xdi2AgentException {

		// check if we can provide the TO peer root

		if (! "https".equalsIgnoreCase(this.getXdiWebSocketEndpointUrl().getProtocol()) && ! "http".equalsIgnoreCase(this.getXdiWebSocketEndpointUrl().getProtocol())) {

			if (log.isDebugEnabled()) log.debug("No HTTP(s) URL: " + this.getXdiWebSocketEndpointUrl() + ". Skipping.");
			return null;
		}

		if (! this.getToPeerRootXDIArc().equals(toPeerRootXDIArc)) {

			if (log.isDebugEnabled()) log.debug("HTTP(s) URL " + this.getXdiWebSocketEndpointUrl() + " does not have target peer root " + toPeerRootXDIArc + ". Skipping.");
			return null;
		}

		// construct the route

		XDIHttpClientRoute route = new XDIHttpClientRoute(this.toPeerRootXDIArc, this.xdiWebSocketEndpointUrl);

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
