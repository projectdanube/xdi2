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
 * An XDI $or policy, represented as an XDI entity.
 * 
 * @author markus
 */
public class PolicyOr extends Policy {

	private static final long serialVersionUID = 5732150401265911411L;

	protected PolicyOr(XdiEntity xdiEntity) {

		super(xdiEntity);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if an XDI entity is a valid XDI $or policy.
	 * @param xdiEntity The XDI entity to check.
	 * @return True if the XDI entity is a valid XDI $or policy.
	 */
	public static boolean isValid(XdiEntity xdiEntity) {

		if (xdiEntity instanceof XdiEntitySingleton)
			return ((XdiEntitySingleton) xdiEntity).getBaseXDIArc().equals(XDIPolicyConstants.XDI_ARC_OR);
		else if (xdiEntity instanceof XdiEntityInstanceUnordered)
			return ((XdiEntityInstanceUnordered) xdiEntity).getXdiCollection().getBaseXDIArc().equals(XDIPolicyConstants.XDI_ARC_OR);
		else if (xdiEntity instanceof XdiEntityInstanceOrdered)
			return ((XdiEntityInstanceOrdered) xdiEntity).getXdiCollection().getBaseXDIArc().equals(XDIPolicyConstants.XDI_ARC_OR);

		return false;
	}

	/**
	 * Factory method that creates an XDI $or policy bound to a XDI entity
	 * @param xdiEntity The XDI entity that is an XDI root policy.
	 * @return The XDI $or policy.
	 */
	public static PolicyOr fromXdiEntity(XdiEntity xdiEntity) {

		if (! isValid(xdiEntity)) return null;

		return new PolicyOr(xdiEntity);
	}

	/*
	 * Instance methods
	 */

	@Override
	public boolean evaluateInternal(PolicyEvaluationContext policyEvaluationContext) {

		Iterator<Policy> policies = this.getPolicies();

		while (policies.hasNext()) {

			Policy policy = policies.next();
			if (policy.evaluate(policyEvaluationContext)) return true;
		}

		Iterator<Operator> operators = this.getOperators();

		while (operators.hasNext()) {

			Operator operator = operators.next();
			for (boolean result : operator.evaluate(policyEvaluationContext)) if (result) return true;
		}

		return false;
	}
}
