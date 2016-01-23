package xdi2.core.util.iterators;

import java.util.Iterator;

import xdi2.core.LiteralNode;
import xdi2.core.Relation;

/**
 * A MappingIterator that maps XDI relations to their target literal nodes.
 * 
 * @author markus
 */
public class MappingRelationTargetLiteralNodeIterator extends MappingIterator<Relation, LiteralNode> {

	public MappingRelationTargetLiteralNodeIterator(Iterator<Relation> relations) {

		super(relations);
	}

	@Override
	public LiteralNode map(Relation relation) {

		return relation.followLiteralNode();
	}
}
