package xdi2.core.util.iterators;

import java.util.Iterator;

import xdi2.core.syntax.CloudNumber;
import xdi2.core.syntax.XDIAddress;

/**
 * A MappingIterator that maps XDI addresses to Cloud Numbers.
 * 
 * @author markus
 */
public class MappingCloudNumberIterator extends MappingIterator<XDIAddress, CloudNumber> {

	public MappingCloudNumberIterator(Iterator<XDIAddress> XDIaddresses) {

		super(XDIaddresses);
	}

	@Override
	public CloudNumber map(XDIAddress XDIaddress) {

		CloudNumber cloudNumber = CloudNumber.fromXDIAddress(XDIaddress);
		if (cloudNumber == null) cloudNumber = CloudNumber.fromPeerRootXDIArc(XDIaddress);
		if (cloudNumber == null) return null;

		return cloudNumber;
	}
}
