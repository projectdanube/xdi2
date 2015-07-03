package xdi2.transport.impl.http.factory.impl;

import java.util.Iterator;

import xdi2.core.syntax.XDIArc;
import xdi2.core.util.iterators.EmptyIterator;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.factory.impl.AbstractMessagingTargetFactory;
import xdi2.transport.exceptions.Xdi2TransportException;
import xdi2.transport.impl.http.factory.HttpMessagingTargetFactory;
import xdi2.transport.impl.http.registry.HttpMessagingTargetRegistry;

public abstract class AbstractHttpMessagingTargetFactory extends AbstractMessagingTargetFactory implements HttpMessagingTargetFactory {

	public AbstractHttpMessagingTargetFactory() {

	}

	@Override
	public Iterator<XDIArc> getOwnerPeerRootXDIArcs() {

		return new EmptyIterator<XDIArc> ();
	}

	@Override
	public MessagingTarget mountMessagingTarget(HttpMessagingTargetRegistry httpMessagingTargetRegistry, String messagingTargetFactoryPath, String requestPath) throws Xdi2TransportException, Xdi2MessagingException {

		return this.mountMessagingTarget(httpMessagingTargetRegistry, messagingTargetFactoryPath, requestPath, false, false);
	}

	@Override
	public MessagingTarget updateMessagingTarget(HttpMessagingTargetRegistry httpMessagingTargetRegistry, String messagingTargetFactoryPath, String requestPath, MessagingTarget messagingTarget) throws Xdi2TransportException, Xdi2MessagingException {

		return this.updateMessagingTarget(httpMessagingTargetRegistry, messagingTargetFactoryPath, requestPath, false, false, messagingTarget);
	}
}
