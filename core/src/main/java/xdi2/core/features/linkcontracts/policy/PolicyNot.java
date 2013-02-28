package xdi2.core.features.linkcontracts.policy;

import java.util.Iterator;

import xdi2.core.constants.XDIPolicyConstants;
import xdi2.core.features.linkcontracts.evaluation.PolicyEvaluationContext;
import xdi2.core.features.linkcontracts.operator.Operator;
import xdi2.core.features.multiplicity.XdiEntityMember;
import xdi2.core.features.multiplicity.XdiEntitySingleton;
import xdi2.core.features.multiplicity.XdiSubGraph;

/**
 * An XDI $not policy, represented as an XDI subgraph.
 * 
 * @author markus
 */
public class PolicyNot extends Policy {

	private static final long serialVersionUID = 5732150467865911411L;

	protected PolicyNot(XdiSubGraph xdiSubGraph) {

		super(xdiSubGraph);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if an XDI subgraph is a valid XDI $not policy.
	 * @param xdiSubGraph The XDI subgraph to check.
	 * @return True if the XDI subgraph is a valid XDI $not policy.
	 */
	public static boolean isValid(XdiSubGraph xdiSubGraph) {

		if (xdiSubGraph instanceof XdiEntitySingleton)
			return ((XdiEntitySingleton) xdiSubGraph).getBaseArcXri().equals(XDIPolicyConstants.XRI_SS_NOT);
		else if (xdiSubGraph instanceof XdiEntityMember)
			return ((XdiEntityMember) xdiSubGraph).getParentCollection().getBaseArcXri().equals(XDIPolicyConstants.XRI_SS_NOT);

		return false;
	}

	/**
	 * Factory method that creates an XDI $and policy bound to a given XDI subgraph.
	 * @param xdiSubGraph The XDI subgraph that is an XDI root policy.
	 * @return The XDI $and policy.
	 */
	public static PolicyNot fromSubGraph(XdiSubGraph xdiSubGraph) {

		if (! isValid(xdiSubGraph)) return null;

		return new PolicyNot(xdiSubGraph);
	}

	/*
	 * Instance methods
	 */

	@Override
	public Boolean evaluateInternal(PolicyEvaluationContext policyEvaluationContext) {

		for (Iterator<Policy> policies = this.getPolicies(); policies.hasNext(); ) {

			Policy policy = policies.next();
			if (Boolean.FALSE.equals(policy.evaluate(policyEvaluationContext))) return Boolean.TRUE;
		}

		for (Iterator<Operator> operators = this.getOperators(); operators.hasNext(); ) {

			Operator operator = operators.next();
			for (Boolean result : operator.evaluate(policyEvaluationContext)) if (Boolean.FALSE.equals(result)) return Boolean.TRUE;
		}

		return Boolean.FALSE;
	}
}
