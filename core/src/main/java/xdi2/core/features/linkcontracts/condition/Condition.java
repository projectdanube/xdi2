package xdi2.core.features.linkcontracts.condition;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.Statement;
import xdi2.core.util.locator.ContextNodeLocator;
import xdi2.core.xri3.impl.XDI3Segment;

/**
 * An XDI condition, represented as a statement.
 * 
 * @author markus
 */
public abstract class Condition implements Serializable, Comparable<Condition> {

	private static final long serialVersionUID = 78354454331218804L;

	private static final Logger log = LoggerFactory.getLogger(Condition.class);

	private Statement statement;

	protected Condition(Statement statement) {

		if (statement == null) throw new NullPointerException();

		this.statement = statement;
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a statement is a valid XDI condition.
	 * @param statement The statement to check.
	 * @return True if the statement is a valid XDI condition.
	 */
	public static boolean isValid(Statement statement) {

		return
				EqualsCondition.isValid(statement) ||
				GreaterCondition.isValid(statement) ||
				LesserCondition.isValid(statement) ||
				IsCondition.isValid(statement);
	}

	/**
	 * Factory method that creates an XDI condition bound to a given statement.
	 * @param statement The statement that is an XDI condition.
	 * @return The XDI condition.
	 */
	public static Condition fromStatement(Statement statement) {

		if (EqualsCondition.isValid(statement)) return EqualsCondition.fromStatement(statement);
		if (GreaterCondition.isValid(statement)) return GreaterCondition.fromStatement(statement);
		if (LesserCondition.isValid(statement)) return LesserCondition.fromStatement(statement);
		if (IsCondition.isValid(statement)) return IsCondition.fromStatement(statement);

		return null;
	}

	/**
	 * Factory method that casts a Condition to the right subclass, e.g. to a EqualsCondition.
	 * @param condition The Condition to be cast.
	 * @return The casted Condition.
	 */
	public static Condition castCondition(Condition condition) {

		if (condition == null) return null;

		return fromStatement(condition.getStatement());
	}

	/*
	 * Instance methods
	 */

	/**
	 * Returns the underlying statement to which this XDI condition is bound.
	 * @return A statement that represents the XDI condition.
	 */
	public Statement getStatement() {

		return this.statement;
	}

	/**
	 * Returns the condition XRI of the XDI condition (e.g. $equals, $greater, $lesser, $is).
	 * @return The condition XRI of the XDI condition.
	 */
	public XDI3Segment getConditionXri() {

		return this.getStatement().getPredicate();
	}

	/**
	 * Checks if the XDI condition evaluates to true or false.
	 * @param contextNodeLocator An object that can locate context nodes.
	 * @return True or false.
	 */
	public final boolean evaluate(ContextNodeLocator contextNodeLocator) {

		if (log.isDebugEnabled()) log.debug("Evaluating " + this.getClass().getSimpleName() + ": " + this.getStatement());
		boolean result = this.evaluateInternal(contextNodeLocator);
		if (log.isDebugEnabled()) log.debug("Evaluated " + this.getClass().getSimpleName() + ": " + this.getStatement() + ": " + result);

		return result;
	}

	protected abstract boolean evaluateInternal(ContextNodeLocator contextNodeLocator);

	/*
	 * Object methods
	 */

	@Override
	public String toString() {

		return this.getStatement().toString();
	}

	@Override
	public boolean equals(Object object) {

		if (object == null || ! (object instanceof Condition)) return(false);
		if (object == this) return(true);

		Condition other = (Condition) object;

		return this.getStatement().equals(other.getStatement());
	}

	@Override
	public int hashCode() {

		int hashCode = 1;

		hashCode = (hashCode * 31) + this.getStatement().hashCode();

		return hashCode;
	}

	@Override
	public int compareTo(Condition other) {

		if (other == this || other == null) return(0);

		return this.getStatement().compareTo(other.getStatement());
	}
}
