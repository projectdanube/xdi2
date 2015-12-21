package xdi2.agent.routing.impl.local;

import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.agent.routing.XDIAgentRouter;
import xdi2.agent.routing.impl.XDIAbstractAgentRouter;
import xdi2.client.exceptions.Xdi2AgentException;
import xdi2.client.impl.local.XDILocalClient;
import xdi2.client.impl.local.XDILocalClientRoute;
import xdi2.core.Graph;
import xdi2.core.syntax.XDIArc;
import xdi2.core.util.GraphUtil;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.interceptor.Interceptor;
import xdi2.transport.Transport;

public class XDILocalAgentRouter extends XDIAbstractAgentRouter<XDILocalClientRoute, XDILocalClient> implements XDIAgentRouter<XDILocalClientRoute, XDILocalClient> {

	private static final Logger log = LoggerFactory.getLogger(XDILocalAgentRouter.class);

	private MessagingTarget messagingTarget;
	private Graph graph;
	private Collection<Interceptor<Transport<?, ?>>> interceptors;

	public XDILocalAgentRouter(MessagingTarget messagingTarget, Graph graph) {

		this.messagingTarget = messagingTarget;
		this.graph = graph;
		this.interceptors = new ArrayList<Interceptor<Transport<?, ?>>> ();
	}

	public XDILocalAgentRouter(MessagingTarget messagingTarget) {

		this(messagingTarget, null);
	}

	public XDILocalAgentRouter(Graph graph) {

		this(null, graph);
	}

	public XDILocalAgentRouter() {

		this(null, null);
	}

	@Override
	protected XDILocalClientRoute routeInternal(XDIArc toPeerRootXDIArc) throws Xdi2AgentException {

		// check if we can provide the TO peer root

		if (toPeerRootXDIArc == null) {

			if (log.isDebugEnabled()) log.debug("Cannot route to unknown peer root. Skipping.");
			return null;
		}

		XDIArc ownerPeerRootXDIArc = null;
		if (ownerPeerRootXDIArc == null && this.getMessagingTarget() != null) ownerPeerRootXDIArc = this.getMessagingTarget().getOwnerPeerRootXDIArc();
		if (ownerPeerRootXDIArc == null && this.getGraph() != null) ownerPeerRootXDIArc = GraphUtil.getOwnerPeerRootXDIArc(this.getGraph());

		if (ownerPeerRootXDIArc == null) {

			if (log.isDebugEnabled()) log.debug("Cannot route to unknown owner peer root. Skipping.");
			return null;
		}

		if (! toPeerRootXDIArc.equals(ownerPeerRootXDIArc)) {

			if (log.isDebugEnabled()) log.debug("Local messaging target " + (this.getMessagingTarget() == null ? null : this.getMessagingTarget().getClass().getSimpleName()) + " and graph " + (this.getGraph() == null ? null : this.getGraph().getClass().getSimpleName()) + " is no route to peer root " + toPeerRootXDIArc + " (" + ownerPeerRootXDIArc + "). Skipping.");
			return null;
		}

		// construct the route

		XDILocalClientRoute route = new XDILocalClientRoute(toPeerRootXDIArc, this.getMessagingTarget(), this.getGraph());

		// add interceptors if supported

		if (this.getInterceptors() != null) {

			route.getInterceptors().addAll(this.getInterceptors());
		}

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

	public Graph getGraph() {

		return this.graph;
	}

	public void setGraph(Graph graph) {

		this.graph = graph;
	}

	public Collection<Interceptor<Transport<?, ?>>> getInterceptors() {

		return this.interceptors;
	}

	public void setInterceptors(Collection<Interceptor<Transport<?, ?>>> interceptors) {

		this.interceptors = interceptors;
	}
}
