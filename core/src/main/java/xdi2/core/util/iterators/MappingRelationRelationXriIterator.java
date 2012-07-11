package xdi2.core.util.iterators;

import java.util.Iterator;

import xdi2.core.Relation;
import xdi2.core.xri3.impl.XRI3Segment;

/**
 * A MappingIterator that maps XDI relations to their arc XRIs.
 * 
 * @author markus
 */
public class MappingRelationRelationXriIterator extends MappingIterator<Relation, XRI3Segment> {

	public MappingRelationRelationXriIterator(Iterator<Relation> relations) {

		super(relations);
	}

	@Override
	public XRI3Segment map(Relation relation) {

		return relation.getArcXri();
	}
}
