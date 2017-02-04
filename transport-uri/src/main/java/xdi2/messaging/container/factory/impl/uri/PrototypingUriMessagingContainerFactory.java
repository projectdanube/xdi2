package xdi2.messaging.container.factory.impl.uri;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.features.nodetypes.XdiPeerRoot;
import xdi2.core.syntax.XDIAddress;
import xdi2.messaging.container.MessagingContainer;
import xdi2.messaging.container.Prototype;
import xdi2.messaging.container.Prototype.PrototypingContext;
import xdi2.messaging.container.contributor.ContributorMap;
import xdi2.messaging.container.exceptions.Xdi2MessagingException;
import xdi2.messaging.container.impl.AbstractMessagingContainer;
import xdi2.messaging.container.interceptor.InterceptorList;
import xdi2.transport.exceptions.Xdi2TransportException;
import xdi2.transport.registry.impl.uri.UriMessagingContainerRegistry;

public abstract class PrototypingUriMessagingContainerFactory extends UriMessagingContainerFactory {

	private static final Logger log = LoggerFactory.getLogger(PrototypingUriMessagingContainerFactory.class);

	private MessagingContainer prototypeMessagingContainer;

	@SuppressWarnings("unchecked")
	public MessagingContainer mountMessagingContainer(UriMessagingContainerRegistry uriMessagingContainerRegistry, String messagingContainerPath, XDIAddress ownerXDIAddress, XdiPeerRoot ownerPeerRoot, ContextNode ownerContextNode) throws Xdi2MessagingException, Xdi2TransportException {

		if (log.isDebugEnabled()) log.debug("messagingContainerPath=" + messagingContainerPath + ", ownerXDIAddress=" + ownerXDIAddress + ", ownerPeerRoot=" + ownerPeerRoot + ", ownerContextNode=" + ownerContextNode);

		// create new messaging target

		if (! (this.getPrototypeMessagingContainer() instanceof Prototype<?>)) {

			throw new Xdi2MessagingException("Cannot use messaging target " + this.getPrototypeMessagingContainer().getClass().getSimpleName() + " as prototype.", null, null);
		}

		PrototypingContext prototypingContext = new PrototypingContext(ownerXDIAddress, ownerPeerRoot, ownerContextNode);

		Prototype<? extends MessagingContainer> messagingContainerPrototype; 
		MessagingContainer prototypedMessagingContainer;

		try {

			messagingContainerPrototype = (Prototype<? extends MessagingContainer>) this.getPrototypeMessagingContainer();
			prototypedMessagingContainer = prototypingContext.instanceFor(messagingContainerPrototype);
		} catch (Xdi2MessagingException ex) {

			throw new Xdi2MessagingException("Cannot instantiate messaging target for prototype " + this.getPrototypeMessagingContainer().getClass().getSimpleName() + ": " + ex.getMessage(), ex, null);
		}

		// set the interceptor list

		if (messagingContainerPrototype instanceof AbstractMessagingContainer && prototypedMessagingContainer instanceof AbstractMessagingContainer) {

			InterceptorList<MessagingContainer> interceptorListPrototype = ((AbstractMessagingContainer) messagingContainerPrototype).getInterceptors();
			InterceptorList<MessagingContainer> prototypedInterceptorList = prototypingContext.instanceFor(interceptorListPrototype);

			((AbstractMessagingContainer) prototypedMessagingContainer).setInterceptors(prototypedInterceptorList);
		}

		// set the contributor map

		if (messagingContainerPrototype instanceof AbstractMessagingContainer && prototypedMessagingContainer instanceof AbstractMessagingContainer) {

			ContributorMap contributorMapPrototype = ((AbstractMessagingContainer) messagingContainerPrototype).getContributors();
			ContributorMap prototypedContributorMap = prototypingContext.instanceFor(contributorMapPrototype);

			((AbstractMessagingContainer) prototypedMessagingContainer).setContributors(prototypedContributorMap);
		}

		// mount the new messaging target

		uriMessagingContainerRegistry.mountMessagingContainer(messagingContainerPath, prototypedMessagingContainer);

		// done

		return prototypedMessagingContainer;
	}

	public MessagingContainer getPrototypeMessagingContainer() {

		return this.prototypeMessagingContainer;
	}

	public void setPrototypeMessagingContainer(MessagingContainer prototypeMessagingContainer) {

		this.prototypeMessagingContainer = prototypeMessagingContainer;
	}
}
