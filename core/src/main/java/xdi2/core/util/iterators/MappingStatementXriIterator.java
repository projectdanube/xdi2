package xdi2.core.util.iterators;

import java.util.Iterator;

import xdi2.core.Statement;
import xdi2.core.xri3.XDI3Statement;

/**
 * A MappingIterator that maps XDI statements to their statement XRIs.
 * 
 * @author markus
 */
public class MappingStatementXriIterator extends MappingIterator<Statement, XDI3Statement> {

	public MappingStatementXriIterator(Iterator<Statement> statements) {

		super(statements);
	}

	@Override
	public XDI3Statement map(Statement statement) {

		return statement.getXri();
	}
}
