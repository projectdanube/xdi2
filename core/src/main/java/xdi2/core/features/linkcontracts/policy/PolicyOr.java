package xdi2.core.features.linkcontracts.policy;

import java.util.Iterator;

import xdi2.core.constants.XDIPolicyConstants;
import xdi2.core.features.linkcontracts.evaluation.PolicyEvaluationContext;
import xdi2.core.features.linkcontracts.operator.Operator;
import xdi2.core.features.nodetypes.XdiEntity;
import xdi2.core.features.nodetypes.XdiEntityInstanceOrdered;
import xdi2.core.features.nodetypes.XdiEntityInstanceUnordered;
import xdi2.core.features.nodetypes.XdiEntitySingleton;

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
			return ((XdiEntitySingleton) xdiEntity).getBaseArcXri().equals(XDIPolicyConstants.XRI_SS_OR);
		else if (xdiEntity instanceof XdiEntityInstanceUnordered)
			return ((XdiEntityInstanceUnordered) xdiEntity).getXdiClass().getBaseArcXri().equals(XDIPolicyConstants.XRI_SS_OR);
		else if (xdiEntity instanceof XdiEntityInstanceOrdered)
			return ((XdiEntityInstanceOrdered) xdiEntity).getXdiClass().getBaseArcXri().equals(XDIPolicyConstants.XRI_SS_OR);

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
	public Boolean evaluateInternal(PolicyEvaluationContext policyEvaluationContext) {

		for (Iterator<Policy> policies = this.getPolicies(); policies.hasNext(); ) {

			Policy policy = policies.next();
			if (Boolean.TRUE.equals(policy.evaluate(policyEvaluationContext))) return Boolean.TRUE;
		}

		for (Iterator<Operator> operators = this.getOperators(); operators.hasNext(); ) {

			Operator operator = operators.next();
			for (Boolean result : operator.evaluate(policyEvaluationContext)) if (Boolean.TRUE.equals(result)) return Boolean.TRUE;
		}

		return Boolean.FALSE;
	}
}
