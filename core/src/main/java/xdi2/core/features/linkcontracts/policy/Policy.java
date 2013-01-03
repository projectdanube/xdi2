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
import xdi2.core.features.multiplicity.XdiCollection;
import xdi2.core.features.multiplicity.XdiEntityMember;
import xdi2.core.features.multiplicity.XdiEntitySingleton;
import xdi2.core.features.multiplicity.XdiSubGraph;
import xdi2.core.util.CopyUtil;
import xdi2.core.util.iterators.CompositeIterator;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;
import xdi2.core.util.iterators.SingleItemIterator;

/**
 * An XDI policy, represented as a context node.
 * 
 * @author markus
 */
public abstract class Policy implements Serializable, Comparable<Policy> {

	private static final long serialVersionUID = 1604380462449272149L;

	private static final Logger log = LoggerFactory.getLogger(Policy.class);

	private XdiSubGraph xdiSubGraph;

	protected Policy(XdiSubGraph xdiSubGraph) {

		if (xdiSubGraph == null) throw new NullPointerException();

		this.xdiSubGraph = xdiSubGraph;
	}

	/**
	 * Checks if a context node is a valid XDI policy.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI policy.
	 */
	public static boolean isValid(XdiSubGraph contextNode) {

		return
				PolicyRoot.isValid(contextNode) ||
				PolicyAnd.isValid(contextNode) ||
				PolicyOr.isValid(contextNode) ||
				PolicyNot.isValid(contextNode);
	}

	/**
	 * Factory method that creates an XDI policy bound to a given XDI subgraph.
	 * @param xdiSubGraph The XDI subgraph that is an XDI policy.
	 * @return The XDI policy.
	 */
	public static Policy fromSubGraph(XdiSubGraph xdiSubGraph) {

		if (PolicyRoot.isValid(xdiSubGraph)) return PolicyRoot.fromLinkContractAndSubGraph(null, xdiSubGraph);
		if (PolicyAnd.isValid(xdiSubGraph)) return PolicyAnd.fromSubGraph(xdiSubGraph);
		if (PolicyOr.isValid(xdiSubGraph)) return PolicyOr.fromSubGraph(xdiSubGraph);
		if (PolicyNot.isValid(xdiSubGraph)) return PolicyNot.fromSubGraph(xdiSubGraph);

		return null;
	}

	/**
	 * Factory method that casts a Policy to the right subclass, e.g. to a PolicyAnd.
	 * @param policy The Policy to be cast.
	 * @return The casted Policy.
	 */
	public static Policy castCondition(Policy policy) {

		if (policy == null) return null;

		return fromSubGraph(policy.getSubGraph());
	}

	/*
	 * Instance methods
	 */

	/**
	 * Returns the underlying XDI subgraph to which this XDI policy is bound.
	 * @return An XDI subgraph that represents the XDI policy.
	 */
	public XdiSubGraph getSubGraph() {

		return this.xdiSubGraph;
	}

	/**
	 * Returns the underlying context node to which this XDI policy is bound.
	 * @return A context node that represents the XDI policy.
	 */
	public ContextNode getContextNode() {

		return this.getSubGraph().getContextNode();
	}

	/**
	 * Returns an XDI $and policy underneath this XDI policy.
	 * @return An XDI $and policy.
	 */
	public Iterator<PolicyAnd> getPolicyAnd() {

		List<Iterator<PolicyAnd>> iterators = new ArrayList<Iterator<PolicyAnd>> ();

		XdiEntitySingleton policyAndEntitySingleton = this.getSubGraph().getEntitySingleton(XDILinkContractConstants.XRI_SS_AND, false);
		if (policyAndEntitySingleton != null) iterators.add(new SingleItemIterator<PolicyAnd> (PolicyAnd.fromSubGraph(policyAndEntitySingleton)));

		XdiCollection policyAndCollection = this.getSubGraph().getCollection(XDILinkContractConstants.XRI_SS_AND, false);
		if (policyAndCollection != null) iterators.add(new MappingIterator<XdiEntityMember, PolicyAnd> (policyAndCollection.entities()) {

			@Override
			public PolicyAnd map(XdiEntityMember item) {

				return PolicyAnd.fromSubGraph(item);
			}
		});

		return new CompositeIterator<PolicyAnd> (iterators.iterator());
	}

	/**
	 * Returns an XDI $or policy underneath this XDI policy.
	 * @param create Whether to create an XDI $or policy if it does not exist.
	 * @return An XDI $or policy.
	 */
	public PolicyOr getPolicyOr(boolean create) {

		XdiSubGraph contextNode = this.getContextNode().getContextNode(XDILinkContractConstants.XRI_SS_OR);
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

		XdiSubGraph contextNode = this.getContextNode().getContextNode(XDILinkContractConstants.XRI_SS_NOT);
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
