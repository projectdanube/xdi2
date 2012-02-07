
package xdi2.util.iterators;

import java.util.Iterator;

import xdi2.Literal;

/**
 * A MappingIterator that maps XDI literals to their datas.
 * 
 * @author msabadello at parityinc dot net
 */
public class MappingLiteralDatasIterator extends MappingIterator<Literal, String> {

	public MappingLiteralDatasIterator(Iterator<Literal> literals) {

		super(literals);
	}

	@Override
	public String map(Literal item) {

		return(item.getData());
	}
}
