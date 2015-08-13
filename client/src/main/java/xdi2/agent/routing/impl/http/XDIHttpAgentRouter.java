package xdi2.agent.routing.impl.http;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.agent.routing.XDIAgentRouter;
import xdi2.agent.routing.impl.XDIAbstractAgentRouter;
import xdi2.client.exceptions.Xdi2AgentException;
import xdi2.client.impl.http.XDIHttpClient;
import xdi2.client.impl.http.XDIHttpClientRoute;
import xdi2.core.syntax.XDIArc;

public class XDIHttpAgentRouter extends XDIAbstractAgentRouter<XDIHttpClientRoute, XDIHttpClient> implements XDIAgentRouter<XDIHttpClientRoute, XDIHttpClient> {

	private static final Logger log = LoggerFactory.getLogger(XDIHttpAgentRouter.class);

	private XDIArc toPeerRootXDIArc;
	private URI xdiEndpointUri;

	public XDIHttpAgentRouter(XDIArc toPeerRootXDIArc, URI xdiEndpointUri) {

		this.toPeerRootXDIArc = toPeerRootXDIArc;
		this.xdiEndpointUri = xdiEndpointUri;
	}

	public XDIHttpAgentRouter() {

		this.toPeerRootXDIArc = null;
		this.xdiEndpointUri = null;
	}

	@Override
	protected XDIHttpClientRoute routeInternal(XDIArc toPeerRootXDIArc) throws Xdi2AgentException {

		// check if we can provide the TO peer root

		if (toPeerRootXDIArc == null) {

			if (log.isDebugEnabled()) log.debug("Cannot route to unknown peer root. Skipping.");
			return null;
		}

		if (! toPeerRootXDIArc.equals(this.getToPeerRootXDIArc())) {

			if (log.isDebugEnabled()) log.debug("XDI endpoint " + this.getXdiEndpointUri() + " is no route to peer root " + toPeerRootXDIArc + " (" + this.getToPeerRootXDIArc() + "). Skipping.");
			return null;
		}

		// construct the route

		XDIHttpClientRoute route = new XDIHttpClientRoute(toPeerRootXDIArc, this.getXdiEndpointUri());

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

	public URI getXdiEndpointUri() {

		return this.xdiEndpointUri;
	}

	public void setXdiEndpointUri(URI xdiEndpointUri) {

		this.xdiEndpointUri = xdiEndpointUri;
	}
}
