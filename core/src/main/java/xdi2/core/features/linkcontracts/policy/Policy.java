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
import xdi2.core.features.contextfunctions.XdiElement;
import xdi2.core.features.contextfunctions.XdiCollection;
import xdi2.core.features.contextfunctions.XdiSubGraph;
import xdi2.core.features.linkcontracts.evaluation.PolicyEvaluationContext;
import xdi2.core.features.linkcontracts.operator.Operator;
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

		Policy policy;

		if ((policy = PolicyRoot.fromSubGraph(xdiSubGraph)) != null) return policy;
		if ((policy = PolicyAnd.fromSubGraph(xdiSubGraph)) != null) return policy;
		if ((policy = PolicyOr.fromSubGraph(xdiSubGraph)) != null) return policy;
		if ((policy = PolicyNot.fromSubGraph(xdiSubGraph)) != null) return policy;

		return null;
	}

	/**
	 * Factory method that casts a Policy to the right subclass, e.g. to a PolicyAnd.
	 * @param policy The Policy to be cast.
	 * @return The casted Policy.
	 */
	public static Policy castCondition(Policy policy) {

		if (policy == null) return null;

		return fromSubGraph(policy.getXdiSubGraph());
	}

	/*
	 * Instance methods
	 */

	/**
	 * Returns the underlying XDI subgraph to which this XDI policy is bound.
	 * @return An XDI subgraph that represents the XDI policy.
	 */
	public XdiSubGraph getXdiSubGraph() {

		return this.xdiSubGraph;
	}

	/**
	 * Returns the underlying context node to which this XDI policy is bound.
	 * @return A context node that represents the XDI policy.
	 */
	public ContextNode getContextNode() {

		return this.getXdiSubGraph().getContextNode();
	}

	/**
	 * Returns the policy XRI of the XDI operation (e.g. $and, $or).
	 * @return The policy XRI of the XDI operation.
	 */
	public XDI3SubSegment getPolicyXri() {

		if (this.getXdiSubGraph() instanceof XdiCollection)
			return ((XdiCollection) this.getXdiSubGraph()).getBaseArcXri();
		else if (this.getXdiSubGraph() instanceof XdiElement)
			return ((XdiElement) this.getXdiSubGraph()).getXdiMember().getBaseArcXri();

		return null;
	}

	/**
	 * Creates an XDI $and policy.
	 */
	public PolicyAnd createAndPolicy() {

		XdiCollection policyAndMember = this.getXdiSubGraph().getXdiMember(XDIPolicyConstants.XRI_SS_AND, true);

		return PolicyAnd.fromSubGraph(policyAndMember);
	}

	/**
	 * Creates an XDI $or policy.
	 */
	public PolicyOr createOrPolicy() {

		XdiCollection policyOrMember = this.getXdiSubGraph().getXdiMember(XDIPolicyConstants.XRI_SS_OR, true);

		return PolicyOr.fromSubGraph(policyOrMember);
	}

	/**
	 * Creates an XDI $not policy.
	 */
	public PolicyNot createNotPolicy() {

		XdiCollection policyNotMember = this.getXdiSubGraph().getXdiMember(XDIPolicyConstants.XRI_SS_NOT, true);

		return PolicyNot.fromSubGraph(policyNotMember);
	}

	/**
	 * Returns the XDI policies underneath this XDI policy.
	 */
	public Iterator<Policy> getPolicies() {

		List<Iterator<? extends Policy>> iterators = new ArrayList<Iterator<? extends Policy>> ();

		XdiCollection policyAndMember = this.getXdiSubGraph().getXdiMember(XDIPolicyConstants.XRI_SS_AND, false);
		XdiCollection policyOrMember = this.getXdiSubGraph().getXdiMember(XDIPolicyConstants.XRI_SS_OR, false);
		XdiCollection policyNotMember = this.getXdiSubGraph().getXdiMember(XDIPolicyConstants.XRI_SS_NOT, false);

		// add policies (either elements, or a member)

		Iterator<XdiElement> policyAndElements = policyAndMember.elements();
		Iterator<XdiElement> policyOrElements = policyOrMember.elements();
		Iterator<XdiElement> policyNotElements = policyNotMember.elements();

		if (policyAndElements.hasNext()) 
			iterators.add(new MappingXdiElementPolicyAndIterator(policyAndElements));
		else
			iterators.add(new SingleItemIterator<PolicyAnd> (PolicyAnd.fromSubGraph(policyAndMember)));

		if (policyOrElements.hasNext()) 
			iterators.add(new MappingXdiElementPolicyOrIterator(policyOrElements));
		else
			iterators.add(new SingleItemIterator<PolicyOr> (PolicyOr.fromSubGraph(policyOrMember)));

		if (policyNotElements.hasNext()) 
			iterators.add(new MappingXdiElementPolicyNotIterator(policyNotElements));
		else
			iterators.add(new SingleItemIterator<PolicyNot> (PolicyNot.fromSubGraph(policyNotMember)));

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

	public static class MappingXdiElementPolicyAndIterator extends NotNullIterator<PolicyAnd> {

		public MappingXdiElementPolicyAndIterator(Iterator<XdiElement> xdiElements) {

			super(new MappingIterator<XdiElement, PolicyAnd> (xdiElements) {

				@Override
				public PolicyAnd map(XdiElement xdiElement) {

					return PolicyAnd.fromSubGraph(xdiElement);
				}
			});
		}
	}

	public static class MappingXdiElementPolicyOrIterator extends NotNullIterator<PolicyOr> {

		public MappingXdiElementPolicyOrIterator(Iterator<XdiElement> xdiElements) {

			super(new MappingIterator<XdiElement, PolicyOr> (xdiElements) {

				@Override
				public PolicyOr map(XdiElement xdiElement) {

					return PolicyOr.fromSubGraph(xdiElement);
				}
			});
		}
	}

	public static class MappingXdiElementPolicyNotIterator extends NotNullIterator<PolicyNot> {

		public MappingXdiElementPolicyNotIterator(Iterator<XdiElement> xdiElements) {

			super(new MappingIterator<XdiElement, PolicyNot> (xdiElements) {

				@Override
				public PolicyNot map(XdiElement xdiElement) {

					return PolicyNot.fromSubGraph(xdiElement);
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
