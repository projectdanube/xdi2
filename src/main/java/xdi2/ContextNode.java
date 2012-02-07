package xdi2;

import java.io.Serializable;
import java.util.Iterator;

import xdi2.xri3.impl.XRI3Segment;
import xdi2.xri3.impl.XRI3SubSegment;

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
	 * Gets the XRI of this context node.
	 * @return The XRI of this context node.
	 */
	public XRI3Segment getXri();

	/**
	 * Deletes this context node.
	 */
	public void delete();

	/**
	 * Every context node has an associated arc XRI.
	 * @return The arc XRI associated with this context node.
	 */
	public XRI3SubSegment getArcXri();

	/**
	 * Clears the context node. This is equivalent to calling deleteContextNodes(), deleteRelations() and deleteLiterals().
	 */
	public void clear();

	/**
	 * Checks if the context nodeis empty. 
	 * This is equivalent to calling ! ( containsContextNodes() || containsRelations() || containsLiterals() ).
	 */
	public boolean isEmpty();

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
	public Relation createRelation(XRI3SubSegment arcXri, XRI3Segment relationXri);

	/**
	 * Finds and returns a relation with a given arc XRI. 
	 * @param arcXri The arc XRI to look for. 
	 * @return The relation with the given arc XRI, or null.
	 */
	public Relation getRelation(XRI3SubSegment arcXri);

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
	 * @return True if this context nod has a relation with the given arc XRI.
	 */
	public boolean containsRelation(XRI3SubSegment arcXri);

	/**
	 * Checks if this context node has one or more relations.
	 * @return True if this context node has relations.
	 */
	public boolean containsRelations();

	/**
	 * Deletes the relation with a given arc XRI from this context node.
	 * @param arcXri The arc XRI of the relation.
	 */
	public void deleteRelation(XRI3SubSegment arcXri);

	/**
	 * Deletes all relations from this context node.
	 */
	public void deleteRelations();

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
	 * @param arcXri The arc XRI of the literal.
	 * @param literalData The data of the literal.
	 * @return The newly created literal.
	 */
	public Literal createLiteral(XRI3SubSegment arcXri, String literalData);

	/**
	 * Finds and returns a literal with a given arc XRI. 
	 * @param arcXri The arc XRI to look for. 
	 * @return The literal with the given arc XRI, or null.
	 */
	public Literal getLiteral(XRI3SubSegment arcXri);

	/**
	 * Returns the literals of this context node.
	 * @return An iterator over literals.
	 */
	public Iterator<Literal> getLiterals();

	/**
	 * Returns all literals of this context node.
	 * @return An iterator over literals.
	 */
	public Iterator<Literal> getAllLiterals();

	/**
	 * Checks if a literal with a given arc XRI exists in this context node.
	 * @param arcXri The arc XRI to look for. 
	 * @return True if this context nod has a literal with the given arc XRI.
	 */
	public boolean containsLiteral(XRI3SubSegment arcXri);

	/**
	 * Checks if this context node has one or more literals.
	 * @return True if this context node has literals.
	 */
	public boolean containsLiterals();

	/**
	 * Deletes the literal with a given arc XRI from this context node.
	 * @param arcXri The arc XRI of the literal.
	 */
	public void deleteLiteral(XRI3SubSegment arcXri);

	/**
	 * Deletes all literals from this context node.
	 */
	public void deleteLiterals();

	/**
	 * Returns the number of literals of this context node.
	 * @return The number of literals.
	 */
	public int getLiteralCount();

	/**
	 * Returns the number of all literals of this context node.
	 * @return The number of literals.
	 */
	public int getAllLiteralCount();
}
