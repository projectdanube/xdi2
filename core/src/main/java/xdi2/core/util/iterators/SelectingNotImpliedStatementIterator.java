package xdi2.core.util.iterators;

import java.util.Iterator;

import xdi2.core.Statement;

/**
 * A SelectingIterator that only selects XDI statements that are not implied by other statements.
 * 
 * @author markus
 */
public class SelectingNotImpliedStatementIterator extends SelectingIterator<Statement> {

	public SelectingNotImpliedStatementIterator(Iterator<Statement> statements) {

		super(statements);
	}

	@Override
	public boolean select(Statement statement) {

		return ! statement.isImplied();
	}
}
