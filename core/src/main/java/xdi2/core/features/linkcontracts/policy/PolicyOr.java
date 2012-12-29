package xdi2.core.features.linkcontracts.policy;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.constants.XDILinkContractConstants;
import xdi2.core.features.linkcontracts.evaluation.PolicyEvaluationContext;
import xdi2.core.features.linkcontracts.policystatement.PolicyStatement;

/**
 * An XDI $or policy, represented as a context node.
 * 
 * @author markus
 */
public class PolicyOr extends Policy {

	private static final long serialVersionUID = 5732150401265911411L;

	protected PolicyOr(ContextNode contextNode) {

		super(contextNode);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI $or policy.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI $or policy.
	 */
	public static boolean isValid(ContextNode contextNode) {

		if (! XDILinkContractConstants.XRI_SS_OR.equals(contextNode.getArcXri())) return false;

		return true;
	}

	/**
	 * Factory method that creates an XDI $or policy bound to a given context node.
	 * @param contextNode The context node that is an XDI $or policy.
	 * @return The XDI $or policy.
	 */
	public static PolicyOr fromContextNode(ContextNode contextNode) {

		if (! isValid(contextNode)) return null;

		return new PolicyOr(contextNode);
	}

	/*
	 * Instance methods
	 */

	@Override
	public boolean evaluateInternal(PolicyEvaluationContext policyEvaluationContext) {

		for (Iterator<Policy> policies = this.getPolicies(); policies.hasNext(); ) {

			Policy policy = policies.next();
			if (true == policy.evaluate(policyEvaluationContext)) return true;
		}

		for (Iterator<PolicyStatement> policyStatements = this.getPolicyStatements(); policyStatements.hasNext(); ) {

			PolicyStatement policyStatement = policyStatements.next();
			if (true == policyStatement.evaluate(policyEvaluationContext)) return true;
		}

		return false;
	}
}
