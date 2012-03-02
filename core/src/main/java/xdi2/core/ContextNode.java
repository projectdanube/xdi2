package xdi2.core;

import java.io.Serializable;
import java.util.Iterator;

import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.core.xri3.impl.XRI3SubSegment;

/**
 * This interface represents a whole XDI graph.
 * XDI graphs consist of subjects and statements between them.
 * Operations on the graph include creating new statements and subjects, finding and
 * manipulating them.
 * 
 * @author markus
 */
public interface ContextNode extends Serializable, Comparable<ContextNode> {

	/*
	 * General methods
	 */

	/**
	 * Get the graph of this context node.
	 * @return The graph of this context node.
	 */
	public Graph getGraph();

	/**
	 * Every context node has a context node from which it originates.
	 * @return The context node of this context node.
	 */
	public ContextNode getContextNode();

	/**
	 * Checks if this context node is the root context node.
	 * @return True, if this context node is the root context node.
	 */
	public boolean isRootContextNode();

	/**
	 * Deletes this context node.
	 */
	public void delete();

	/**
	 * Clears the context node. This is equivalent to calling deleteContextNodes(), deleteRelations() and deleteLiterals().
	 */
	public void clear();

	/**
	 * Checks if the context nodeis empty. 
	 * This is equivalent to calling ! ( containsContextNodes() || containsRelations() || containsLiterals() ).
	 */
	public boolean isEmpty();

	/**
	 * Every context node has an associated arc XRI.
	 * This returns null for the root context node.
	 * @return The arc XRI associated with this context node.
	 */
	public XRI3SubSegment getArcXri();

	/**
	 * Gets the XRI of this context node.
	 * This returns () for the root context node.
	 * @return The XRI of this context node.
	 */
	public XRI3Segment getXri();

	/*
	 * Methods related to context nodes of this context node
	 */

	/**
	 * Creates a new context node and adds it to this context node.
	 * @param arcXri The arc XRI of the new context node.
	 * @return The newly created context node.
	 */
	public ContextNode createContextNode(XRI3SubSegment arcXri);

	/**
	 * Creates new context nodes and adds them to this context node.
	 * @param arcXri The arc XRI of the new context nodes.
	 * @return The newly created final context node.
	 */
	public ContextNode createContextNodes(XRI3Segment arcXri);

	/**
	 * Finds and returns a context node with a given arc XRI. 
	 * @param arcXri The arc XRI to look for. 
	 * @return The context node with the given arc XRI, or null.
	 */
	public ContextNode getContextNode(XRI3SubSegment arcXri);

	/**
	 * Returns the context nodes of this context node.
	 * @return An iterator over context nodes.
	 */
	public Iterator<ContextNode> getContextNodes();

	/**
	 * Returns all context nodes of this context node.
	 * @return An iterator over context nodes.
	 */
	public Iterator<ContextNode> getAllContextNodes();

	/**
	 * Returns all leaf context nodes of this context node.
	 * @return An iterator over leaf context nodes.
	 */
	public Iterator<ContextNode> getAllLeafContextNodes();

	/**
	 * Checks if a context node with a given arc XRI exists in this context node.
	 * @param arcXri The arc XRI to look for. 
	 * @return True if this context node has a context node with the given arc XRI.
	 */
	public boolean containsContextNode(XRI3SubSegment arcXri);

	/**
	 * Checks if this context node has one or more context nodes.
	 * @return True if this context node has context nodes.
	 */
	public boolean containsContextNodes();

	/**
	 * Deletes the context node with a given arc XRI from this context node.
	 * @param arcXri The arc XRI of this context arc.
	 */
	public void deleteContextNode(XRI3SubSegment arcXri);

	/**
	 * Deletes all context nodes from this context node.
	 */
	public void deleteContextNodes();

	/**
	 * Returns the number of context nodes of this context node.
	 * @return The number of context nodes.
	 */
	public int getContextNodeCount();

	/**
	 * Returns the number of all context nodes of this context node.
	 * @return The number of context nodes.
	 */
	public int getAllContextNodeCount();

	/*
	 * Methods related to relations of this context node
	 */

	/**
	 * Creates a new relation and adds it to this context node.
	 * @param arcXri The arc XRI of the relation.
	 * @param relationXri The relation XRI of the relation.
	 * @return The newly created relation.
	 */
	public Relation createRelation(XRI3Segment arcXri, XRI3Segment relationXri);

	/**
	 * Creates a new relation and adds it to this context node.
	 * @param arcXri The arc XRI of the relation.
	 * @param contextNode The context node it points to.
	 * @return The newly created relation.
	 */
	public Relation createRelation(XRI3Segment arcXri, ContextNode contextNode);

	/**
	 * Finds and returns a relation with a given arc XRI. 
	 * @param arcXri The arc XRI to look for. 
	 * @param relationXri The relation XRI of the relation.
	 * @return The relation with the given arc XRI, or null.
	 */
	public Relation getRelation(XRI3Segment arcXri, XRI3Segment relationXri);

	/**
	 * Finds and returns a relation with a given arc XRI. 
	 * @param arcXri The arc XRI to look for. 
	 * @return The relation with the given arc XRI, or null.
	 */
	public Relation getRelation(XRI3Segment arcXri);

	/**
	 * Finds and returns the relations with a given arc XRI. 
	 * @param arcXri The arc XRI to look for. 
	 * @return The relations with the given arc XRI, or null.
	 */
	public Iterator<Relation> getRelations(XRI3Segment arcXri);

	/**
	 * Returns the relations of this context node.
	 * @return An iterator over relations.
	 */
	public Iterator<Relation> getRelations();

	/**
	 * Returns all relations of this context node.
	 * @return An iterator over relations.
	 */
	public Iterator<Relation> getAllRelations();

	/**
	 * Checks if a relation with a given arc XRI exists in this context node.
	 * @param arcXri The arc XRI to look for. 
	 * @param relationXri The relation XRI of the relation.
	 * @return True if this context nod has a relation with the given arc XRI.
	 */
	public boolean containsRelation(XRI3Segment arcXri, XRI3Segment relationXri);

	/**
	 * Checks if relations with a given arc XRI exists in this context node.
	 * @param arcXri The arc XRI to look for. 
	 * @return True if this context nod has a relation with the given arc XRI.
	 */
	public boolean containsRelations(XRI3Segment arcXri);

	/**
	 * Checks if this context node has one or more relations.
	 * @return True if this context node has relations.
	 */
	public boolean containsRelations();

	/**
	 * Deletes the relation with a given arc XRI from this context node.
	 * @param arcXri The arc XRI of the relation.
	 * @param relationXri The relation XRI of the relation.
	 */
	public void deleteRelation(XRI3Segment arcXri, XRI3Segment relationXri);

	/**
	 * Deletes the relation with a given arc XRI from this context node.
	 * @param arcXri The arc XRI of the relation.
	 */
	public void deleteRelations(XRI3Segment arcXri);

	/**
	 * Deletes all relations from this context node.
	 */
	public void deleteRelations();

	/**
	 * Returns the number of relations of this context node.
	 * @param arcXri The arc XRI to look for. 
	 * @return The number of relations.
	 */
	public int getRelationCount(XRI3Segment arcXri);

	/**
	 * Returns the number of relations of this context node.
	 * @return The number of relations.
	 */
	public int getRelationCount();

	/**
	 * Returns the number of all relations of this context node.
	 * @return The number of relations.
	 */
	public int getAllRelationCount();

	/*
	 * Methods related to literals of this context node
	 */

	/**
	 * Creates a new literal and adds it to this context node.
	 * @param literalData The data of the literal.
	 * @return The newly created literal.
	 */
	public Literal createLiteral(String literalData);

	/**
	 * Creates a new literal of a context node and adds it to this context node.
	 * @param arcXri The arc XRI of the context node.
	 * @param literalData The data of the literal.
	 * @return The newly created literal.
	 */
	public Literal createLiteralInContextNode(XRI3SubSegment arcXri, String literalData);

	/**
	 * Returns the literal of this context node.
	 * @return The literal.
	 */
	public Literal getLiteral();

	/**
	 * Returns the literal of a context node in this context node.
	 * @param arcXri The arc XRI of the context node.
	 * @return The literal.
	 */
	public Literal getLiteralInContextNode(XRI3SubSegment arcXri);

	/**
	 * Returns all literals of this context node.
	 * @return An iterator over literals.
	 */
	public Iterator<Literal> getAllLiterals();

	/**
	 * Checks if this context node has a literal.
	 * @return True if this context node has literals.
	 */
	public boolean containsLiteral();

	/**
	 * Checks if this context node has a literal.
	 * @param arcXri The arc XRI of the context node.
	 * @return True if this context node has literals.
	 */
	public boolean containsLiteralInContextNode(XRI3SubSegment arcXri);

	/**
	 * Deletes the literal from this context node.
	 */
	public void deleteLiteral();

	/**
	 * Returns the number of all literals of this context node.
	 * @return The number of literals.
	 */
	public int getAllLiteralCount();

	/*
	 * Methods related to statements
	 */

	/**
	 * Gets the statement that contains this context node.
	 * @return A statement.
	 */
	public Statement getStatement();

	/**
	 * Gets all statements rooted in this context node.
	 * @return An iterator over statements.
	 */
	public Iterator<Statement> getAllStatements();

	/**
	 * Returns the number of all statements of this context node.
	 * @return The number of statements.
	 */
	public int getAllStatementCount();
}
