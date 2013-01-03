package xdi2.core.features.linkcontracts.policy;

import java.util.Iterator;

import xdi2.core.constants.XDILinkContractConstants;
import xdi2.core.features.linkcontracts.LinkContract;
import xdi2.core.features.linkcontracts.evaluation.PolicyEvaluationContext;
import xdi2.core.features.linkcontracts.policystatement.PolicyStatement;
import xdi2.core.features.multiplicity.XdiEntityMember;
import xdi2.core.features.multiplicity.XdiEntitySingleton;
import xdi2.core.features.multiplicity.XdiSubGraph;

/**
 * An XDI root policy, represented as an XDI subgraph.
 * 
 * @author markus
 */
public final class PolicyRoot extends Policy {

	private static final long serialVersionUID = -9212794041490417047L;

	private LinkContract linkContract;

	protected PolicyRoot(LinkContract linkContract, XdiSubGraph xdiSubGraph) {

		super(xdiSubGraph);

		if (linkContract == null) throw new NullPointerException();

		this.linkContract = linkContract;
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if an XDI subgraph is a valid XDI root policy.
	 * @param xdiSubGraph The XDI subgraph to check.
	 * @return True if the XDI subgraph is a valid XDI root policy.
	 */
	public static boolean isValid(XdiSubGraph xdiSubGraph) {

		if (xdiSubGraph instanceof XdiEntitySingleton)
			return ((XdiEntitySingleton) xdiSubGraph).getBaseArcXri().equals(XDILinkContractConstants.XRI_SS_IF);
		else if (xdiSubGraph instanceof XdiEntityMember)
			return ((XdiEntityMember) xdiSubGraph).getParentCollection().getBaseArcXri().equals(XDILinkContractConstants.XRI_SS_IF);

		return false;
	}

	/**
	 * Factory method that creates an XDI root policy bound to a given XDI subgraph.
	 * @param linkContract The XDI link contract to which this XDI policy root belongs.
	 * @param xdiSubGraph The XDI subgraph that is an XDI root policy.
	 * @return The XDI root policy.
	 */
	public static PolicyRoot fromLinkContractAndSubGraph(LinkContract linkContract, XdiSubGraph xdiSubGraph) {

		if (! isValid(xdiSubGraph)) return null;

		return new PolicyRoot(linkContract, xdiSubGraph);
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
