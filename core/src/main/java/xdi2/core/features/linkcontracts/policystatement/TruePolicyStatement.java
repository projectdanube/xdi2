package xdi2.core.features.linkcontracts.policystatement;

import xdi2.core.Relation;
import xdi2.core.constants.XDIConstants;
import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.core.features.linkcontracts.condition.Condition;
import xdi2.core.features.linkcontracts.evaluation.PolicyEvaluationContext;
import xdi2.core.util.GraphUtil;

/**
 * An XDI $true policy statement, represented as a relation.
 * 
 * @author markus
 */
public class TruePolicyStatement extends ConditionPolicyStatement {

	private static final long serialVersionUID = 4296419491079293469L;

	protected TruePolicyStatement(Relation relation) {

		super(relation);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a relation is a valid XDI $true policy statement.
	 * @param relation The relation to check.
	 * @return True if the relation is a valid XDI $true policy statement.
	 */
	public static boolean isValid(Relation relation) {

		if (! XDIConstants.XRI_S_TRUE.equals(relation.getArcXri())) return false;

		return true;
	}

	/**
	 * Factory method that creates an XDI $true policy statement bound to a given relation.
	 * @param relation The relation that is an XDI $true policy statement.
	 * @return The XDI $true policy statement.
	 */
	public static TruePolicyStatement fromRelation(Relation relation) {

		if (! isValid(relation)) return null;

		return new TruePolicyStatement(relation);
	}

	public static TruePolicyStatement fromCondition(Condition condition) {

		return fromRelation(GraphUtil.relationFromComponents(XDIConstants.XRI_S_ROOT, XDIConstants.XRI_S_TRUE, condition.getStatement().toXriSegment()));
	}

	/*
	 * Instance methods
	 */

	@Override
	public boolean evaluateInternal(PolicyEvaluationContext policyEvaluationContext) {

		Condition condition = this.getCondition();
		if (condition == null) throw new Xdi2RuntimeException("Missing or invalid condition in $true policy statement.");

		return true == condition.evaluate(policyEvaluationContext);
	}
}
