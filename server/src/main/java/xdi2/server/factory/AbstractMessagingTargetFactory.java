package xdi2.server.factory;

import java.util.Iterator;

import xdi2.core.util.iterators.EmptyIterator;
import xdi2.core.xri3.XDI3SubSegment;

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
	public Iterator<XDI3SubSegment> getOwnerPeerRootXris() {

		return new EmptyIterator<XDI3SubSegment> ();
	}

	@Override
	public String getRequestPath(String messagingTargetFactoryPath, XDI3SubSegment ownerPeerRootXri) {

		return null;
	}
}
