package xdi2.core.features.linkcontracts.operator;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.Relation;
import xdi2.core.features.linkcontracts.evaluation.PolicyEvaluationContext;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;

/**
 * An XDI operator, represented as a relation.
 * 
 * @author markus
 */
public abstract class Operator implements Serializable, Comparable<Operator> {

	private static final long serialVersionUID = 3402735169426576942L;

	private static final Logger log = LoggerFactory.getLogger(Operator.class);

	protected Relation relation;

	protected Operator(Relation relation) {

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
				TrueOperator.isValid(relation) ||
				FalseOperator.isValid(relation) ||
				GenericOperator.isValid(relation);
	}

	/**
	 * Factory method that creates an XDI operator bound to a given relation.
	 * @param relation The relation that is an XDI operator.
	 * @return The XDI operator.
	 */
	public static Operator fromRelation(Relation relation) {

		if (TrueOperator.isValid(relation)) return TrueOperator.fromRelation(relation);
		if (FalseOperator.isValid(relation)) return FalseOperator.fromRelation(relation);
		if (GenericOperator.isValid(relation)) return GenericOperator.fromRelation(relation);

		return null;
	}

	/**
	 * Factory method that casts an Operator to the right subclass, e.g. to a TruePolicyStatement.
	 * @param operator The Operator to be cast.
	 * @return The casted Operator.
	 */
	public static Operator castOperator(Operator operator) {

		if (operator == null) return null;

		return fromRelation(operator.getRelation());
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
	public final Boolean[] evaluate(PolicyEvaluationContext policyEvaluationContext) {

		if (log.isDebugEnabled()) log.debug("Evaluating " + this.getClass().getSimpleName() + ": " + this.getRelation());
		Boolean[] result = this.evaluateInternal(policyEvaluationContext);
		if (log.isDebugEnabled()) log.debug("Evaluated " + this.getClass().getSimpleName() + ": " + this.getRelation() + ": " + Arrays.asList(result));

		return result;
	}

	protected abstract Boolean[] evaluateInternal(PolicyEvaluationContext policyEvaluationContext);

	/*
	 * Object methods
	 */

	@Override
	public String toString() {

		return this.getRelation().toString();
	}

	@Override
	public boolean equals(Object object) {

		if (object == null || ! (object instanceof Operator)) return(false);
		if (object == this) return(true);

		Operator other = (Operator) object;

		return this.getRelation().equals(other.getRelation());
	}

	@Override
	public int hashCode() {

		int hashCode = 1;

		hashCode = (hashCode * 31) + this.getRelation().hashCode();

		return hashCode;
	}

	@Override
	public int compareTo(Operator other) {

		if (other == this || other == null) return(0);

		return this.getRelation().compareTo(other.getRelation());
	}

	/*
	 * Helper classes
	 */

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
