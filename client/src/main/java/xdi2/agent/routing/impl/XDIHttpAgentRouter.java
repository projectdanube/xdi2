package xdi2.agent.routing.impl;

import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.agent.routing.XDIAgentRouter;
import xdi2.client.exceptions.Xdi2AgentException;
import xdi2.client.impl.http.XDIHttpClient;
import xdi2.client.impl.http.XDIHttpClientRoute;
import xdi2.core.syntax.XDIArc;

public class XDIHttpAgentRouter implements XDIAgentRouter<XDIHttpClientRoute, XDIHttpClient> {

	private static final Logger log = LoggerFactory.getLogger(XDIHttpAgentRouter.class);

	private XDIArc toPeerRootXDIArc;
	private URL xdiEndpointUrl;

	public XDIHttpAgentRouter(XDIArc toPeerRootXDIArc, URL xdiEndpointUrl) {

		this.toPeerRootXDIArc = toPeerRootXDIArc;
		this.xdiEndpointUrl = xdiEndpointUrl;
	}

	public XDIHttpAgentRouter() {

		this.toPeerRootXDIArc = null;
		this.xdiEndpointUrl = null;
	}

	@Override
	public XDIHttpClientRoute route(XDIArc toPeerRootXDIArc) throws Xdi2AgentException {

		// check if we can provide the TO peer root

		if (! "https".equalsIgnoreCase(this.getXdiEndpointUrl().getProtocol()) && ! "http".equalsIgnoreCase(this.getXdiEndpointUrl().getProtocol())) {

			if (log.isDebugEnabled()) log.debug("No HTTP(S) URL: " + this.getXdiEndpointUrl() + ". Skipping.");
			return null;
		}

		if (! this.getToPeerRootXDIArc().equals(toPeerRootXDIArc)) {

			if (log.isDebugEnabled()) log.debug("HTTP(S) URL " + this.getXdiEndpointUrl() + " does not have target peer root " + toPeerRootXDIArc + ". Skipping.");
			return null;
		}

		// construct the route

		XDIHttpClientRoute route = new XDIHttpClientRoute(this.toPeerRootXDIArc, this.xdiEndpointUrl);

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

	public URL getXdiEndpointUrl() {

		return this.xdiEndpointUrl;
	}

	public void setXdiEndpointUrl(URL xdiEndpointUrl) {

		this.xdiEndpointUrl = xdiEndpointUrl;
	}
}
