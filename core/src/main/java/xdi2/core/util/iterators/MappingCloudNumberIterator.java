package xdi2.core.util.iterators;

import java.util.Iterator;

import xdi2.core.syntax.CloudNumber;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;

/**
 * A MappingIterator that maps XDI addresses to Cloud Numbers.
 * 
 * @author markus
 */
public class MappingCloudNumberIterator extends MappingIterator<XDIAddress, CloudNumber> {

	private boolean peerRootXDIArc;

	public MappingCloudNumberIterator(Iterator<XDIAddress> XDIaddresses, boolean peerRootXDIArc) {

		super(XDIaddresses);

		this.peerRootXDIArc = peerRootXDIArc;
	}

	public MappingCloudNumberIterator(Iterator<XDIAddress> XDIaddresses) {

		this(XDIaddresses, false);
	}

	@Override
	public CloudNumber map(XDIAddress XDIaddress) {

		if (this.peerRootXDIArc)
			return CloudNumber.fromPeerRootXDIArc(XDIArc.fromComponent(XDIaddress));
		else
			return CloudNumber.fromXDIAddress(XDIaddress);
	}
}
