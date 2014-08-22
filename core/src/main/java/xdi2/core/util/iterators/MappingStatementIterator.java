package xdi2.core.util.iterators;

import java.util.Iterator;

import xdi2.core.Statement;
import xdi2.core.syntax.XDIStatement;

/**
 * A MappingIterator that maps XDI statements to their statement XRIs.
 * 
 * @author markus
 */
public class MappingStatementIterator extends MappingIterator<Statement, XDIStatement> {

	public MappingStatementIterator(Iterator<Statement> statements) {

		super(statements);
	}

	@Override
	public XDIStatement map(Statement statement) {

		return statement.getXri();
	}
}
