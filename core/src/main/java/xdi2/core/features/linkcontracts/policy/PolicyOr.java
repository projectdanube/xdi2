package xdi2.core.features.linkcontracts.policy;

import java.util.Iterator;

import xdi2.core.constants.XDIPolicyConstants;
import xdi2.core.features.contextfunctions.XdiElement;
import xdi2.core.features.contextfunctions.XdiMember;
import xdi2.core.features.contextfunctions.XdiSubGraph;
import xdi2.core.features.linkcontracts.evaluation.PolicyEvaluationContext;
import xdi2.core.features.linkcontracts.operator.Operator;

/**
 * An XDI $or policy, represented as an XDI subgraph.
 * 
 * @author markus
 */
public class PolicyOr extends Policy {

	private static final long serialVersionUID = 5732150401265911411L;

	protected PolicyOr(XdiSubGraph xdiSubGraph) {

		super(xdiSubGraph);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if an XDI subgraph is a valid XDI $or policy.
	 * @param xdiSubGraph The XDI subgraph to check.
	 * @return True if the XDI subgraph is a valid XDI $or policy.
	 */
	public static boolean isValid(XdiSubGraph xdiSubGraph) {

		if (xdiSubGraph instanceof XdiMember)
			return ((XdiMember) xdiSubGraph).getBaseArcXri().equals(XDIPolicyConstants.XRI_SS_OR);
		else if (xdiSubGraph instanceof XdiElement)
			return ((XdiElement) xdiSubGraph).getXdiMember().getBaseArcXri().equals(XDIPolicyConstants.XRI_SS_OR);

		return false;
	}

	/**
	 * Factory method that creates an XDI $or policy bound to a XDI subgraph
	 * @param xdiSubGraph The XDI subgraph that is an XDI root policy.
	 * @return The XDI $or policy.
	 */
	public static PolicyOr fromSubGraph(XdiSubGraph xdiSubGraph) {

		if (! isValid(xdiSubGraph)) return null;

		return new PolicyOr(xdiSubGraph);
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
