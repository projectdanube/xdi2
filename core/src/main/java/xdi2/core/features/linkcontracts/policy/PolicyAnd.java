package xdi2.core.features.linkcontracts.policy;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.constants.XDILinkContractConstants;
import xdi2.core.features.linkcontracts.evaluation.PolicyEvaluationContext;
import xdi2.core.features.linkcontracts.policystatement.PolicyStatement;

/**
 * An XDI $and policy, represented as a context node.
 * 
 * @author markus
 */
public class PolicyAnd extends Policy {

	private static final long serialVersionUID = 5732150498065911411L;

	protected PolicyAnd(ContextNode contextNode) {

		super(contextNode);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI $and policy.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI $and policy.
	 */
	public static boolean isValid(ContextNode contextNode) {

		if (! XDILinkContractConstants.XRI_SS_AND.equals(contextNode.getArcXri())) return false;

		return true;
	}

	/**
	 * Factory method that creates an XDI $and policy bound to a given context node.
	 * @param contextNode The context node that is an XDI $and policy.
	 * @return The XDI $and policy.
	 */
	public static PolicyAnd fromContextNode(ContextNode contextNode) {

		if (! isValid(contextNode)) return null;

		return new PolicyAnd(contextNode);
	}

	/*
	 * Instance methods
	 */

	@Override
	public boolean evaluateInternal(PolicyEvaluationContext policyEvaluationContext) {

		for (Iterator<Policy> policies = this.getPolicies(); policies.hasNext(); ) {

			Policy policy = policies.next();
			if (false == policy.evaluate(policyEvaluationContext)) return false;
		}

		for (Iterator<PolicyStatement> policyStatements = this.getPolicyStatements(); policyStatements.hasNext(); ) {

			PolicyStatement policyStatement = policyStatements.next();
			if (false == policyStatement.evaluate(policyEvaluationContext)) return false;
		}

		return true;
	}
}
