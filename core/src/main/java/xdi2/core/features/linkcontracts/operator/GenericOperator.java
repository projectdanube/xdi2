package xdi2.core.features.linkcontracts.operator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import xdi2.core.Relation;
import xdi2.core.features.linkcontracts.evaluation.PolicyEvaluationContext;
import xdi2.core.features.roots.InnerRoot;
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

	/*
	 * Instance methods
	 */

	@Override
	public Boolean[] evaluateInternal(PolicyEvaluationContext policyEvaluationContext) {

		InnerRoot innerRoot = InnerRoot.fromContextNode(this.getRelation().follow());

		if (innerRoot == null) {

			for (Iterator<Relation> policyEvaluationContextRelations = policyEvaluationContext.getRelations(this.getRelation().getArcXri()); policyEvaluationContextRelations.hasNext(); ) {

				Relation policyEvaluationContextRelation = policyEvaluationContextRelations.next();
				InnerRoot policyEvaluationContextInnerRoot = InnerRoot.fromContextNode(policyEvaluationContextRelation.follow());
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
					InnerRoot policyEvaluationContextInnerRoot = InnerRoot.fromContextNode(policyEvaluationContextRelation.follow());
					if (policyEvaluationContextInnerRoot == null) continue;

					results.add(Boolean.valueOf(policyEvaluationContextInnerRoot.containsRelativeStatement(statementXri)));
				}
			}

			return results.toArray(new Boolean[results.size()]);
		}
	}
}
