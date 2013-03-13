package xdi2.core.util.iterators;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.Statement;

/**
 * A SelectingIterator that only selects XDI statements that are not implied by other statements.
 * 
 * @author markus
 */
public class SelectingNotImpliedStatementIterator<T extends Statement> extends SelectingIterator<T> {

	private static final Logger log = LoggerFactory.getLogger(SelectingNotImpliedStatementIterator.class);

	public SelectingNotImpliedStatementIterator(Iterator<? extends T> statements) {

		super(statements);
	}

	@Override
	public boolean select(Statement statement) {

		if (statement.isImplied()) {

			if (log.isTraceEnabled()) log.trace("Skipping implied statement: " + statement);

			return false;
		}

		return true;
	}
}
