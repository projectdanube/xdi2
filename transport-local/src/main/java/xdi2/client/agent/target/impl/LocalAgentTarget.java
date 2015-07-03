package xdi2.client.agent.target.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.client.agent.target.AgentRoute;
import xdi2.client.agent.target.AgentTarget;
import xdi2.client.exceptions.Xdi2AgentException;
import xdi2.core.Graph;
import xdi2.core.syntax.XDIArc;
import xdi2.core.util.GraphUtil;

public class LocalAgentTarget implements AgentTarget {

	private static final Logger log = LoggerFactory.getLogger(LocalAgentTarget.class);

	private Graph graph;

	@Override
	public AgentRoute route(XDIArc targetPeerRootXDIArc) throws Xdi2AgentException {

		// check if we can provide the target peer root

		XDIArc ownerPeerRootXDIArc = GraphUtil.getOwnerPeerRootXDIArc(this.getGraph());

		if (ownerPeerRootXDIArc == null) {

			log.debug("Graph does not have target peer root " + targetPeerRootXDIArc + ". Skipping.");
			return null;
		}

		if (! ownerPeerRootXDIArc.equals(targetPeerRootXDIArc)) {

			log.debug("Graph owner peer root " + ownerPeerRootXDIArc + " does not match target peer root " + targetPeerRootXDIArc + ". Skipping.");
			return null;
		}

		// construct the route

		AgentRoute route = new LocalAgentRoute(this.getGraph(), ownerPeerRootXDIArc);

		// done

		return route;
	}

	/*
	 * Getters and setters
	 */

	public Graph getGraph() {

		return this.graph;
	}

	public void setGraph(Graph graph) {

		this.graph = graph;
	}
}
