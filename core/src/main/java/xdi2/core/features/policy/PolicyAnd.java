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
 * An XDI $and policy, represented as an XDI entity.
 * 
 * @author markus
 */
public class PolicyAnd extends Policy {

	private static final long serialVersionUID = 5732150498065911411L;

	protected PolicyAnd(XdiEntity xdiEntity) {

		super(xdiEntity);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if an XDI entity is a valid XDI $and policy.
	 * @param xdiEntity The XDI entity to check.
	 * @return True if the XDI entity is a valid XDI $and policy.
	 */
	public static boolean isValid(XdiEntity xdiEntity) {

		if (xdiEntity instanceof XdiEntitySingleton)
			return ((XdiEntitySingleton) xdiEntity).getBaseXDIArc().equals(XDIPolicyConstants.XDI_ARC_AND);
		else if (xdiEntity instanceof XdiEntityInstanceUnordered)
			return ((XdiEntityInstanceUnordered) xdiEntity).getXdiCollection().getBaseXDIArc().equals(XDIPolicyConstants.XDI_ARC_AND);
		else if (xdiEntity instanceof XdiEntityInstanceOrdered)
			return ((XdiEntityInstanceOrdered) xdiEntity).getXdiCollection().getBaseXDIArc().equals(XDIPolicyConstants.XDI_ARC_AND);

		return false;
	}

	/**
	 * Factory method that creates an XDI $and policy bound to a given XDI entity.
	 * @param xdiEntity The XDI entity that is an XDI root policy.
	 * @return The XDI $and policy.
	 */
	public static PolicyAnd fromXdiEntity(XdiEntity xdiEntity) {

		if (! isValid(xdiEntity)) return null;

		return new PolicyAnd(xdiEntity);
	}

	/*
	 * Instance methods
	 */

	@Override
	public boolean evaluateInternal(PolicyEvaluationContext policyEvaluationContext) {

		Iterator<Policy> policies = this.getPolicies();

		while (policies.hasNext()) {

			Policy policy = policies.next();
			if (! policy.evaluate(policyEvaluationContext)) return false;
		}

		Iterator<Operator> operators = this.getOperators();

		while (operators.hasNext()) {

			Operator operator = operators.next();
			for (boolean result : operator.evaluate(policyEvaluationContext)) if (! result) return false;
		}

		return true;
	}
}
