package xdi2.core.features.linkcontracts.policystatement;

import xdi2.core.Relation;
import xdi2.core.constants.XDIConstants;
import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.core.features.linkcontracts.condition.Condition;
import xdi2.core.features.linkcontracts.evaluation.PolicyEvaluationContext;
import xdi2.core.util.GraphUtil;

/**
 * An XDI $false policy statement, represented as a relation.
 * 
 * @author markus
 */
public class FalsePolicyStatement extends ConditionPolicyStatement {

	private static final long serialVersionUID = -7397004800836677763L;

	protected FalsePolicyStatement(Relation relation) {

		super(relation);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a relation is a valid XDI $false policy statement.
	 * @param relation The relation to check.
	 * @return True if the relation is a valid XDI $false policy statement.
	 */
	public static boolean isValid(Relation relation) {

		if (! XDIConstants.XRI_S_FALSE.equals(relation.getArcXri())) return false;

		return true;
	}

	/**
	 * Factory method that creates an XDI $false policy statement bound to a given relation.
	 * @param relation The relation that is an XDI $false policy statement.
	 * @return The XDI $false policy statement.
	 */
	public static FalsePolicyStatement fromRelation(Relation relation) {

		if (! isValid(relation)) return null;

		return new FalsePolicyStatement(relation);
	}

	public static FalsePolicyStatement fromCondition(Condition condition) {

		return fromRelation(GraphUtil.relationFromComponents(XDIConstants.XRI_S_ROOT, XDIConstants.XRI_S_FALSE, condition.getStatement().toXriSegment()));
	}

	/*
	 * Instance methods
	 */

	@Override
	public boolean evaluateInternal(PolicyEvaluationContext policyEvaluationContext) {

		Condition condition = this.getCondition();
		if (condition == null) throw new Xdi2RuntimeException("Missing or invalid condition in $false policy statement.");

		return false == condition.evaluate(policyEvaluationContext);
	}
}
