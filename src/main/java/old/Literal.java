package old;

import java.io.Serializable;

/**
 * This interface represents a literal in an XDI graph.
 * 
 * @author markus
 */
public interface Literal extends GraphComponent, Serializable, Comparable<Literal> {

	/*
	 * General methods
	 */

	/**
	 * Gets the predicate that has this literal as its target.
	 * @return A predicate.
	 */
	public Predicate getPredicate();

	/**
	 * Deletes this literal from the graph.
	 */
	public void deleteFromPredicate();

	/**
	 * Returns the data of this literal.
	 * @return The data of this literal.
	 */
	public String getData();

	/**
	 * Set the data of this literal.
	 * @param data The new data for this literal.
	 */
	public void setData(String data);

	/*
	 * Methods related to statements
	 */

	/**
	 * Gets the statement that contains this literal.
	 * @return A statement.
	 */
	public Statement getStatement();
}
