package xdi2.core.util.iterators;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.Relation;

/**
 * A MappingIterator that maps XDI relations to their target context nodes.
 * 
 * @author markus
 */
public class MappingRelationTargetContextNodeIterator extends MappingIterator<Relation, ContextNode> {

	public MappingRelationTargetContextNodeIterator(Iterator<Relation> relations) {

		super(relations);
	}

	@Override
	public ContextNode map(Relation relation) {

		return relation.follow();
	}
}
