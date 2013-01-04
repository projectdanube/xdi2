package xdi2.core.features.linkcontracts.policy;

import java.util.Iterator;

import xdi2.core.constants.XDILinkContractConstants;
import xdi2.core.features.linkcontracts.evaluation.PolicyEvaluationContext;
import xdi2.core.features.linkcontracts.policystatement.PolicyStatement;
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
			return ((XdiEntitySingleton) xdiSubGraph).getBaseArcXri().equals(XDILinkContractConstants.XRI_SS_NOT);
		else if (xdiSubGraph instanceof XdiEntityMember)
			return ((XdiEntityMember) xdiSubGraph).getParentCollection().getBaseArcXri().equals(XDILinkContractConstants.XRI_SS_NOT);

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
	public boolean evaluateInternal(PolicyEvaluationContext policyEvaluationContext) {

		for (Iterator<Policy> policies = this.getPolicies(); policies.hasNext(); ) {

			Policy policy = policies.next();
			if (false == policy.evaluate(policyEvaluationContext)) return true;
		}

		for (Iterator<PolicyStatement> policyStatements = this.getPolicyStatements(); policyStatements.hasNext(); ) {

			PolicyStatement policyStatement = policyStatements.next();
			if (false == policyStatement.evaluate(policyEvaluationContext)) return true;
		}

		return false;
	}
}
