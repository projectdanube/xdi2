package xdi2.agent.routing.impl.local;

import java.util.Collections;
import java.util.List;

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

	private List<MessagingTarget> messagingTargets;

	public XDILocalAgentRouter(List<MessagingTarget> messagingTargets) {

		this.messagingTargets = messagingTargets;
	}

	public XDILocalAgentRouter(MessagingTarget messagingTarget) {

		this.messagingTargets = Collections.singletonList(messagingTarget);
	}

	public XDILocalAgentRouter(Graph graph) {

		try {

			GraphMessagingTarget messagingTarget = new GraphMessagingTarget();
			messagingTarget.setGraph(graph);
			messagingTarget.init();

			this.messagingTargets = Collections.singletonList((MessagingTarget) messagingTarget);
		} catch (Exception ex) {

			throw new RuntimeException("Cannot initialize messaging target: " + ex.getMessage(), ex);
		}
	}

	@Override
	public XDILocalClientRoute route(XDIArc targetPeerRootXDIArc) throws Xdi2AgentException {

		// check if we can provide the target peer root

		for (MessagingTarget messagingTarget : this.getMessagingTargets()) {

			XDIArc ownerPeerRootXDIArc = messagingTarget.getOwnerPeerRootXDIArc();

			if (! ownerPeerRootXDIArc.equals(targetPeerRootXDIArc)) continue;

			// construct the route

			XDILocalClientRoute route = new XDILocalClientRoute(messagingTarget);

			// done

			return route;
		}

		// done

		log.debug("No messaging target for target peer root " + targetPeerRootXDIArc + ". Skipping.");
		return null;
	}

	/*
	 * Getters and setters
	 */

	public List<MessagingTarget> getMessagingTargets() {

		return this.messagingTargets;
	}

	public void setMessagingTargets(List<MessagingTarget> messagingTargets) {

		this.messagingTargets = messagingTargets;
	}
}
