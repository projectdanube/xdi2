package xdi2.util.iterators;

import java.util.Iterator;

import xdi2.xri3.impl.XRI3Segment;

/**
 * A MappingIterator that maps XDI references to their XRIs.
 * 
 * @author markus
 */
public class MappingRelationXrisIterator extends MappingIterator<Reference, XRI3Segment> {

	public MappingRelationXrisIterator(Iterator<Reference> references) {

		super(references);
	}

	@Override
	public XRI3Segment map(Reference item) {

		return(item.getReferenceXri());
	}
}
