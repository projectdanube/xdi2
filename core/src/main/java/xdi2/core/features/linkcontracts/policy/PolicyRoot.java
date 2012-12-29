package xdi2.core.features.linkcontracts.policy;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.constants.XDILinkContractConstants;
import xdi2.core.features.linkcontracts.LinkContract;
import xdi2.core.features.linkcontracts.evaluation.PolicyEvaluationContext;
import xdi2.core.features.linkcontracts.policystatement.PolicyStatement;

/**
 * An XDI root policy, represented as a context node.
 * 
 * @author markus
 */
public final class PolicyRoot extends Policy {

	private static final long serialVersionUID = -9212794041490417047L;

	private LinkContract linkContract;

	protected PolicyRoot(LinkContract linkContract, ContextNode contextNode) {

		super(contextNode);

		if (linkContract == null) throw new NullPointerException();

		this.linkContract = linkContract;
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI root policy.
	 * 
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI root policy.
	 */
	public static boolean isValid(ContextNode contextNode) {

		if (! XDILinkContractConstants.XRI_SS_IF.equals(contextNode.getArcXri())) return false;

		return true;
	}

	/**
	 * Factory method that creates an XDI root policy bound to a given context node.
	 * @param linkContract The XDI link contract to which this XDI policy root belongs.
	 * @param contextNode The context node that is an XDI root policy.
	 * @return The XDI root policy.
	 */
	public static PolicyRoot fromLinkContractAndContextNode(LinkContract linkContract, ContextNode contextNode) {

		if (!isValid(contextNode)) return null;

		return new PolicyRoot(linkContract, contextNode);
	}

	/*
	 * Instance methods
	 */

	/**
	 * Returns the XDI link contract to which this XDI root policy belongs.
	 * @return An XDI link contract.
	 */
	public LinkContract getLinkContract() {

		return this.linkContract;
	}

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
