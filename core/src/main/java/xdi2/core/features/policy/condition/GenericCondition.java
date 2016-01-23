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

	protected GenericCondition(XDIStatement XDIstatement) {

		super(XDIstatement);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a statement is a valid XDI generic condition.
	 * @param XDIstatement The statement to check.
	 * @return True if the statement is a valid XDI generic condition.
	 */
	public static boolean isValid(XDIStatement XDIstatement) {

		return true;
	}

	/**
	 * Factory method that creates an XDI generic condition bound to a given statement.
	 * @param XDIstatement The statement that is an XDI generic condition.
	 * @return The XDI generic condition.
	 */
	public static GenericCondition fromStatement(XDIStatement XDIstatement) {

		if (! isValid(XDIstatement)) return null;

		return new GenericCondition(XDIstatement);
	}

	/*
	 * Instance methods
	 */

	@Override
	public boolean evaluateInternal(PolicyEvaluationContext policyEvaluationContext) {

		Statement statement = policyEvaluationContext.getStatement(this.getXDIStatement());

		return statement != null;
	}
}
