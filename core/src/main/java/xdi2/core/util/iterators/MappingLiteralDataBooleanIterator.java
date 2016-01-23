package xdi2.core.util.iterators;

import java.util.Iterator;

import xdi2.core.LiteralNode;

/**
 * A MappingIterator that maps XDI literals to their literal datas.
 * 
 * @author markus
 */
public class MappingLiteralDataBooleanIterator extends MappingIterator<LiteralNode, Boolean> {

	public MappingLiteralDataBooleanIterator(Iterator<LiteralNode> literals) {

		super(literals);
	}

	@Override
	public Boolean map(LiteralNode literal) {

		return literal.getLiteralDataBoolean();
	}
}
