package xdi2.core.util.iterators;

import java.util.Iterator;

import xdi2.core.xri3.CloudName;
import xdi2.core.xri3.XDI3Segment;

/**
 * A MappingIterator that maps XDI segments to Cloud Names.
 * 
 * @author markus
 */
public class MappingCloudNameIterator extends MappingIterator<XDI3Segment, CloudName> {

	public MappingCloudNameIterator(Iterator<XDI3Segment> xris) {

		super(xris);
	}

	@Override
	public CloudName map(XDI3Segment xri) {

		return CloudName.fromXri(xri);
	}
}
