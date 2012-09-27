package xdi2.core.util.iterators;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.Relation;

/**
 * A MappingIterator that maps XDI relations to their context nodes.
 * 
 * @author markus
 */
public class MappingRelationContextNodeIterator extends MappingIterator<Relation, ContextNode> {

	public MappingRelationContextNodeIterator(Iterator<Relation> relations) {

		super(relations);
	}

	@Override
	public ContextNode map(Relation relation) {

		return relation.getContextNode();
	}
}
