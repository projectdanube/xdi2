package xdi2.core.util.iterators;

import java.util.Iterator;

import xdi2.core.Literal;

/**
 * A MappingIterator that maps XDI literals to their literal datas.
 * 
 * @author markus
 */
public class MappingLiteralDataStringIterator extends MappingIterator<Literal, String> {

	public MappingLiteralDataStringIterator(Iterator<Literal> literals) {

		super(literals);
	}

	@Override
	public String map(Literal literal) {

		return literal.getLiteralDataString();
	}
}
