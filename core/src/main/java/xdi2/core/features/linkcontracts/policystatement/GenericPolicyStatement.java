package xdi2.core.features.linkcontracts.policystatement;

import xdi2.core.Relation;
import xdi2.core.features.linkcontracts.evaluation.PolicyEvaluationContext;
import xdi2.core.xri3.XDI3Segment;

/**
 * An XDI generic policy statement, represented as a relation.
 * 
 * @author markus
 */
public class GenericPolicyStatement extends PolicyStatement {

	private static final long serialVersionUID = 4296419491079293469L;

	protected GenericPolicyStatement(Relation relation) {

		super(relation);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a relation is a valid XDI generic policy statement.
	 * @param relation The relation to check.
	 * @return True if the relation is a valid XDI generic policy statement.
	 */
	public static boolean isValid(Relation relation) {

		return true;
	}

	/**
	 * Factory method that creates an XDI generic policy statement bound to a given relation.
	 * @param relation The relation that is an XDI generic policy statement.
	 * @return The XDI generic policy statement.
	 */
	public static GenericPolicyStatement fromRelation(Relation relation) {

		if (! isValid(relation)) return null;

		return new GenericPolicyStatement(relation);
	}

	/*
	 * Instance methods
	 */

	@Override
	public boolean evaluateInternal(PolicyEvaluationContext policyEvaluationContext) {

		XDI3Segment arcXri = this.getRelation().getArcXri();
		XDI3Segment targetContextNodeXri = this.getRelation().getTargetContextNodeXri();

		return policyEvaluationContext.getRelation(arcXri, targetContextNodeXri) != null;
	}
}
