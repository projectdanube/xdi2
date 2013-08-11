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
 * Also, an XDI graph can be expressed as a set of XDI statements.
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
	 * Deep methods
	 */

	/**
	 * Deep version of ContextNode.createContextNode(XDI3SubSegment), operates at a context node further down in the graph.
	 */
	public ContextNode createDeepContextNode(XDI3Segment contextNodeArcXris);

	/**
	 * Deep version of ContextNode.setContextNode(XDI3SubSegment), operates at a context node further down in the graph.
	 */
	public ContextNode setDeepContextNode(XDI3Segment contextNodeArcXris);

	/**
	 * Deep version of ContextNode.getContextNode(XDI3SubSegment), operates at a context node further down in the graph.
	 */
	public ContextNode getDeepContextNode(XDI3Segment contextNodeArcXris);

	/**
	 * Deep version of ContextNode.getContextNodes(), operates at a context node further down in the graph.
	 */
	public ReadOnlyIterator<ContextNode> getDeepContextNodes(XDI3Segment contextNodeArcXris);

	/**
	 * Deep version of ContextNode.createRelation(XDI3Segment, XDI3Segment), operates at a context node further down in the graph.
	 */
	public Relation createDeepRelation(XDI3Segment contextNodeArcXris, XDI3Segment arcXri, XDI3Segment targetContextNodeXri);

	/**
	 * Deep version of ContextNode.createRelation(XDI3Segment, ContextNode), operates at a context node further down in the graph.
	 */
	public Relation createDeepRelation(XDI3Segment contextNodeArcXris, XDI3Segment arcXri, ContextNode targetContextNode);

	/**
	 * Deep version of ContextNode.setRelation(XDI3Segment, XDI3Segment), operates at a context node further down in the graph.
	 */
	public Relation setDeepRelation(XDI3Segment contextNodeArcXris, XDI3Segment arcXri, XDI3Segment targetContextNodeXri);

	/**
	 * Deep version of ContextNode.setRelation(XDI3Segment, ContextNode), operates at a context node further down in the graph.
	 */
	public Relation setDeepRelation(XDI3Segment contextNodeArcXris, XDI3Segment arcXri, ContextNode targetContextNode);

	/**
	 * Deep version of ContextNode.getRelation(XDI3Segment, XDI3Segment), operates at a context node further down in the graph.
	 */
	public Relation getDeepRelation(XDI3Segment contextNodeArcXris, XDI3Segment arcXri, XDI3Segment targetContextNodeXri);

	/**
	 * Deep version of ContextNode.getRelation(XDI3Segment), operates at a context node further down in the graph.
	 */
	public Relation getDeepRelation(XDI3Segment contextNodeArcXris, XDI3Segment arcXri);

	/**
	 * Deep version of ContextNode.getRelations(XDI3Segment), operates at a context node further down in the graph.
	 */
	public ReadOnlyIterator<Relation> getDeepRelations(XDI3Segment contextNodeArcXris, XDI3Segment arcXri);

	/**
	 * Deep version of ContextNode.getRelations(), operates at a context node further down in the graph.
	 */
	public ReadOnlyIterator<Relation> getDeepRelations(XDI3Segment contextNodeArcXris);

	/**
	 * Deep version of ContextNode.createLiteral(Object), operates at a context node further down in the graph.
	 */
	public Literal createDeepLiteral(XDI3Segment contextNodeArcXris, Object literalData);

	/**
	 * Deep version of ContextNode.createLiteralString(String), operates at a context node further down in the graph.
	 */
	public Literal createDeepLiteralString(XDI3Segment contextNodeArcXris, String literalData);

	/**
	 * Deep version of ContextNode.createLiteralNumber(Number), operates at a context node further down in the graph.
	 */
	public Literal createDeepLiteralNumber(XDI3Segment contextNodeArcXris, Number literalData);

	/**
	 * Deep version of ContextNode.createLiteralBoolean(Boolean), operates at a context node further down in the graph.
	 */
	public Literal createDeepLiteralBoolean(XDI3Segment contextNodeArcXris, Boolean literalData);

	/**
	 * Deep version of ContextNode.setLiteral(Object), operates at a context node further down in the graph.
	 */
	public Literal setDeepLiteral(XDI3Segment contextNodeArcXris, Object literalData);

	/**
	 * Deep version of ContextNode.setLiteralString(String), operates at a context node further down in the graph.
	 */
	public Literal setDeepLiteralString(XDI3Segment contextNodeArcXris, String literalData);

	/**
	 * Deep version of ContextNode.setLiteralNumber(Number), operates at a context node further down in the graph.
	 */
	public Literal setDeepLiteralNumber(XDI3Segment contextNodeArcXris, Number literalData);

	/**
	 * Deep version of ContextNode.setLiteralBoolean(Boolean), operates at a context node further down in the graph.
	 */
	public Literal setDeepLiteralBoolean(XDI3Segment contextNodeArcXris, Boolean literalData);

	/**
	 * Deep version of ContextNode.getLiteral(Object), operates at a context node further down in the graph.
	 */
	public Literal getDeepLiteral(XDI3Segment contextNodeArcXris, Object literalData);

	/**
	 * Deep version of ContextNode.getLiteralString(String), operates at a context node further down in the graph.
	 */
	public Literal getDeepLiteralString(XDI3Segment contextNodeArcXris, String literalData);

	/**
	 * Deep version of ContextNode.getLiteralNumber(Number), operates at a context node further down in the graph.
	 */
	public Literal getDeepLiteralNumber(XDI3Segment contextNodeArcXris, Number literalData);

	/**
	 * Deep version of ContextNode.getLiteralBoolean(Boolean), operates at a context node further down in the graph.
	 */
	public Literal getDeepLiteralBoolean(XDI3Segment contextNodeArcXris, Boolean literalData);

	/**
	 * Deep version of ContextNode.getLiteral(), operates at a context node further down in the graph.
	 */
	public Literal getDeepLiteral(XDI3Segment contextNodeArcXris);

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
