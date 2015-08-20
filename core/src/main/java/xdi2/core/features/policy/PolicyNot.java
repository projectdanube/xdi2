package xdi2.core.features.policy;

import java.util.Iterator;

import xdi2.core.constants.XDIPolicyConstants;
import xdi2.core.features.nodetypes.XdiEntity;
import xdi2.core.features.nodetypes.XdiEntityInstanceOrdered;
import xdi2.core.features.nodetypes.XdiEntityInstanceUnordered;
import xdi2.core.features.nodetypes.XdiEntitySingleton;
import xdi2.core.features.policy.evaluation.PolicyEvaluationContext;
import xdi2.core.features.policy.operator.Operator;

/**
 * An XDI $not policy, represented as an XDI entity.
 * 
 * @author markus
 */
public class PolicyNot extends Policy {

	private static final long serialVersionUID = 5732150467865911411L;

	protected PolicyNot(XdiEntity xdiEntity) {

		super(xdiEntity);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if an XDI entity is a valid XDI $not policy.
	 * @param xdiEntity The XDI entity to check.
	 * @return True if the XDI entity is a valid XDI $not policy.
	 */
	public static boolean isValid(XdiEntity xdiEntity) {

		if (xdiEntity instanceof XdiEntitySingleton)
			return ((XdiEntitySingleton) xdiEntity).getBaseXDIArc().equals(XDIPolicyConstants.XDI_ARC_NOT);
		else if (xdiEntity instanceof XdiEntityInstanceUnordered)
			return ((XdiEntityInstanceUnordered) xdiEntity).getXdiCollection().getBaseXDIArc().equals(XDIPolicyConstants.XDI_ARC_NOT);
		else if (xdiEntity instanceof XdiEntityInstanceOrdered)
			return ((XdiEntityInstanceOrdered) xdiEntity).getXdiCollection().getBaseXDIArc().equals(XDIPolicyConstants.XDI_ARC_NOT);

		return false;
	}

	/**
	 * Factory method that creates an XDI $and policy bound to a given XDI entity.
	 * @param xdiEntity The XDI entity that is an XDI root policy.
	 * @return The XDI $and policy.
	 */
	public static PolicyNot fromXdiEntity(XdiEntity xdiEntity) {

		if (! isValid(xdiEntity)) return null;

		return new PolicyNot(xdiEntity);
	}

	/*
	 * Instance methods
	 */

	@Override
	public boolean evaluateInternal(PolicyEvaluationContext policyEvaluationContext) {

		for (Iterator<Policy> policies = this.getPolicies(); policies.hasNext(); ) {

			Policy policy = policies.next();
			if (! policy.evaluate(policyEvaluationContext)) return true;
		}

		for (Iterator<Operator> operators = this.getOperators(); operators.hasNext(); ) {

			Operator operator = operators.next();
			for (boolean result : operator.evaluate(policyEvaluationContext)) if (! result) return true;
		}

		return false;
	}
}
