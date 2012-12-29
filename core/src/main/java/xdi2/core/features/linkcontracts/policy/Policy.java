package xdi2.core.features.linkcontracts.policy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.Relation;
import xdi2.core.constants.XDILinkContractConstants;
import xdi2.core.features.linkcontracts.evaluation.PolicyEvaluationContext;
import xdi2.core.features.linkcontracts.policystatement.PolicyStatement;
import xdi2.core.util.CopyUtil;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;

/**
 * An XDI policy, represented as a context node.
 * 
 * @author markus
 */
public abstract class Policy implements Serializable, Comparable<Policy> {

	private static final long serialVersionUID = 1604380462449272149L;

	private static final Logger log = LoggerFactory.getLogger(Policy.class);

	private ContextNode contextNode;

	protected Policy(ContextNode contextNode) {

		if (contextNode == null) throw new NullPointerException();

		this.contextNode = contextNode;
	}

	/**
	 * Checks if a context node is a valid XDI policy.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI policy.
	 */
	public static boolean isValid(ContextNode contextNode) {

		return
				PolicyRoot.isValid(contextNode) ||
				PolicyAnd.isValid(contextNode) ||
				PolicyOr.isValid(contextNode) ||
				PolicyNot.isValid(contextNode);
	}

	/**
	 * Factory method that creates an XDI policy bound to a given context node.
	 * @param contextNode The context node that is an XDI policy.
	 * @return The XDI policy.
	 */
	public static Policy fromContextNode(ContextNode contextNode) {

		if (PolicyRoot.isValid(contextNode)) return PolicyRoot.fromLinkContractAndContextNode(null, contextNode);
		if (PolicyAnd.isValid(contextNode)) return PolicyAnd.fromContextNode(contextNode);
		if (PolicyOr.isValid(contextNode)) return PolicyOr.fromContextNode(contextNode);
		if (PolicyNot.isValid(contextNode)) return PolicyNot.fromContextNode(contextNode);

		return null;
	}

	/**
	 * Factory method that casts a Policy to the right subclass, e.g. to a PolicyAnd.
	 * @param policy The Policy to be cast.
	 * @return The casted Policy.
	 */
	public static Policy castCondition(Policy policy) {

		if (policy == null) return null;

		return fromContextNode(policy.getContextNode());
	}

	/*
	 * Instance methods
	 */

	/**
	 * Returns the underlying context node to which this XDI policy is bound.
	 * @return A context node that represents the XDI policy.
	 */
	public ContextNode getContextNode() {

		return this.contextNode;
	}

	/**
	 * Returns an XDI $and policy underneath this XDI policy.
	 * @param create Whether to create an XDI $and policy if it does not exist.
	 * @return An XDI $and policy.
	 */
	public PolicyAnd getPolicyAnd(boolean create) {

		ContextNode contextNode = this.getContextNode().getContextNode(XDILinkContractConstants.XRI_SS_AND);
		if (contextNode == null && create) contextNode = this.getContextNode().createContextNode(XDILinkContractConstants.XRI_SS_AND);
		if (contextNode == null) return null;

		return PolicyAnd.fromContextNode(contextNode);
	}

	/**
	 * Returns an XDI $or policy underneath this XDI policy.
	 * @param create Whether to create an XDI $or policy if it does not exist.
	 * @return An XDI $or policy.
	 */
	public PolicyOr getPolicyOr(boolean create) {

		ContextNode contextNode = this.getContextNode().getContextNode(XDILinkContractConstants.XRI_SS_OR);
		if (contextNode == null && create) contextNode = this.getContextNode().createContextNode(XDILinkContractConstants.XRI_SS_OR);
		if (contextNode == null) return null;

		return PolicyOr.fromContextNode(contextNode);
	}

	/**
	 * Returns an XDI $not policy underneath this XDI policy.
	 * @param create Whether to create an XDI $not policy if it does not exist.
	 * @return An XDI $not policy.
	 */
	public PolicyNot getPolicyNot(boolean create) {

		ContextNode contextNode = this.getContextNode().getContextNode(XDILinkContractConstants.XRI_SS_NOT);
		if (contextNode == null && create) contextNode = this.getContextNode().createContextNode(XDILinkContractConstants.XRI_SS_NOT);
		if (contextNode == null) return null;

		return PolicyNot.fromContextNode(contextNode);
	}

	/**
	 * Returns the XDI policies underneath this XDI policy.
	 */
	public Iterator<Policy> getPolicies() {

		List<Policy> policies = new ArrayList<Policy> ();

		PolicyAnd policyAnd = this.getPolicyAnd(false);
		PolicyOr policyOr = this.getPolicyOr(false);
		PolicyNot policyNot = this.getPolicyNot(false);

		if (policyAnd != null) policies.add(policyAnd);
		if (policyOr != null) policies.add(policyOr);
		if (policyNot != null) policies.add(policyNot);

		return policies.iterator();
	}

	/**
	 * Returns the XDI policy statements underneath this XDI policy.
	 */
	public Iterator<PolicyStatement> getPolicyStatements() {

		return new NotNullIterator<PolicyStatement> (new MappingIterator<Relation, PolicyStatement> (this.getContextNode().getRelations()) {

			@Override
			public PolicyStatement map(Relation relation) {

				return PolicyStatement.fromRelation(relation);
			}
		});
	}

	/**
	 * Adds an XDI policy statement to this XDI policy.
	 * @param arcXri The arc XRI of the XDI policy statement.
	 * @param statement The statement of the XDI condition.
	 */
	public PolicyStatement addPolicyStatement(PolicyStatement policyStatement) {

		Relation relation = CopyUtil.copyRelation(policyStatement.getRelation(), this.getContextNode(), null);

		return PolicyStatement.fromRelation(relation);
	}

	/**
	 * Checks if the XDI policy evaluates to true or false.
	 * @param policyEvaluationContext An object that can locate context nodes.
	 * @return True or false.
	 */
	public final boolean evaluate(PolicyEvaluationContext policyEvaluationContext) {

		if (log.isDebugEnabled()) log.debug("Evaluating " + this.getClass().getSimpleName() + ": " + this.getContextNode());
		boolean result = this.evaluateInternal(policyEvaluationContext);
		if (log.isDebugEnabled()) log.debug("Evaluated " + this.getClass().getSimpleName() + ": " + this.getContextNode() + ": " + result);

		return result;
	}

	protected abstract boolean evaluateInternal(PolicyEvaluationContext policyEvaluationContext);

	/*
	 * Object methods
	 */

	@Override
	public String toString() {

		return this.getContextNode().toString();
	}

	@Override
	public boolean equals(Object object) {

		if (object == null || ! (object instanceof Policy)) return false;
		if (object == this) return true;

		Policy other = (Policy) object;

		return this.getContextNode().equals(other.getContextNode());
	}

	@Override
	public int hashCode() {

		int hashCode = 1;

		hashCode = (hashCode * 31) + this.getContextNode().hashCode();

		return hashCode;
	}

	@Override
	public int compareTo(Policy other) {

		if (other == this || other == null) return 0;

		return this.getContextNode().compareTo(other.getContextNode());
	}
}
