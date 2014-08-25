package xdi2.transport.impl.http.factory;

import java.util.Iterator;

import xdi2.core.syntax.XDIArc;
import xdi2.core.util.iterators.EmptyIterator;

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
	public Iterator<XDIArc> getOwnerPeerRootXDIArcs() {

		return new EmptyIterator<XDIArc> ();
	}

	@Override
	public String getRequestPath(String messagingTargetFactoryPath, XDIArc ownerPeerRootAddress) {

		return null;
	}
}
