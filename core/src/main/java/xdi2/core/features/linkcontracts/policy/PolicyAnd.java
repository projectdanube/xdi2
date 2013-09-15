package xdi2.core.features.linkcontracts.policy;

import java.util.Iterator;

import xdi2.core.constants.XDIPolicyConstants;
import xdi2.core.features.linkcontracts.evaluation.PolicyEvaluationContext;
import xdi2.core.features.linkcontracts.operator.Operator;
import xdi2.core.features.nodetypes.XdiEntity;
import xdi2.core.features.nodetypes.XdiEntityMemberOrdered;
import xdi2.core.features.nodetypes.XdiEntityMemberUnordered;
import xdi2.core.features.nodetypes.XdiEntitySingleton;

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
			return ((XdiEntitySingleton) xdiEntity).getBaseArcXri().equals(XDIPolicyConstants.XRI_SS_AND);
		else if (xdiEntity instanceof XdiEntityMemberUnordered)
			return ((XdiEntityMemberUnordered) xdiEntity).getXdiCollection().getBaseArcXri().equals(XDIPolicyConstants.XRI_SS_AND);
		else if (xdiEntity instanceof XdiEntityMemberOrdered)
			return ((XdiEntityMemberOrdered) xdiEntity).getXdiCollection().getBaseArcXri().equals(XDIPolicyConstants.XRI_SS_AND);

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
	public Boolean evaluateInternal(PolicyEvaluationContext policyEvaluationContext) {

		for (Iterator<Policy> policies = this.getPolicies(); policies.hasNext(); ) {

			Policy policy = policies.next();
			if (Boolean.FALSE.equals(policy.evaluate(policyEvaluationContext))) return Boolean.FALSE;
		}

		for (Iterator<Operator> operators = this.getOperators(); operators.hasNext(); ) {

			Operator operator = operators.next();
			for (Boolean result : operator.evaluate(policyEvaluationContext)) if (Boolean.FALSE.equals(result)) return Boolean.FALSE;
		}

		return Boolean.TRUE;
	}
}
