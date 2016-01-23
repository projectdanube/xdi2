package xdi2.core.util.iterators;

import java.util.Iterator;

import xdi2.core.LiteralNode;

/**
 * A MappingIterator that maps XDI literals to their literal datas.
 * 
 * @author markus
 */
public class MappingLiteralDataIterator extends MappingIterator<LiteralNode, Object> {

	public MappingLiteralDataIterator(Iterator<LiteralNode> literals) {

		super(literals);
	}

	@Override
	public Object map(LiteralNode literal) {

		return literal.getLiteralData();
	}
}
