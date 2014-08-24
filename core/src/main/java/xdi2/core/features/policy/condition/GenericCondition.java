package xdi2.core.features.policy.condition;

import xdi2.core.Statement;
import xdi2.core.features.policy.evaluation.PolicyEvaluationContext;
import xdi2.core.syntax.XDIStatement;

/**
 * An XDI generic condition, represented as a statement.
 * 
 * @author markus
 */
public class GenericCondition extends Condition {

	private static final long serialVersionUID = 3812888725775095575L;

	protected GenericCondition(XDIStatement statementXri) {

		super(statementXri);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a statement is a valid XDI generic condition.
	 * @param statement The statement to check.
	 * @return True if the statement is a valid XDI generic condition.
	 */
	public static boolean isValid(XDIStatement statement) {

		return true;
	}

	/**
	 * Factory method that creates an XDI generic condition bound to a given statement.
	 * @param statement The statement that is an XDI generic condition.
	 * @return The XDI generic condition.
	 */
	public static GenericCondition fromStatement(XDIStatement statement) {

		if (! isValid(statement)) return null;

		return new GenericCondition(statement);
	}

	/*
	 * Instance methods
	 */

	@Override
	public Boolean evaluateInternal(PolicyEvaluationContext policyEvaluationContext) {

		Statement statement = policyEvaluationContext.getStatement(this.getStatementXri());

		return Boolean.valueOf(statement != null);
	}
}
