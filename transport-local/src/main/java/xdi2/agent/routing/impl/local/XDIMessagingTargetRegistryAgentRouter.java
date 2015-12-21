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
import xdi2.core.exceptions.Xdi2Exception;
import xdi2.core.syntax.XDIArc;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.interceptor.Interceptor;
import xdi2.transport.Transport;
import xdi2.transport.registry.MessagingTargetMount;
import xdi2.transport.registry.MessagingTargetRegistry;

public class XDIMessagingTargetRegistryAgentRouter extends XDIAbstractAgentRouter<XDILocalClientRoute, XDILocalClient> implements XDIAgentRouter<XDILocalClientRoute, XDILocalClient> {

	private static final Logger log = LoggerFactory.getLogger(XDIMessagingTargetRegistryAgentRouter.class);

	private MessagingTargetRegistry messagingTargetRegistry;
	private Collection<Interceptor<Transport<?, ?>>> interceptors;

	public XDIMessagingTargetRegistryAgentRouter(MessagingTargetRegistry messagingTargetRegistry) {

		this.messagingTargetRegistry = messagingTargetRegistry;
		this.interceptors = new ArrayList<Interceptor<Transport<?, ?>>> ();
	}

	public XDIMessagingTargetRegistryAgentRouter() {

		this(null);
	}

	@Override
	protected XDILocalClientRoute routeInternal(XDIArc toPeerRootXDIArc) throws Xdi2AgentException {

		// check if we can provide the TO peer root

		if (toPeerRootXDIArc == null) {

			if (log.isDebugEnabled()) log.debug("Cannot route to unknown peer root. Skipping.");
			return null;
		}

		MessagingTargetMount messagingTargetMount;

		try {

			messagingTargetMount = this.getMessagingTargetRegistry().lookup(toPeerRootXDIArc);
		} catch (Xdi2Exception ex) {

			throw new Xdi2AgentException("Registry lookup problem: " + ex.getMessage(), ex);
		}

		MessagingTarget messagingTarget = messagingTargetMount == null ? null : messagingTargetMount.getMessagingTarget();

		if (messagingTarget == null) {

			log.debug("Messaging target registry " + this.getMessagingTargetRegistry().getClass().getSimpleName() + " is no route to peer root " + toPeerRootXDIArc + ". Skipping.");
			return null;
		}

		// construct the route

		XDILocalClientRoute route = new XDILocalClientRoute(toPeerRootXDIArc, messagingTarget);

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

	public MessagingTargetRegistry getMessagingTargetRegistry() {

		return this.messagingTargetRegistry;
	}

	public void setMessagingTargetRegistry(MessagingTargetRegistry messagingTargetRegistry) {

		this.messagingTargetRegistry = messagingTargetRegistry;
	}

	public Collection<Interceptor<Transport<?, ?>>> getInterceptors() {

		return this.interceptors;
	}

	public void setInterceptors(Collection<Interceptor<Transport<?, ?>>> interceptors) {

		this.interceptors = interceptors;
	}
}
