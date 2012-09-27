package xdi2.core.util.iterators;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.Literal;

/**
 * A MappingIterator that maps XDI literals to their context nodes.
 * 
 * @author markus
 */
public class MappingLiteralContextNodeIterator extends MappingIterator<Literal, ContextNode> {

	public MappingLiteralContextNodeIterator(Iterator<Literal> literals) {

		super(literals);
	}

	@Override
	public ContextNode map(Literal literal) {

		return literal.getContextNode();
	}
}
