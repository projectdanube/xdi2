package xdi2.core;

import java.io.Closeable;
import java.io.Serializable;
import java.util.Properties;

import xdi2.core.io.MimeType;
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
	 * Creates new context nodes and adds them to this graph.
	 * @param contextNodeArcXris The path of arc XRIs of the context node.
	 * @return The newly created final context node.
	 */
	public ContextNode createContextNode(XDI3Segment contextNodeArcXris);

	/**
	 * Creates new context nodes and adds them to this graph, or returns an existing context node.
	 * @param contextNodeArcXris The path of arc XRIs of the context node.
	 * @return The newly created or existing final context node.
	 */
	public ContextNode setContextNode(XDI3Segment contextNodeArcXris);

	/**
	 * Returns a context node from this graph.
	 * @param contextNodeArcXris The path of arc XRIs of the context node.
	 * @return The context node with the given path of arc XRIs, or null.
	 */
	public ContextNode getContextNode(XDI3Segment contextNodeArcXris);

	/**
	 * Checks if a context node exists in this graph.
	 * @param contextNodeArcXris The path of arc XRIs of the context node.
	 * @return True if this graph has a context node with the given path of arc XRIs.
	 */
	public boolean containsContextNode(XDI3Segment contextNodeArcXris);

	/**
	 * Deletes a context node from this graph.
	 * @param contextNodeArcXris The path of arc XRIs of the context node.
	 */
	public void deleteContextNode(XDI3Segment contextNodeArcXris);

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
	 * A simple way to set a statement in this graph.
	 */
	public Statement setStatement(XDI3Statement statementXri);

	/**
	 * A simple way to get a statement in this graph.
	 */
	public Statement getStatement(XDI3Statement statementXri);

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
