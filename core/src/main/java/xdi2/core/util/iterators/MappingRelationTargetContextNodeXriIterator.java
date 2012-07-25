package xdi2.core.util.iterators;

import java.util.Iterator;

import xdi2.core.Relation;
import xdi2.core.xri3.impl.XRI3Segment;

/**
 * A MappingIterator that maps XDI relations to their target context nodes XRIs.
 * 
 * @author markus
 */
public class MappingRelationTargetContextNodeXriIterator extends MappingIterator<Relation, XRI3Segment> {

	public MappingRelationTargetContextNodeXriIterator(Iterator<Relation> relations) {

		super(relations);
	}

	@Override
	public XRI3Segment map(Relation relation) {

		return relation.getTargetContextNodeXri();
	}
}
