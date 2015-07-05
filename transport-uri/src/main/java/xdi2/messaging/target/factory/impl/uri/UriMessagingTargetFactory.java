package xdi2.messaging.target.factory.impl.uri;

import java.util.Iterator;

import xdi2.core.syntax.XDIArc;
import xdi2.core.util.iterators.EmptyIterator;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.factory.MessagingTargetFactory;
import xdi2.messaging.target.factory.impl.AbstractMessagingTargetFactory;
import xdi2.transport.exceptions.Xdi2TransportException;
import xdi2.transport.registry.impl.uri.UriMessagingTargetRegistry;

public abstract class UriMessagingTargetFactory extends AbstractMessagingTargetFactory implements MessagingTargetFactory {

	public UriMessagingTargetFactory() {

	}

	/*
	 * Maintenance methods
	 */

	/**
	 * Returns the request path at which this MessagingTargetFactory is able to
	 * mount a MessagingTarget with a given owner peer root XDI arc.
	 */
	public abstract String getRequestPath(String messagingTargetFactoryPath, XDIArc ownerPeerRootXDIArc);

	@Override
	public Iterator<XDIArc> getOwnerPeerRootXDIArcs() {

		return new EmptyIterator<XDIArc> ();
	}

	/**
	 * Mount a new MessagingTarget.
	 */
	public abstract MessagingTarget mountMessagingTarget(UriMessagingTargetRegistry uriMessagingTargetRegistry, String messagingTargetFactoryPath, String requestPath, boolean checkDisabled, boolean checkExpired) throws Xdi2TransportException, Xdi2MessagingException;

	public MessagingTarget mountMessagingTarget(UriMessagingTargetRegistry httpMessagingTargetRegistry, String messagingTargetFactoryPath, String requestPath) throws Xdi2TransportException, Xdi2MessagingException {

		return this.mountMessagingTarget(httpMessagingTargetRegistry, messagingTargetFactoryPath, requestPath, false, false);
	}

	/**
	 * Updates a MessagingTarget.
	 */
	public abstract MessagingTarget updateMessagingTarget(UriMessagingTargetRegistry uriMessagingTargetRegistry, String messagingTargetFactoryPath, String requestPath, boolean checkDisabled, boolean checkExpired, MessagingTarget messagingTarget) throws Xdi2TransportException, Xdi2MessagingException;

	public MessagingTarget updateMessagingTarget(UriMessagingTargetRegistry httpMessagingTargetRegistry, String messagingTargetFactoryPath, String requestPath, MessagingTarget messagingTarget) throws Xdi2TransportException, Xdi2MessagingException {

		return this.updateMessagingTarget(httpMessagingTargetRegistry, messagingTargetFactoryPath, requestPath, false, false, messagingTarget);
	}
}
