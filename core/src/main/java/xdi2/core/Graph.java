package xdi2.core;

import java.io.Serializable;
import java.util.Properties;

import xdi2.core.io.MimeType;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIStatement;
import xdi2.core.util.iterators.ReadOnlyIterator;

/**
 * This interface represents a whole XDI graph.
 * XDI graphs consist of context nodes, relations, and literals.
 * Also, an XDI graph can be expressed as a set of XDI statements.
 * 
 * @author markus
 */
public interface Graph extends Serializable, Comparable<Graph> {

	/*
	 * General methods
	 */

	/**
	 * Gets the graph factory that created this graph.
	 * @return The graph factory.
	 */
	public GraphFactory getGraphFactory();

	/**
	 * Returns an optional identifier to distinguish graphs from one another.
	 * @return The graph identifier.
	 */
	public String getIdentifier();

	/**
	 * Gets the local root context node of this graph.
	 * @param subgraph This is simply a hint to the implementation whether 
	 * child context nodes will subsequently be requested. Implementations may 
	 * or may not actually use this parameter.
	 * @return The graph's local root context node.
	 */
	public ContextNode getRootContextNode(boolean subgraph);

	/**
	 * Gets the local root context node of this graph.
	 * @return The graph's local root context node.
	 */
	public ContextNode getRootContextNode();

	/**
	 * Closes the graph. This should be called when work on the graph is done.
	 */
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
	 * Deep methods for nodes
	 */

	public Node setDeepNode(XDIAddress nodeXDIAddress);

	public Node getDeepNode(XDIAddress nodeXDIAddress, boolean subgraph);

	public Node getDeepNode(XDIAddress nodeXDIAddress);

	/*
	 * Deep methods for context nodes
	 */

	public ContextNode setDeepContextNode(XDIAddress contextNodeXDIAddress);

	public ContextNode getDeepContextNode(XDIAddress contextNodeXDIAddress, boolean subgraph);

	public ContextNode getDeepContextNode(XDIAddress contextNodeXDIAddress);

	/*
	 * Deep methods for literal nodes
	 */

	public LiteralNode setDeepLiteralNode(XDIAddress literalNodeXDIAddress);

	public LiteralNode getDeepLiteralNode(XDIAddress literalNodeXDIAddress, boolean subgraph);

	public LiteralNode getDeepLiteralNode(XDIAddress literalNodeXDIAddress);

	/*
	 * Deep methods for relations
	 */

	public Relation setDeepRelation(XDIAddress contextNodeXDIAddress, XDIAddress XDIaddress, XDIAddress targetXDIAddress);

	public Relation setDeepRelation(XDIAddress contextNodeXDIAddress, XDIAddress XDIaddress, Node targetNode);

	public Relation getDeepRelation(XDIAddress contextNodeXDIAddress, XDIAddress XDIaddress, XDIAddress targetXDIAddress);

	public Relation getDeepRelation(XDIAddress contextNodeXDIAddress, XDIAddress XDIaddress);

	public ReadOnlyIterator<Relation> getDeepRelations(XDIAddress contextNodeXDIAddress, XDIAddress XDIaddress);

	/*
	 * Methods related to statements
	 */

	/**
	 * Sets a statement in this graph.
	 */
	public Statement setStatement(XDIStatement XDIstatement);

	/**
	 * Gets a statement in this graph.
	 */
	public Statement getStatement(XDIStatement XDIstatement);

	/**
	 * Gets all statements in this graph.
	 * @return An iterator over statements.
	 */
	public ReadOnlyIterator<Statement> getAllStatements();

	/**
	 * Check if a statement exists in this graph.
	 */
	public boolean containsStatement(XDIStatement XDIstatement);

	/**
	 * Returns the number of all statements in this graph.
	 * @return The number of statements.
	 */
	public long getAllStatementCount();

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
