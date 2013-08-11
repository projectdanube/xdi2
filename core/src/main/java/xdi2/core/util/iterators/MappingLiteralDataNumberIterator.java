package xdi2.core.util.iterators;

import java.util.Iterator;

import xdi2.core.Literal;

/**
 * A MappingIterator that maps XDI literals to their literal datas.
 * 
 * @author markus
 */
public class MappingLiteralDataNumberIterator extends MappingIterator<Literal, Number> {

	public MappingLiteralDataNumberIterator(Iterator<Literal> literals) {

		super(literals);
	}

	@Override
	public Number map(Literal literal) {

		return literal.getLiteralDataNumber();
	}
}
