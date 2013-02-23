package xdi2.core;

import java.io.Closeable;
import java.io.Serializable;
import java.util.Properties;

import xdi2.core.io.MimeType;
import xdi2.core.util.iterators.ReadOnlyIterator;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;

/**
 * This interface represents a whole XDI graph.
 * XDI graphs consist of context nodes, relations, and literals.
 * Also, XDI graphs can be expressed a set of XDI statements.
 * 
 * @author markus
 */
public interface Graph extends Serializable, Comparable<Graph>, Closeable {

	/*
	 * General methods
	 */

	/**
	 * Gets the graph factory that created this graph.
	 * @return The graph factory.
	 */
	public GraphFactory getGraphFactory();

	/**
	 * Gets the local root context node of this graph.
	 * @return The graph's local root context node.
	 */
	public ContextNode getRootContextNode();

	/**
	 * Closes the graph. This should be called when work on the graph is done.
	 */
	@Override
	public void close();

	/**
	 * Clears all data from the graph.
	 * This is equivalent to calling getRootContextNode().clear();
	 */
	public void clear();

	/**
	 * Checks if the graph is empty.
	 * This is equivalent to calling getRootContextNode().isEmpty();
	 */
	public boolean isEmpty();

	/**
	 * Finds a context node at any depth in this graph.
	 * @param contextNodeXri The XRI of the context node.
	 * @param create Whether or not to create context nodes if they don't exist.
	 * @return A context node or null
	 */
	public ContextNode findContextNode(XDI3Segment contextNodeXri, boolean create);

	/**
	 * Finds a relation at any depth in this graph.
	 * @param contextNodeXri The relation XRI of the context node containing the relation.
	 * @param arcXri The arc XRI of the relation.
	 * @param targetContextNodeXri The target context node XRI of the relation.
	 * @return A relation or null.
	 */
	public Relation findRelation(XDI3Segment contextNodeXri, XDI3Segment arcXri, XDI3Segment targetContextNodeXri);

	/**
	 * Finds a relation at any depth in this graph.
	 * @param contextNodeXri The XRI of the context node containing the relation.
	 * @param arcXri The arc XRI of the relation.
	 * @return A relation or null.
	 */
	public Relation findRelation(XDI3Segment contextNodeXri, XDI3Segment arcXri);

	/**
	 * Finds relations at any depth in this graph.
	 * @param contextNodeXri The XRI of the context node containing the relations.
	 * @param arcXri The arc XRI of the relations.
	 * @return An iterator over relations.
	 */
	public ReadOnlyIterator<Relation> findRelations(XDI3Segment contextNodeXri, XDI3Segment arcXri);

	/**
	 * Finds a literal at any depth in this graph.
	 * @param contextNodeXri The XRI of the context node containing the literal.
	 * @param literalData The data of the literal.
	 * @return A literal or null.
	 */
	public Literal findLiteral(XDI3Segment contextNodeXri, String literalData);

	/**
	 * Finds a literal at any depth in this graph.
	 * @param contextNodeXri The XRI of the context node containing the literal.
	 * @return A literal or null.
	 */
	public Literal findLiteral(XDI3Segment contextNodeXri);

	/**
	 * Checks if a context node exists in this graph.
	 * @param contextNodeXri The XRI of the context node.
	 * @return True, if the context node exists.
	 */
	public boolean containsContextNode(XDI3Segment contextNodeXri);

	/**
	 * Checks if relations exists in this graph.
	 * @param contextNodeXri The XRI of the context node containing the relation.
	 * @param arcXri The arc XRI of the relation.
	 * @param targetContextNodeXri The target context node XRI of the relation.
	 * @return True, if the relation exists.
	 */
	public boolean containsRelation(XDI3Segment contextNodeXri, XDI3Segment arcXri, XDI3Segment targetContextNodeXri);

	/**
	 * Checks if relations exists in this graph.
	 * @param contextNodeXri The XRI of the context node containing the relation.
	 * @param arcXri The arc XRI of the relation.
	 * @return True, if the relation exists.
	 */
	public boolean containsRelations(XDI3Segment contextNodeXri, XDI3Segment arcXri);

	/**
	 * Checks if a literal exists in this graph.
	 * @param contextNodeXri The XRI of the context node containing the literal.
	 * @param literalData The data of the literal.
	 * @return True, if the literal exists.
	 */
	public boolean containsLiteral(XDI3Segment contextNodeXri, String literalData);

	/**
	 * Checks if a literal exists in this graph.
	 * @param contextNodeXri The XRI of the context node containing the literal.
	 * @return True, if the literal exists.
	 */
	public boolean containsLiteral(XDI3Segment contextNodeXri);

	/**
	 * Converts the graph to a string in the given serialization format.
	 * @param format The serialization format.
	 * @param parameters Parameters for the serialization.
	 */
	public String toString(String format, Properties parameters);

	/**
	 * Converts the graph to a string in the given MIME type.
	 * @param mimeType The MIME type.
	 */
	public String toString(MimeType mimeType);

	/*
	 * Methods related to statements
	 */

	/**
	 * A simple way to create a statement in this graph.
	 */
	public Statement createStatement(XDI3Statement statementXri);

	/**
	 * A simple way to find a statement in this graph.
	 */
	public Statement findStatement(XDI3Statement statementXri);

	/**
	 * A simple way to check if a statement exists in this graph.
	 */
	public boolean containsStatement(XDI3Statement statementXri);

	/*
	 * Methods related to transactions
	 */

	/**
	 * Check if this graph supports transactions.
	 */
	public boolean supportsTransactions();

	/**
	 * Starts a new transaction.
	 */
	public void beginTransaction();

	/**
	 * Commits the changes made by the transaction.
	 */
	public void commitTransaction();

	/**
	 * Rolls back the changes made by the transaction.
	 */
	public void rollbackTransaction();
}
