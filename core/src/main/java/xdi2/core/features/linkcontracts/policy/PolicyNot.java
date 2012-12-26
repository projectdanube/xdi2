package xdi2.core.features.linkcontracts.policy;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.constants.XDILinkContractConstants;
import xdi2.core.features.linkcontracts.policystatement.PolicyStatement;
import xdi2.core.util.locator.ContextNodeLocator;

/**
 * An XDI $not policy, represented as a context node.
 * 
 * @author markus
 */
public class PolicyNot extends Policy {

	private static final long serialVersionUID = 5732150467865911411L;

	protected PolicyNot(ContextNode contextNode) {

		super(contextNode);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI $not policy.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI $not policy.
	 */
	public static boolean isValid(ContextNode contextNode) {

		if (! XDILinkContractConstants.XRI_SS_NOT.equals(contextNode.getArcXri())) return false;

		return true;
	}

	/**
	 * Factory method that creates an XDI $not policy bound to a given context node.
	 * @param contextNode The context node that is an XDI $not policy.
	 * @return The XDI $not policy.
	 */
	public static PolicyNot fromContextNode(ContextNode contextNode) {

		if (! isValid(contextNode)) return null;

		return new PolicyNot(contextNode);
	}

	/*
	 * Instance methods
	 */

	@Override
	public boolean evaluateInternal(ContextNodeLocator contextNodeLocator) {

		for (Iterator<Policy> policies = this.getPolicies(); policies.hasNext(); ) {

			Policy policy = policies.next();
			if (true == policy.evaluate(contextNodeLocator)) return false;
		}

		for (Iterator<PolicyStatement> policyStatements = this.getPolicyStatements(); policyStatements.hasNext(); ) {

			PolicyStatement policyStatement = policyStatements.next();
			if (true == policyStatement.evaluate(contextNodeLocator)) return false;
		}

		return true;
	}
}
