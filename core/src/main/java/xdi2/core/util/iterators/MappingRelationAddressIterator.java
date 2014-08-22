package xdi2.core.util.iterators;

import java.util.Iterator;

import xdi2.core.Relation;
import xdi2.core.syntax.XDIAddress;

/**
 * A MappingIterator that maps XDI relations to their arcs.
 * 
 * @author markus
 */
public class MappingRelationAddressIterator extends MappingIterator<Relation, XDIAddress> {

	public MappingRelationAddressIterator(Iterator<Relation> relations) {

		super(relations);
	}

	@Override
	public XDIAddress map(Relation relation) {

		return relation.getArc();
	}
}
