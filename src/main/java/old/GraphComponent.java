package old;

import java.util.Iterator;

/**
 * A graph component in an XDI graph is one of those:
 * - A graph
 * - A subject
 * - A predicate
 * - A reference
 * - A literal
 */
public interface GraphComponent {

	/**
	 * Returns the graph that contains this graph component.
	 * This may be null if the graph component is itself a graph and not an inner graph.
	 * @return The graph that contains this graph component.
	 */
	public Graph getContainingGraph();

	/**
	 * Returns the outermost graph this graph component belongs to.
	 * This never returns null.
	 * @return The top-level graph that contains this graph component.
	 */
	public Graph getTopLevelGraph();

	/**
	 * Get the parent graph component.
	 * - If the graph component is a reference, literal or inner graph, return the predicate
	 * - If the graph component is a predicate, return the subject
	 * - If the graph component is a subject, return the containing graph
	 * - If the graph component is a top level graph, return null
	 */
	public GraphComponent getParentGraphComponent();

	/**
	 * True,
	 * - If the graph component is a reference or literal
	 * - If the graph component is an empty predicate
	 * - If the graph component is an empty subject
	 * - If the graph component is an empty graph
	 */
	public boolean isLeaf();

	/**
	 * Get the pre-comment of the graph component.
	 */
	public String getPreComment();

	/**
	 * Set the pre-comment of the graph component.
	 */
	public void setPreComment(String preComment);

	/**
	 * Checks if the graph component contains a pre-comment.
	 */
	public boolean containsPreComment();

	/**
	 * Get the post-comment of the graph component.
	 */
	public String getPostComment();

	/**
	 * Set the post-comment of the graph component.
	 */
	public void setPostComment(String postComment);

	/**
	 * Checks if the graph component contains a post-comment.
	 */
	public boolean containsPostComment();

	/**
	 * Lists all statements rooted in this graph component.
	 * @return An iterator over statements.
	 */
	public Iterator<Statement> getStatements();

	/**
	 * Returns the total number of statements in the graph component. 
	 * @return The number of statements.
	 */
	public int getStatementCount();
}
