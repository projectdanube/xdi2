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

	public MappingCloudNumberIterator(Iterator<XDIAddress> addresses) {

		super(addresses);
	}

	@Override
	public CloudNumber map(XDIAddress address) {

		CloudNumber cloudNumber = CloudNumber.fromAddress(address);
		if (cloudNumber == null) cloudNumber = CloudNumber.fromPeerRootArc(address);
		if (cloudNumber == null) return null;

		return cloudNumber;
	}
}
