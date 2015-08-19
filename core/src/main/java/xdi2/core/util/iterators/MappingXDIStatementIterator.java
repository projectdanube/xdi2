package xdi2.core.util.iterators;

import java.util.Iterator;

import xdi2.core.Statement;
import xdi2.core.syntax.XDIStatement;

/**
 * A MappingIterator that maps XDI statements.
 * 
 * @author markus
 */
public class MappingXDIStatementIterator extends MappingIterator<Statement, XDIStatement> {

	public MappingXDIStatementIterator(Iterator<Statement> statements) {

		super(statements);
	}

	@Override
	public XDIStatement map(Statement statement) {

		return statement.getXDIStatement();
	}
}
