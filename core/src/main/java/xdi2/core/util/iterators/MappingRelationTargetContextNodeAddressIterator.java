package xdi2.core.util.iterators;

import java.util.Iterator;

import xdi2.core.Relation;
import xdi2.core.syntax.XDIAddress;

/**
 * A MappingIterator that maps XDI relations to their target context node addresses.
 * 
 * @author markus
 */
public class MappingRelationTargetContextNodeAddressIterator extends MappingIterator<Relation, XDIAddress> {

	public MappingRelationTargetContextNodeAddressIterator(Iterator<Relation> relations) {

		super(relations);
	}

	@Override
	public XDIAddress map(Relation relation) {

		return relation.getTargetContextNodeAddress();
	}
}
