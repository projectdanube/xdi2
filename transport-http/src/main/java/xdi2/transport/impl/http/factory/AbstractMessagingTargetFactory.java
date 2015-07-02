package xdi2.transport.impl.http.factory;

import java.util.Iterator;

import xdi2.core.syntax.XDIArc;
import xdi2.core.util.iterators.EmptyIterator;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.exceptions.Xdi2MessagingException;
import xdi2.transport.exceptions.Xdi2TransportException;
import xdi2.transport.impl.http.registry.HttpMessagingTargetRegistry;

public abstract class AbstractMessagingTargetFactory implements MessagingTargetFactory {

	public AbstractMessagingTargetFactory() {

	}

	@Override
	public void init() throws Exception {

	}

	@Override
	public void shutdown() throws Exception {

	}

	@Override
	public MessagingTarget mountMessagingTarget(HttpMessagingTargetRegistry httpMessagingTargetRegistry, String messagingTargetFactoryPath, String requestPath) throws Xdi2TransportException, Xdi2MessagingException {

		return this.mountMessagingTarget(httpMessagingTargetRegistry, messagingTargetFactoryPath, requestPath, false, false);
	}

	@Override
	public MessagingTarget updateMessagingTarget(HttpMessagingTargetRegistry httpMessagingTargetRegistry, String messagingTargetFactoryPath, String requestPath, MessagingTarget messagingTarget) throws Xdi2TransportException, Xdi2MessagingException {

		return this.updateMessagingTarget(httpMessagingTargetRegistry, messagingTargetFactoryPath, requestPath, false, false, messagingTarget);
	}

	@Override
	public Iterator<XDIArc> getOwnerPeerRootXDIArcs() {

		return new EmptyIterator<XDIArc> ();
	}

	@Override
	public String getRequestPath(String messagingTargetFactoryPath, XDIArc ownerPeerRootXDIArc) {

		return null;
	}
}
