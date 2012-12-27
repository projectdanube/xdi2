package xdi2.core.features.linkcontracts.condition;

import xdi2.core.ContextNode;
import xdi2.core.Statement;
import xdi2.core.Statement.ContextNodeStatement;
import xdi2.core.Statement.LiteralStatement;
import xdi2.core.Statement.RelationStatement;
import xdi2.core.util.locator.ContextNodeLocator;
import xdi2.core.xri3.impl.XDI3Segment;

/**
 * An XDI statement condition, represented as a statement.
 * 
 * @author markus
 */
public class StatementCondition extends Condition {

	private static final long serialVersionUID = 3812888725775095575L;

	protected StatementCondition(Statement statement) {

		super(statement);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a statement is a valid XDI statement condition.
	 * @param relation The relation to check.
	 * @return True if the relation is a valid XDI statement condition.
	 */
	public static boolean isValid(Statement statement) {

		return true;
	}

	/**
	 * Factory method that creates an XDI statement condition bound to a given statement.
	 * @param statement The statement that is an XDI statement condition.
	 * @return The XDI statement condition.
	 */
	public static StatementCondition fromStatement(Statement statement) {

		if (! isValid(statement)) return null;

		return new StatementCondition(statement);
	}

	/*
	 * Instance methods
	 */

	@Override
	public boolean evaluateInternal(ContextNodeLocator contextNodeLocator) {

		if (this.getStatement() instanceof ContextNodeStatement) {

			ContextNode subject = contextNodeLocator.locateContextNode(this.getStatement().getContextNodeXri());

			return subject != null;
		}

		if (this.getStatement() instanceof RelationStatement) {

			ContextNode subject = contextNodeLocator.locateContextNode(this.getStatement().getContextNodeXri());
			XDI3Segment arcXri = ((RelationStatement) this.getStatement()).getRelation().getArcXri();
			XDI3Segment targetContextNodeXri = contextNodeLocator.getContextNodeXri(((RelationStatement) this.getStatement()).getRelation().getTargetContextNodeXri());

			return subject.containsRelation(arcXri, targetContextNodeXri);
		}

		if (this.getStatement() instanceof LiteralStatement) {

			ContextNode subject = contextNodeLocator.locateContextNode(this.getStatement().getContextNodeXri());
			String literalData = ((LiteralStatement) this.getStatement()).getLiteral().getLiteralData();

			return subject.containsLiteral(literalData);
		}

		return false;
	}
}
