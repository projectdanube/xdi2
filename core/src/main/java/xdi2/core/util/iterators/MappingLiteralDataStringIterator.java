package xdi2.core.util.iterators;

import java.util.Iterator;

import xdi2.core.LiteralNode;

/**
 * A MappingIterator that maps XDI literals to their literal datas.
 * 
 * @author markus
 */
public class MappingLiteralDataStringIterator extends MappingIterator<LiteralNode, String> {

	public MappingLiteralDataStringIterator(Iterator<LiteralNode> literals) {

		super(literals);
	}

	@Override
	public String map(LiteralNode literal) {

		return literal.getLiteralDataString();
	}
}
