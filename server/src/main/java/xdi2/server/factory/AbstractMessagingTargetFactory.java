package xdi2.server.factory;

import java.util.Iterator;

import xdi2.core.util.iterators.EmptyIterator;
import xdi2.core.xri3.XDI3Segment;

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
	public Iterator<XDI3Segment> getOwnerPeerRootXris() {

		return new EmptyIterator<XDI3Segment> ();
	}

	@Override
	public String getRequestPath(String messagingTargetFactoryPath, XDI3Segment ownerPeerRootXri) {

		return null;
	}
}
