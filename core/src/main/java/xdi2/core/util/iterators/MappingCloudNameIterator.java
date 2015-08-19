package xdi2.core.util.iterators;

import java.util.Iterator;

import xdi2.core.syntax.CloudName;
import xdi2.core.syntax.XDIAddress;

/**
 * A MappingIterator that maps XDI addresses to Cloud Names.
 * 
 * @author markus
 */
public class MappingCloudNameIterator extends MappingIterator<XDIAddress, CloudName> {

	private boolean peerRootXDIArc;

	public MappingCloudNameIterator(Iterator<XDIAddress> XDIaddresses, boolean peerRootXDIArc) {

		super(XDIaddresses);

		this.peerRootXDIArc = peerRootXDIArc;
	}

	public MappingCloudNameIterator(Iterator<XDIAddress> XDIaddresses) {

		this(XDIaddresses, false);
	}

	@Override
	public CloudName map(XDIAddress XDIaddress) {

		if (this.peerRootXDIArc)
			return CloudName.fromPeerRootXDIArc(XDIaddress);
		else
			return CloudName.fromXDIAddress(XDIaddress);
	}
}
