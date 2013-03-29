package xdi2.core.features.linkcontracts.policy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.Relation;
import xdi2.core.constants.XDIPolicyConstants;
import xdi2.core.features.linkcontracts.evaluation.PolicyEvaluationContext;
import xdi2.core.features.linkcontracts.operator.Operator;
import xdi2.core.features.multiplicity.XdiEntityCollection;
import xdi2.core.features.multiplicity.XdiElement;
import xdi2.core.features.multiplicity.XdiMember;
import xdi2.core.features.multiplicity.ContextFunction;
import xdi2.core.util.CopyUtil;
import xdi2.core.util.iterators.CompositeIterator;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;
import xdi2.core.util.iterators.SingleItemIterator;
import xdi2.core.xri3.XDI3SubSegment;

/**
 * An XDI policy, represented as a context node.
 * 
 * @author markus
 */
public abstract class Policy implements Serializable, Comparable<Policy> {

	private static final long serialVersionUID = 1604380462449272149L;

	private static final Logger log = LoggerFactory.getLogger(Policy.class);

	private ContextFunction xdiSubGraph;

	protected Policy(ContextFunction xdiSubGraph) {

		if (xdiSubGraph == null) throw new NullPointerException();

		this.xdiSubGraph = xdiSubGraph;
	}

	/**
	 * Checks if a context node is a valid XDI policy.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI policy.
	 */
	public static boolean isValid(ContextFunction contextNode) {

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
	public static Policy fromSubGraph(ContextFunction xdiSubGraph) {

		if (PolicyRoot.isValid(xdiSubGraph)) return PolicyRoot.fromSubGraph(xdiSubGraph);
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
	public ContextFunction getSubGraph() {

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
	 * Returns the policy XRI of the XDI operation (e.g. $and, $or).
	 * @return The policy XRI of the XDI operation.
	 */
	public XDI3SubSegment getPolicyXri() {

		if (this.getSubGraph() instanceof XdiMember)
			return ((XdiMember) this.getSubGraph()).getBaseArcXri();
		else if (this.getSubGraph() instanceof XdiElement)
			return ((XdiElement) this.getSubGraph()).getCollection().getBaseArcXri();

		return null;
	}

	/**
	 * Creates an XDI $and policy.
	 */
	public PolicyAnd createAndPolicy() {

		XdiMember policyAndEntitySingleton = this.getSubGraph().getEntitySingleton(XDIPolicyConstants.XRI_SS_AND, true);

		return PolicyAnd.fromSubGraph(policyAndEntitySingleton);
	}

	/**
	 * Creates an XDI $or policy.
	 */
	public PolicyOr createOrPolicy() {

		XdiMember policyOrEntitySingleton = this.getSubGraph().getEntitySingleton(XDIPolicyConstants.XRI_SS_OR, true);

		return PolicyOr.fromSubGraph(policyOrEntitySingleton);
	}

	/**
	 * Creates an XDI $not policy.
	 */
	public PolicyNot createNotPolicy() {

		XdiMember policyNotEntitySingleton = this.getSubGraph().getEntitySingleton(XDIPolicyConstants.XRI_SS_NOT, true);

		return PolicyNot.fromSubGraph(policyNotEntitySingleton);
	}

	/**
	 * Returns the XDI policies underneath this XDI policy.
	 */
	public Iterator<Policy> getPolicies() {

		List<Iterator<? extends Policy>> iterators = new ArrayList<Iterator<? extends Policy>> ();

		// add policies that are XDI entity singletons

		XdiMember policyAndEntitySingleton = this.getSubGraph().getEntitySingleton(XDIPolicyConstants.XRI_SS_AND, false);
		XdiMember policyOrEntitySingleton = this.getSubGraph().getEntitySingleton(XDIPolicyConstants.XRI_SS_OR, false);
		XdiMember policyNotEntitySingleton = this.getSubGraph().getEntitySingleton(XDIPolicyConstants.XRI_SS_NOT, false);

		if (policyAndEntitySingleton != null) iterators.add(new SingleItemIterator<Policy> (PolicyAnd.fromSubGraph(policyAndEntitySingleton)));
		if (policyOrEntitySingleton != null) iterators.add(new SingleItemIterator<Policy> (PolicyOr.fromSubGraph(policyOrEntitySingleton)));
		if (policyNotEntitySingleton != null) iterators.add(new SingleItemIterator<Policy> (PolicyNot.fromSubGraph(policyNotEntitySingleton)));

		// add policies that are XDI entity members

		XdiEntityCollection policyAndCollection = this.getSubGraph().getEntityCollection(XDIPolicyConstants.XRI_SS_AND, false);
		XdiEntityCollection policyOrCollection = this.getSubGraph().getEntityCollection(XDIPolicyConstants.XRI_SS_OR, false);
		XdiEntityCollection policyNotCollection = this.getSubGraph().getEntityCollection(XDIPolicyConstants.XRI_SS_NOT, false);

		if (policyAndCollection != null) iterators.add(new MappingEntityMemberPolicyAndIterator(policyAndCollection.members()));
		if (policyOrCollection != null) iterators.add(new MappingEntityMemberPolicyOrIterator(policyOrCollection.members()));
		if (policyNotCollection != null) iterators.add(new MappingEntityMemberPolicyNotIterator(policyNotCollection.members()));

		return new CompositeIterator<Policy> (iterators.iterator());
	}

	/**
	 * Returns the XDI operators underneath this XDI policy.
	 */
	public Iterator<Operator> getOperators() {

		// get all relations that are valid XDI operators

		Iterator<Relation> relations = this.getContextNode().getRelations();

		return new MappingRelationOperatorIterator(relations);
	}

	/**
	 * Adds an XDI operator to this XDI policy.
	 * @param arcXri The arc XRI of the XDI operator.
	 * @param statement The statement of the XDI condition.
	 */
	public Operator addOperator(Operator policyStatement) {

		Relation relation = CopyUtil.copyRelation(policyStatement.getRelation(), this.getContextNode(), null);

		return Operator.fromRelation(relation);
	}

	/**
	 * Checks if the XDI policy evaluates to true or false.
	 * @param policyEvaluationContext An object that can locate context nodes.
	 * @return True or false.
	 */
	public final Boolean evaluate(PolicyEvaluationContext policyEvaluationContext) {

		if (log.isDebugEnabled()) log.debug("Evaluating " + this.getClass().getSimpleName() + ": " + this.getContextNode());
		Boolean result = this.evaluateInternal(policyEvaluationContext);
		if (log.isDebugEnabled()) log.debug("Evaluated " + this.getClass().getSimpleName() + ": " + this.getContextNode() + ": " + result);

		return result;
	}

	protected abstract Boolean evaluateInternal(PolicyEvaluationContext policyEvaluationContext);

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

	/*
	 * Helper classes
	 */

	public static class MappingEntityMemberPolicyAndIterator extends NotNullIterator<PolicyAnd> {

		public MappingEntityMemberPolicyAndIterator(Iterator<XdiElement> xdiEntityMembers) {

			super(new MappingIterator<XdiElement, PolicyAnd> (xdiEntityMembers) {

				@Override
				public PolicyAnd map(XdiElement xdiEntityMember) {

					return PolicyAnd.fromSubGraph(xdiEntityMember);
				}
			});
		}
	}

	public static class MappingEntityMemberPolicyOrIterator extends NotNullIterator<PolicyOr> {

		public MappingEntityMemberPolicyOrIterator(Iterator<XdiElement> xdiEntityMembers) {

			super(new MappingIterator<XdiElement, PolicyOr> (xdiEntityMembers) {

				@Override
				public PolicyOr map(XdiElement xdiEntityMember) {

					return PolicyOr.fromSubGraph(xdiEntityMember);
				}
			});
		}
	}

	public static class MappingEntityMemberPolicyNotIterator extends NotNullIterator<PolicyNot> {

		public MappingEntityMemberPolicyNotIterator(Iterator<XdiElement> xdiEntityMembers) {

			super(new MappingIterator<XdiElement, PolicyNot> (xdiEntityMembers) {

				@Override
				public PolicyNot map(XdiElement xdiEntityMember) {

					return PolicyNot.fromSubGraph(xdiEntityMember);
				}
			});
		}
	}

	public static class MappingRelationOperatorIterator extends NotNullIterator<Operator> {

		public MappingRelationOperatorIterator(Iterator<Relation> relations) {

			super(new MappingIterator<Relation, Operator> (relations) {

				@Override
				public Operator map(Relation relation) {

					return Operator.fromRelation(relation);
				}
			});
		}
	}
}
