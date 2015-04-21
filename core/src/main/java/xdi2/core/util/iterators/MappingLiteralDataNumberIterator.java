package xdi2.core.util.iterators;

import java.util.Iterator;

import xdi2.core.LiteralNode;

/**
 * A MappingIterator that maps XDI literals to their literal datas.
 * 
 * @author markus
 */
public class MappingLiteralDataNumberIterator extends MappingIterator<LiteralNode, Number> {

	public MappingLiteralDataNumberIterator(Iterator<LiteralNode> literals) {

		super(literals);
	}

	@Override
	public Number map(LiteralNode literal) {

		return literal.getLiteralDataNumber();
	}
}
