package xdi2.agent.routing.impl.local;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.agent.routing.XDIAgentRouter;
import xdi2.client.exceptions.Xdi2AgentException;
import xdi2.client.impl.local.XDILocalClient;
import xdi2.client.impl.local.XDILocalClientRoute;
import xdi2.core.Graph;
import xdi2.core.syntax.XDIArc;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.impl.graph.GraphMessagingTarget;

public class XDILocalAgentRouter implements XDIAgentRouter<XDILocalClientRoute, XDILocalClient> {

	private static final Logger log = LoggerFactory.getLogger(XDILocalAgentRouter.class);

	private MessagingTarget messagingTarget;

	public XDILocalAgentRouter(MessagingTarget messagingTarget) {

		this.messagingTarget = messagingTarget;
	}

	public XDILocalAgentRouter(Graph graph) {

		try {

			GraphMessagingTarget graphMessagingTarget = new GraphMessagingTarget();
			graphMessagingTarget.setGraph(graph);
			graphMessagingTarget.init();

			this.messagingTarget = graphMessagingTarget;
		} catch (Exception ex) {

			throw new RuntimeException("Cannot initialize messaging target: " + ex.getMessage(), ex);
		}
	}

	@Override
	public XDILocalClientRoute route(XDIArc toPeerRootXDIArc) throws Xdi2AgentException {

		// check if we can provide the TO peer root

		XDIArc ownerPeerRootXDIArc = this.getMessagingTarget().getOwnerPeerRootXDIArc();

		if (ownerPeerRootXDIArc != null) {

			if (! ownerPeerRootXDIArc.equals(toPeerRootXDIArc)) {

				if (log.isDebugEnabled()) log.debug("Local messaging target does not have target peer root " + toPeerRootXDIArc + " (" + ownerPeerRootXDIArc + "). Skipping.");
				return null;
			}
		}

		// construct the route

		XDILocalClientRoute route = new XDILocalClientRoute(toPeerRootXDIArc, this.getMessagingTarget());

		// done

		return route;
	}

	/*
	 * Getters and setters
	 */

	public MessagingTarget getMessagingTarget() {

		return this.messagingTarget;
	}

	public void setMessagingTarget(MessagingTarget messagingTargets) {

		this.messagingTarget = messagingTargets;
	}
}
