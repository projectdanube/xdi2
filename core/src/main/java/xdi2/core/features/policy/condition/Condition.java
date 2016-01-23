package xdi2.core.features.policy.condition;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.features.policy.evaluation.PolicyEvaluationContext;
import xdi2.core.syntax.XDIStatement;

/**
 * An XDI condition, represented as a statement.
 * 
 * @author markus
 */
public abstract class Condition implements Serializable, Comparable<Condition> {

	private static final long serialVersionUID = 78354454331218804L;

	private static final Logger log = LoggerFactory.getLogger(Condition.class);

	private XDIStatement XDIstatement;

	protected Condition(XDIStatement XDIstatement) {

		if (XDIstatement == null) throw new NullPointerException();

		this.XDIstatement = XDIstatement;
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a statement is a valid XDI condition.
	 * @param XDIstatement The statement to check.
	 * @return True if the statement is a valid XDI condition.
	 */
	public static boolean isValid(XDIStatement XDIstatement) {

		return
				EqualsCondition.isValid(XDIstatement) ||
				MatchesCondition.isValid(XDIstatement) ||
				GreaterCondition.isValid(XDIstatement) ||
				LesserCondition.isValid(XDIstatement) ||
				IsCondition.isValid(XDIstatement) ||
				GenericCondition.isValid(XDIstatement);
	}

	/**
	 * Factory method that creates an XDI condition bound to a given statement.
	 * @param XDIstatement The statement that is an XDI condition.
	 * @return The XDI condition.
	 */
	public static Condition fromStatement(XDIStatement XDIstatement) {

		if (EqualsCondition.isValid(XDIstatement)) return EqualsCondition.fromStatement(XDIstatement);
		if (MatchesCondition.isValid(XDIstatement)) return MatchesCondition.fromStatement(XDIstatement);
		if (GreaterCondition.isValid(XDIstatement)) return GreaterCondition.fromStatement(XDIstatement);
		if (LesserCondition.isValid(XDIstatement)) return LesserCondition.fromStatement(XDIstatement);
		if (IsCondition.isValid(XDIstatement)) return IsCondition.fromStatement(XDIstatement);
		if (GenericCondition.isValid(XDIstatement)) return GenericCondition.fromStatement(XDIstatement);

		return null;
	}

	/**
	 * Factory method that casts a Condition to the right subclass, e.g. to a EqualsCondition.
	 * @param condition The Condition to be cast.
	 * @return The casted Condition.
	 */
	public static Condition castCondition(Condition condition) {

		if (condition == null) return null;

		return fromStatement(condition.getXDIStatement());
	}

	/*
	 * Instance methods
	 */

	/**
	 * Returns the underlying statement to which this XDI condition is bound.
	 * @return A statement that represents the XDI condition.
	 */
	public XDIStatement getXDIStatement() {

		return this.XDIstatement;
	}

	/**
	 * Checks if the XDI condition evaluates to true or false.
	 * @param policyEvaluationContext A context for evaluating an XDI policy.
	 * @return True or false.
	 */
	public final boolean evaluate(PolicyEvaluationContext policyEvaluationContext) {

		if (log.isDebugEnabled()) log.debug("Evaluating " + this.getClass().getSimpleName() + ": " + this.getXDIStatement());
		boolean result = this.evaluateInternal(policyEvaluationContext);
		if (log.isDebugEnabled()) log.debug("Evaluated " + this.getClass().getSimpleName() + ": " + this.getXDIStatement() + ": " + result);

		return result;
	}

	protected abstract boolean evaluateInternal(PolicyEvaluationContext policyEvaluationContext);

	/*
	 * Object methods
	 */

	@Override
	public String toString() {

		return this.getXDIStatement().toString();
	}

	@Override
	public boolean equals(Object object) {

		if (object == null || ! (object instanceof Condition)) return(false);
		if (object == this) return(true);

		Condition other = (Condition) object;

		return this.getXDIStatement().equals(other.getXDIStatement());
	}

	@Override
	public int hashCode() {

		int hashCode = 1;

		hashCode = (hashCode * 31) + this.getXDIStatement().hashCode();

		return hashCode;
	}

	@Override
	public int compareTo(Condition other) {

		if (other == this || other == null) return(0);

		return this.getXDIStatement().compareTo(other.getXDIStatement());
	}
}
