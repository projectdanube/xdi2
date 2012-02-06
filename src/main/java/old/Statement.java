package old;

import java.io.Serializable;
import java.util.Properties;

/**
 * This interface represents a statement in an XDI graph.
 * A statement consists of either:
 * <ul>
 * <li>A subject (if the subject has no predicates).</li>
 * <li>A subject and a predicate (if the predicate has no references or literals).</li>
 * <li>A subject and a predicate and a reference.</li>
 * <li>A subject and a predicate and a literal.</li>
 * <li>A subject and a predicate and an inner graph.</li>
 * </ul>
 * 
 * A statement never has BOTH an reference and a literal.
 * 
 * @author markus
 */
public interface Statement extends Serializable, Comparable<Statement> {

	/*
	 * General methods
	 */

	/**
	 * Deletes the statement.
	 */
	public void delete();

	/**
	 * Returns the graph that contains this statements.
	 * @return The graph that contains this statements.
	 */
	public Graph getContainingGraph();

	/**
	 * Gets the subject of this statement.
	 * @return A subject.
	 */
	public Subject getSubject();

	/**
	 * Gets the predicate of this statement.
	 * @return A predicate.
	 */
	public Predicate getPredicate();

	/**
	 * Gets the reference of this statement.
	 * @return A reference.
	 */
	public Reference getReference();

	/**
	 * Gets the literal of this statement.
	 * @return A literal.
	 */
	public Literal getLiteral();

	/**
	 * Gets the inner graph of this statement.
	 * @return An inner graph.
	 */
	public Graph getInnerGraph();

	/**
	 * Checks if the statement has a predicate.
	 * @return True if this statement contains a predicate.
	 */
	public boolean containsPredicate();

	/**
	 * Checks if the statement has a reference.
	 * @return True if this statement contains a reference.
	 */
	public boolean containsReference();

	/**
	 * Checks if the statement has a literal.
	 * @return True if this statement contains a literal.
	 */
	public boolean containsLiteral();

	/**
	 * Checks if the statement has an inner graph.
	 * @return True if this statement contains an inner graph.
	 */
	public boolean containsInnerGraph();

	/**
	 * Checks if two statements contain the same kinds of graph components.
	 * This method returns true, if
	 * - Both statements consist only of a subject
	 * - Both statements consist of a subject and predicate
	 * - Both statements consist of a subject, predicate and reference
	 * - Both statements consist of a subject, predicate and literal
	 * - Both statements consist of a subject, predicate and inner graph
	 * @param other Another statement.
	 * @return True for one of the above conditions.
	 */
	public boolean containsSameGraphComponents(Statement other);

	/**
	 * Checks if the graph components in this statement also exist in
	 * the other statement. If the other statement contains additional
	 * graph components, that's fine too.
	 * @param other Another statement.
	 * @return True if the other statement 'starts with' this statement
	 */
	public boolean startsWith(Statement other);

	/**
	 * Returns the number of graph components this statement consists of.
	 * This is 1,
	 * - if the statement consists only of a subject
	 * This is 2,
	 * - if the statement consists of a subject and predicate
	 * This is 3,
	 * - if the statement consists of a subject, predicate and reference
	 * - if the statement consists of a subject, predicate and literal
	 * - if the statement consists of a subject, predicate and inner graph
	 * @return The number of graph components in this statement.
	 */
	public int getSize();
	
	/**
	 * Returns the deepest graph component of this statement.
	 * @return The left graph component of this statement.
	 */
	public GraphComponent getLeafGraphComponent();
	
	/**
	 * Converts the statement to a string in the given serialization format.
	 * @param format The serialization format.
	 * @param parameters Parameters for the serialization.
	 */
	public String toString(String format, Properties parameters);
}
