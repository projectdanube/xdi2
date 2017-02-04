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
import xdi2.messaging.container.MessagingContainer;
import xdi2.messaging.container.interceptor.Interceptor;
import xdi2.transport.Transport;
import xdi2.transport.registry.MessagingContainerMount;
import xdi2.transport.registry.MessagingContainerRegistry;

public class XDIMessagingContainerRegistryAgentRouter extends XDIAbstractAgentRouter<XDILocalClientRoute, XDILocalClient> implements XDIAgentRouter<XDILocalClientRoute, XDILocalClient> {

	private static final Logger log = LoggerFactory.getLogger(XDIMessagingContainerRegistryAgentRouter.class);

	private MessagingContainerRegistry messagingContainerRegistry;
	private Collection<Interceptor<Transport<?, ?>>> interceptors;

	public XDIMessagingContainerRegistryAgentRouter(MessagingContainerRegistry messagingContainerRegistry) {

		this.messagingContainerRegistry = messagingContainerRegistry;
		this.interceptors = new ArrayList<Interceptor<Transport<?, ?>>> ();
	}

	public XDIMessagingContainerRegistryAgentRouter() {

		this(null);
	}

	@Override
	protected XDILocalClientRoute routeInternal(XDIArc toPeerRootXDIArc) throws Xdi2AgentException {

		// check if we can provide the TO peer root

		if (toPeerRootXDIArc == null) {

			if (log.isDebugEnabled()) log.debug("Cannot route to unknown peer root. Skipping.");
			return null;
		}

		MessagingContainerMount messagingContainerMount;

		try {

			messagingContainerMount = this.getMessagingContainerRegistry().lookup(toPeerRootXDIArc);
		} catch (Xdi2Exception ex) {

			throw new Xdi2AgentException("Registry lookup problem: " + ex.getMessage(), ex);
		}

		MessagingContainer messagingContainer = messagingContainerMount == null ? null : messagingContainerMount.getMessagingContainer();

		if (messagingContainer == null) {

			log.debug("Messaging target registry " + this.getMessagingContainerRegistry().getClass().getSimpleName() + " is no route to peer root " + toPeerRootXDIArc + ". Skipping.");
			return null;
		}

		// construct the route

		XDILocalClientRoute route = new XDILocalClientRoute(toPeerRootXDIArc, messagingContainer);

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

	public MessagingContainerRegistry getMessagingContainerRegistry() {

		return this.messagingContainerRegistry;
	}

	public void setMessagingContainerRegistry(MessagingContainerRegistry messagingContainerRegistry) {

		this.messagingContainerRegistry = messagingContainerRegistry;
	}

	public Collection<Interceptor<Transport<?, ?>>> getInterceptors() {

		return this.interceptors;
	}

	public void setInterceptors(Collection<Interceptor<Transport<?, ?>>> interceptors) {

		this.interceptors = interceptors;
	}
}
