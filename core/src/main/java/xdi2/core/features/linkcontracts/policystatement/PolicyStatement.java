package xdi2.core.features.linkcontracts.policystatement;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.Relation;
import xdi2.core.features.linkcontracts.evaluation.PolicyEvaluationContext;

/**
 * An XDI policy statement, represented as a relation.
 * 
 * @author markus
 */
public abstract class PolicyStatement implements Serializable, Comparable<PolicyStatement> {

	private static final long serialVersionUID = 3402735169426576942L;

	private static final Logger log = LoggerFactory.getLogger(PolicyStatement.class);

	protected Relation relation;

	protected PolicyStatement(Relation relation) {

		if (relation == null) throw new NullPointerException();

		this.relation = relation;
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a relation is a valid XDI policy statement.
	 * @param relation The relation to check.
	 * @return True if the relation is a valid XDI policy statement.
	 */
	public static boolean isValid(Relation relation) {

		return
				TruePolicyStatement.isValid(relation) ||
				FalsePolicyStatement.isValid(relation) ||
				GenericPolicyStatement.isValid(relation);
	}

	/**
	 * Factory method that creates an XDI policy statement bound to a given relation.
	 * @param relation The relation that is an XDI policy statement.
	 * @return The XDI policy statement.
	 */
	public static PolicyStatement fromRelation(Relation relation) {

		if (TruePolicyStatement.isValid(relation)) return TruePolicyStatement.fromRelation(relation);
		if (FalsePolicyStatement.isValid(relation)) return FalsePolicyStatement.fromRelation(relation);
		if (GenericPolicyStatement.isValid(relation)) return GenericPolicyStatement.fromRelation(relation);

		return null;
	}

	/**
	 * Factory method that casts a Policy to the right subclass, e.g. to a TruePolicyStatement.
	 * @param policy The Policy to be cast.
	 * @return The casted Policy.
	 */
	public static PolicyStatement castPolicy(PolicyStatement policy) {

		if (policy == null) return null;

		return fromRelation(policy.getRelation());
	}

	/*
	 * Instance methods
	 */

	/**
	 * Returns the underlying relation to which this XDI policy statement is bound.
	 * @return A relation that represents the XDI policy statement.
	 */
	public Relation getRelation() {

		return this.relation;
	}

	/**
	 * Checks if the XDI policy statement evaluates to true or false.
	 * @param policyEvaluationContext An object that can locate context nodes.
	 * @return True or false.
	 */
	public final boolean evaluate(PolicyEvaluationContext policyEvaluationContext) {

		if (log.isDebugEnabled()) log.debug("Evaluating " + this.getClass().getSimpleName() + ": " + this.getRelation());
		boolean result = this.evaluateInternal(policyEvaluationContext);
		if (log.isDebugEnabled()) log.debug("Evaluated " + this.getClass().getSimpleName() + ": " + this.getRelation() + ": " + result);

		return result;
	}

	protected abstract boolean evaluateInternal(PolicyEvaluationContext policyEvaluationContext);

	/*
	 * Object methods
	 */

	@Override
	public String toString() {

		return this.getRelation().toString();
	}

	@Override
	public boolean equals(Object object) {

		if (object == null || ! (object instanceof PolicyStatement)) return(false);
		if (object == this) return(true);

		PolicyStatement other = (PolicyStatement) object;

		return this.getRelation().equals(other.getRelation());
	}

	@Override
	public int hashCode() {

		int hashCode = 1;

		hashCode = (hashCode * 31) + this.getRelation().hashCode();

		return hashCode;
	}

	@Override
	public int compareTo(PolicyStatement other) {

		if (other == this || other == null) return(0);

		return this.getRelation().compareTo(other.getRelation());
	}
}
