package xdi2.core.util.iterators;

import java.util.Iterator;

import xdi2.core.Literal;

/**
 * A MappingIterator that maps XDI literals to their literal datas.
 * 
 * @author markus
 */
public class MappingLiteralDataBooleanIterator extends MappingIterator<Literal, Boolean> {

	public MappingLiteralDataBooleanIterator(Iterator<Literal> literals) {

		super(literals);
	}

	@Override
	public Boolean map(Literal literal) {

		return literal.getLiteralDataBoolean();
	}
}
