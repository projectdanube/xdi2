package xdi2.agent.routing.impl.bootstrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.agent.routing.XDIAgentRouter;
import xdi2.agent.routing.impl.XDIAbstractAgentRouter;
import xdi2.client.exceptions.Xdi2AgentException;
import xdi2.client.impl.local.XDILocalClient;
import xdi2.client.impl.local.XDILocalClientRoute;
import xdi2.core.bootstrap.XDIBootstrap;
import xdi2.core.constants.XDIConstants;
import xdi2.core.syntax.XDIArc;

public class XDIBootstrapAgentRouter extends XDIAbstractAgentRouter<XDILocalClientRoute, XDILocalClient> implements XDIAgentRouter<XDILocalClientRoute, XDILocalClient> {

	private static final Logger log = LoggerFactory.getLogger(XDIBootstrapAgentRouter.class);

	@Override
	protected XDILocalClientRoute routeInternal(XDIArc toPeerRootXDIArc) throws Xdi2AgentException {

		// check if we can provide the TO peer root

		if (toPeerRootXDIArc == null) {

			if (log.isDebugEnabled()) log.debug("Cannot route to unknown peer root. Skipping.");
			return null;
		}

		if (! XDIArc.create("($)").equals(toPeerRootXDIArc)) {

			if (log.isDebugEnabled()) log.debug("Bootstrap graph is no route to peer root " + toPeerRootXDIArc + ". Skipping.");
			return null;
		}

		// construct the route

		XDILocalClientRoute route = new XDILocalClientRoute(toPeerRootXDIArc, XDIBootstrap.BOOTSTRAP_GRAPH);

		// done

		return route;
	}

	@Override
	protected XDIArc overrideToPeerRootXDIArc(XDIArc toPeerRootXDIArc) {

		if (! XDIConstants.CS_CLASS_RESERVED.equals(toPeerRootXDIArc.getCs())) return null;

		return XDIArc.create("($)");	// TODO: create constant somewhere
	}
}
