package xdi2.core.features.linkcontracts.policy;

import java.util.Iterator;

import xdi2.core.constants.XDILinkContractConstants;
import xdi2.core.features.linkcontracts.evaluation.PolicyEvaluationContext;
import xdi2.core.features.linkcontracts.policystatement.PolicyStatement;
import xdi2.core.features.multiplicity.XdiEntityMember;
import xdi2.core.features.multiplicity.XdiEntitySingleton;
import xdi2.core.features.multiplicity.XdiSubGraph;

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

		if (xdiSubGraph instanceof XdiEntitySingleton)
			return ((XdiEntitySingleton) xdiSubGraph).getBaseArcXri().equals(XDILinkContractConstants.XRI_SS_OR);
		else if (xdiSubGraph instanceof XdiEntityMember)
			return ((XdiEntityMember) xdiSubGraph).getParentCollection().getBaseArcXri().equals(XDILinkContractConstants.XRI_SS_OR);

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
