package xdi2.core.util.iterators;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.LiteralNode;

/**
 * A MappingIterator that maps XDI literals to their context nodes.
 * 
 * @author markus
 */
public class MappingLiteralNodeContextNodeIterator extends MappingIterator<LiteralNode, ContextNode> {

	public MappingLiteralNodeContextNodeIterator(Iterator<LiteralNode> literals) {

		super(literals);
	}

	@Override
	public ContextNode map(LiteralNode literal) {

		return literal.getContextNode();
	}
}
