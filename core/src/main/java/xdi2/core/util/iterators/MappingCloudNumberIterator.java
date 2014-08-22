package xdi2.core.util.iterators;

import java.util.Iterator;

import xdi2.core.syntax.CloudNumber;
import xdi2.core.syntax.XDIAddress;

/**
 * A MappingIterator that maps XDI segments to Cloud Numbers.
 * 
 * @author markus
 */
public class MappingCloudNumberIterator extends MappingIterator<XDIAddress, CloudNumber> {

	public MappingCloudNumberIterator(Iterator<XDIAddress> xris) {

		super(xris);
	}

	@Override
	public CloudNumber map(XDIAddress xri) {

		CloudNumber cloudNumber = CloudNumber.fromAddress(xri);
		if (cloudNumber == null) cloudNumber = CloudNumber.fromPeerRootArc(xri);
		if (cloudNumber == null) return null;

		return cloudNumber;
	}
}
