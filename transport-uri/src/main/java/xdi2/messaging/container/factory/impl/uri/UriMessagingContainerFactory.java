package xdi2.messaging.container.factory.impl.uri;

import java.util.Iterator;

import xdi2.core.syntax.XDIArc;
import xdi2.core.util.iterators.EmptyIterator;
import xdi2.messaging.container.MessagingContainer;
import xdi2.messaging.container.exceptions.Xdi2MessagingException;
import xdi2.messaging.container.factory.MessagingContainerFactory;
import xdi2.messaging.container.factory.impl.AbstractMessagingContainerFactory;
import xdi2.transport.exceptions.Xdi2TransportException;
import xdi2.transport.registry.impl.uri.UriMessagingContainerRegistry;

public abstract class UriMessagingContainerFactory extends AbstractMessagingContainerFactory implements MessagingContainerFactory {

	public UriMessagingContainerFactory() {

	}

	/*
	 * Maintenance methods
	 */

	/**
	 * Returns the request path at which this MessagingContainerFactory is able to
	 * mount a MessagingContainer with a given owner peer root XDI arc.
	 */
	public abstract String getRequestPath(String messagingContainerFactoryPath, XDIArc ownerPeerRootXDIArc);

	@Override
	public Iterator<XDIArc> getOwnerPeerRootXDIArcs() {

		return new EmptyIterator<XDIArc> ();
	}

	/**
	 * Mount a new MessagingContainer.
	 */
	public abstract MessagingContainer mountMessagingContainer(UriMessagingContainerRegistry uriMessagingContainerRegistry, String messagingContainerFactoryPath, String requestPath, boolean checkDisabled, boolean checkExpired) throws Xdi2TransportException, Xdi2MessagingException;

	public MessagingContainer mountMessagingContainer(UriMessagingContainerRegistry uriMessagingContainerRegistry, String messagingContainerFactoryPath, String requestPath) throws Xdi2TransportException, Xdi2MessagingException {

		return this.mountMessagingContainer(uriMessagingContainerRegistry, messagingContainerFactoryPath, requestPath, false, false);
	}

	/**
	 * Updates a MessagingContainer.
	 */
	public abstract MessagingContainer updateMessagingContainer(UriMessagingContainerRegistry uriMessagingContainerRegistry, String messagingContainerFactoryPath, String requestPath, boolean checkDisabled, boolean checkExpired, MessagingContainer messagingContainer) throws Xdi2TransportException, Xdi2MessagingException;

	public MessagingContainer updateMessagingContainer(UriMessagingContainerRegistry uriMessagingContainerRegistry, String messagingContainerFactoryPath, String requestPath, MessagingContainer messagingContainer) throws Xdi2TransportException, Xdi2MessagingException {

		return this.updateMessagingContainer(uriMessagingContainerRegistry, messagingContainerFactoryPath, requestPath, false, false, messagingContainer);
	}
}
