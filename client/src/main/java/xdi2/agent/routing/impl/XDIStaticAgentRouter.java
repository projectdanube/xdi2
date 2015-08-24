package xdi2.agent.routing.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.agent.routing.XDIAgentRouter;
import xdi2.client.XDIClient;
import xdi2.client.XDIClientRoute;
import xdi2.client.exceptions.Xdi2AgentException;
import xdi2.core.syntax.XDIArc;
import xdi2.messaging.response.MessagingResponse;

public class XDIStaticAgentRouter extends XDIAbstractAgentRouter<XDIClientRoute<? extends XDIClient<? extends MessagingResponse>>, XDIClient<? extends MessagingResponse>> implements XDIAgentRouter<XDIClientRoute<? extends XDIClient<? extends MessagingResponse>>, XDIClient<? extends MessagingResponse>> {

	private static final Logger log = LoggerFactory.getLogger(XDIStaticAgentRouter.class);

	private XDIArc toPeerRootXDIArc;
	private XDIClientRoute<? extends XDIClient<? extends MessagingResponse>> xdiClientRoute;

	public XDIStaticAgentRouter(XDIArc toPeerRootXDIArc, XDIClientRoute<? extends XDIClient<? extends MessagingResponse>> xdiClientRoute) {

		this.toPeerRootXDIArc = toPeerRootXDIArc;
		this.xdiClientRoute = xdiClientRoute;
	}

	public XDIStaticAgentRouter() {

		this.toPeerRootXDIArc = null;
		this.xdiClientRoute = null;
	}

	@Override
	protected XDIClientRoute<? extends XDIClient<? extends MessagingResponse>> routeInternal(XDIArc toPeerRootXDIArc) throws Xdi2AgentException {

		// check if we can provide the TO peer root

		if (this.getToPeerRootXDIArc() != null) {

			if (toPeerRootXDIArc == null) {

				if (log.isDebugEnabled()) log.debug("Cannot route to unknown peer root. Skipping.");
				return null;
			}

			if (! toPeerRootXDIArc.equals(this.getToPeerRootXDIArc())) {

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

	public XDIClientRoute<? extends XDIClient<? extends MessagingResponse>> getXdiClientRoute() {

		return this.xdiClientRoute;
	}

	public void setXdiClientRoute(XDIClientRoute<? extends XDIClient<? extends MessagingResponse>> xdiClientRoute) {

		this.xdiClientRoute = xdiClientRoute;
	}
}
