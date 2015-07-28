package xdi2.agent.routing.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.agent.routing.XDIAgentRouter;
import xdi2.client.XDIClient;
import xdi2.client.XDIClientRoute;
import xdi2.client.exceptions.Xdi2AgentException;
import xdi2.core.syntax.XDIArc;

public class XDIStaticAgentRouter extends XDIAbstractAgentRouter<XDIClientRoute<XDIClient>, XDIClient> implements XDIAgentRouter<XDIClientRoute<XDIClient>, XDIClient> {

	private static final Logger log = LoggerFactory.getLogger(XDIStaticAgentRouter.class);

	private XDIArc toPeerRootXDIArc;
	private XDIClientRoute<XDIClient> xdiClientRoute;

	public XDIStaticAgentRouter(XDIArc toPeerRootXDIArc, XDIClientRoute<XDIClient> xdiClientRoute) {

		this.toPeerRootXDIArc = toPeerRootXDIArc;
		this.xdiClientRoute = xdiClientRoute;
	}

	public XDIStaticAgentRouter() {

		this.toPeerRootXDIArc = null;
		this.xdiClientRoute = null;
	}

	@Override
	protected XDIClientRoute<XDIClient> routeInternal(XDIArc toPeerRootXDIArc) throws Xdi2AgentException {

		// check if we can provide the TO peer root

		if (this.getToPeerRootXDIArc() != null) {

			if (! this.getToPeerRootXDIArc().equals(toPeerRootXDIArc)) {

				if (log.isDebugEnabled()) log.debug("Static route " + this.getXdiClientRoute() + " is no route to peer root " + toPeerRootXDIArc + " (" + this.getToPeerRootXDIArc() + "). Skipping.");
				return null;
			}
		}

		// done

		return this.getXdiClientRoute();
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

	public XDIClientRoute<XDIClient> getXdiClientRoute() {

		return this.xdiClientRoute;
	}

	public void setXdiClientRoute(XDIClientRoute<XDIClient> xdiClientRoute) {

		this.xdiClientRoute = xdiClientRoute;
	}
}
