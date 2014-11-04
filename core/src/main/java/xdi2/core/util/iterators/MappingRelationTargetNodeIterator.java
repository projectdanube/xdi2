package xdi2.core.util.iterators;

import java.util.Iterator;

import xdi2.core.Node;
import xdi2.core.Relation;

/**
 * A MappingIterator that maps XDI relations to their target nodes.
 * 
 * @author markus
 */
public class MappingRelationTargetNodeIterator extends MappingIterator<Relation, Node> {

	public MappingRelationTargetNodeIterator(Iterator<Relation> relations) {

		super(relations);
	}

	@Override
	public Node map(Relation relation) {

		return relation.follow();
	}
}
