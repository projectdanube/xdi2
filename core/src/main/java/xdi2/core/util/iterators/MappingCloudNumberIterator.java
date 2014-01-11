package xdi2.core.util.iterators;

import java.util.Iterator;

import xdi2.core.xri3.CloudNumber;
import xdi2.core.xri3.XDI3Segment;

/**
 * A MappingIterator that maps XDI segments to Cloud Numbers.
 * 
 * @author markus
 */
public class MappingCloudNumberIterator extends MappingIterator<XDI3Segment, CloudNumber> {

	public MappingCloudNumberIterator(Iterator<XDI3Segment> xris) {

		super(xris);
	}

	@Override
	public CloudNumber map(XDI3Segment xri) {

		CloudNumber cloudNumber = CloudNumber.fromXri(xri);
		if (cloudNumber == null) cloudNumber = CloudNumber.fromPeerRootXri(xri);
		if (cloudNumber == null) return null;

		return cloudNumber;
	}
}
