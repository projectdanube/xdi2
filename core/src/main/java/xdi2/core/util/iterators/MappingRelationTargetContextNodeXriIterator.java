package xdi2.core.util.iterators;

import java.util.Iterator;

import xdi2.core.Relation;
import xdi2.core.xri3.XDI3Segment;

/**
 * A MappingIterator that maps XDI relations to their target context node XRIs.
 * 
 * @author markus
 */
public class MappingRelationTargetContextNodeXriIterator extends MappingIterator<Relation, XDI3Segment> {

	public MappingRelationTargetContextNodeXriIterator(Iterator<Relation> relations) {

		super(relations);
	}

	@Override
	public XDI3Segment map(Relation relation) {

		return relation.getTargetContextNodeXri();
	}
}
