package xdi2.core.features.linkcontracts.policy;

import java.util.Iterator;

import xdi2.core.constants.XDIPolicyConstants;
import xdi2.core.features.contextfunctions.XdiElement;
import xdi2.core.features.contextfunctions.XdiCollection;
import xdi2.core.features.contextfunctions.XdiSubGraph;
import xdi2.core.features.linkcontracts.evaluation.PolicyEvaluationContext;
import xdi2.core.features.linkcontracts.operator.Operator;

/**
 * An XDI $and policy, represented as an XDI subgraph.
 * 
 * @author markus
 */
public class PolicyAnd extends Policy {

	private static final long serialVersionUID = 5732150498065911411L;

	protected PolicyAnd(XdiSubGraph xdiSubGraph) {

		super(xdiSubGraph);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if an XDI subgraph is a valid XDI $and policy.
	 * @param xdiSubGraph The XDI subgraph to check.
	 * @return True if the XDI subgraph is a valid XDI $and policy.
	 */
	public static boolean isValid(XdiSubGraph xdiSubGraph) {

		if (xdiSubGraph instanceof XdiCollection)
			return ((XdiCollection) xdiSubGraph).getBaseArcXri().equals(XDIPolicyConstants.XRI_SS_AND);
		else if (xdiSubGraph instanceof XdiElement)
			return ((XdiElement) xdiSubGraph).getXdiMember().getBaseArcXri().equals(XDIPolicyConstants.XRI_SS_AND);

		return false;
	}

	/**
	 * Factory method that creates an XDI $and policy bound to a given XDI subgraph.
	 * @param xdiSubGraph The XDI subgraph that is an XDI root policy.
	 * @return The XDI $and policy.
	 */
	public static PolicyAnd fromSubGraph(XdiSubGraph xdiSubGraph) {

		if (! isValid(xdiSubGraph)) return null;

		return new PolicyAnd(xdiSubGraph);
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
