package xdi2.core.features.linkcontracts.operator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import xdi2.core.Relation;
import xdi2.core.features.contextfunctions.XdiAbstractEntity;
import xdi2.core.features.linkcontracts.evaluation.PolicyEvaluationContext;
import xdi2.core.features.linkcontracts.policy.Policy;
import xdi2.core.features.roots.XdiInnerRoot;
import xdi2.core.features.roots.XdiLocalRoot;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;

/**
 * An XDI generic operator, represented as a relation.
 * 
 * @author markus
 */
public class GenericOperator extends Operator {

	private static final long serialVersionUID = 4296419491079293469L;

	protected GenericOperator(Relation relation) {

		super(relation);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a relation is a valid XDI generic operator.
	 * @param relation The relation to check.
	 * @return True if the relation is a valid XDI generic operator.
	 */
	public static boolean isValid(Relation relation) {

		if (! XdiAbstractEntity.isValid(relation.getContextNode())) return false;
		if (! Policy.isValid(XdiAbstractEntity.fromContextNode(relation.getContextNode()))) return false;

		return true;
	}

	/**
	 * Factory method that creates an XDI generic policy statement bound to a given relation.
	 * @param relation The relation that is an XDI generic operator.
	 * @return The XDI generic operator.
	 */
	public static GenericOperator fromRelation(Relation relation) {

		if (! isValid(relation)) return null;

		return new GenericOperator(relation);
	}

	public static GenericOperator createGenericOperator(Policy policy, XDI3Segment arcXri, XDI3Segment targetContextNodeXri) {

		if (policy == null) throw new NullPointerException();

		Relation relation = policy.getContextNode().createRelation(arcXri, targetContextNodeXri);

		return fromRelation(relation);
	}

	public static GenericOperator createGenericOperator(Policy policy, XDI3Segment arcXri, XDI3Statement relativeStatement) {

		if (policy == null) throw new NullPointerException();

		XdiInnerRoot xdiInnerRoot = XdiLocalRoot.findLocalRoot(policy.getContextNode().getGraph()).findInnerRoot(policy.getContextNode().getXri(), arcXri, true);

		xdiInnerRoot.createRelativeStatement(relativeStatement);

		return fromRelation(xdiInnerRoot.getPredicateRelation());
	}

	/*
	 * Instance methods
	 */

	@Override
	public Boolean[] evaluateInternal(PolicyEvaluationContext policyEvaluationContext) {

		XdiInnerRoot innerRoot = XdiInnerRoot.fromContextNode(this.getRelation().follow());

		if (innerRoot == null) {

			for (Iterator<Relation> policyEvaluationContextRelations = policyEvaluationContext.getRelations(this.getRelation().getArcXri()); policyEvaluationContextRelations.hasNext(); ) {

				Relation policyEvaluationContextRelation = policyEvaluationContextRelations.next();
				XdiInnerRoot policyEvaluationContextInnerRoot = XdiInnerRoot.fromContextNode(policyEvaluationContextRelation.follow());
				if (policyEvaluationContextInnerRoot != null) continue;

				if (policyEvaluationContextRelation.getTargetContextNodeXri().equals(this.getRelation().getTargetContextNodeXri())) return new Boolean[] { Boolean.TRUE };
			}

			return new Boolean[] { Boolean.FALSE };
		} else {

			List<Boolean> results = new ArrayList<Boolean> ();

			for (Iterator<XDI3Statement> relativeStatements = innerRoot.getRelativeStatements(true); relativeStatements.hasNext(); ) {

				XDI3Statement statementXri = relativeStatements.next();

				for (Iterator<Relation> policyEvaluationContextRelations = policyEvaluationContext.getRelations(this.getRelation().getArcXri()); policyEvaluationContextRelations.hasNext(); ) {

					Relation policyEvaluationContextRelation = policyEvaluationContextRelations.next();
					XdiInnerRoot policyEvaluationContextInnerRoot = XdiInnerRoot.fromContextNode(policyEvaluationContextRelation.follow());
					if (policyEvaluationContextInnerRoot == null) continue;

					results.add(Boolean.valueOf(policyEvaluationContextInnerRoot.containsRelativeStatement(statementXri)));
				}
			}

			return results.toArray(new Boolean[results.size()]);
		}
	}
}
