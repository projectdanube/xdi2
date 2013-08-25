package xdi2.core;

import java.io.Serializable;

import xdi2.core.Statement.ContextNodeStatement;
import xdi2.core.util.iterators.ReadOnlyIterator;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3SubSegment;

/**
 * This interface represents a context node in an XDI graph.
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
	 * Checks if this context node is a leaf context node.
	 * @return True, if this context node is a leaf context node.
	 */
	public boolean isLeafContextNode();

	/**
	 * Deletes this context node.
	 */
	public void delete();

	/**
	 * Clears the context node. This is equivalent to calling deleteContextNodes(), deleteRelations() and deleteLiterals().
	 */
	public void clear();

	/**
	 * Checks if the context node is empty. 
	 * This is equivalent to calling ! ( containsContextNodes() || containsRelations() || containsLiterals() ).
	 */
	public boolean isEmpty();

	/**
	 * Every context node has an associated arc XRI.
	 * This returns null for the root context node.
	 * @return The arc XRI associated with this context node.
	 */
	public XDI3SubSegment getArcXri();

	/**
	 * Gets the XRI of this context node.
	 * This returns () for the root context node.
	 * @return The XRI of this context node.
	 */
	public XDI3Segment getXri();

	/*
	 * Methods related to context nodes of this context node
	 */

	/**
	 * Creates a new context node and adds it to this context node.
	 * @param arcXri The arc XRI of the new context node.
	 * @return The newly created context node.
	 */
	public ContextNode createContextNode(XDI3SubSegment arcXri);

	/**
	 * Deep version of ContextNode.createContextNode(XDI3SubSegment), operates at a context node further down in the graph.
	 */
	public ContextNode createDeepContextNode(XDI3Segment contextNodeXri);

	/**
	 * Creates a new context node and adds it to this context node, or returns an existing context node.
	 * @param arcXri The arc XRI of the new or existing context node.
	 * @return The newly created or existing context node.
	 */
	public ContextNode setContextNode(XDI3SubSegment arcXri);

	/**
	 * Deep version of ContextNode.setContextNode(XDI3SubSegment), operates at a context node further down in the graph.
	 */
	public ContextNode setDeepContextNode(XDI3Segment contextNodeXri);

	/**
	 * Returns the context node with a given arc XRI.
	 * @param arcXri The arc XRI of the context node.
	 * @return The context node with the given arc XRI, or null.
	 */
	public ContextNode getContextNode(XDI3SubSegment arcXri);

	/**
	 * Deep version of ContextNode.getContextNode(XDI3SubSegment), operates at a context node further down in the graph.
	 */
	public ContextNode getDeepContextNode(XDI3Segment contextNodeXri);

	/**
	 * Returns the context nodes of this context node.
	 * @return An iterator over context nodes.
	 */
	public ReadOnlyIterator<ContextNode> getContextNodes();

	/**
	 * Deep version of ContextNode.getContextNodes(), operates at a context node further down in the graph.
	 */
	public ReadOnlyIterator<ContextNode> getDeepContextNodes(XDI3Segment contextNodeXri);

	/**
	 * Returns all context nodes of this context node.
	 * @return An iterator over context nodes.
	 */
	public ReadOnlyIterator<ContextNode> getAllContextNodes();

	/**
	 * Returns all leaf context nodes of this context node.
	 * @return An iterator over leaf context nodes.
	 */
	public ReadOnlyIterator<ContextNode> getAllLeafContextNodes();

	/**
	 * Checks if a context node with a given arc XRI exists in this context node.
	 * @param arcXri The arc XRI to look for. 
	 * @return True if this context node has a context node with the given arc XRI.
	 */
	public boolean containsContextNode(XDI3SubSegment arcXri);

	/**
	 * Checks if this context node has one or more context nodes.
	 * @return True if this context node has context nodes.
	 */
	public boolean containsContextNodes();

	/**
	 * Deletes the context node with a given arc XRI.
	 * @param arcXri The arc XRI of this context arc.
	 */
	public void deleteContextNode(XDI3SubSegment arcXri);

	/**
	 * Deletes all context nodes from this context node.
	 */
	public void deleteContextNodes();

	/**
	 * Returns the number of context nodes of this context node.
	 * @return The number of context nodes.
	 */
	public long getContextNodeCount();

	/**
	 * Returns the number of all context nodes of this context node.
	 * @return The number of context nodes.
	 */
	public long getAllContextNodeCount();

	/*
	 * Methods related to relations of this context node
	 */

	/**
	 * Creates a new relation and adds it to this context node.
	 * @param arcXri The arc XRI of the relation.
	 * @param targetContextNodeXri The target context node XRI of the relation.
	 * @return The newly created relation.
	 */
	public Relation createRelation(XDI3Segment arcXri, XDI3Segment targetContextNodeXri);

	/**
	 * Deep version of ContextNode.createRelation(XDI3Segment, XDI3Segment), operates at a context node further down in the graph.
	 */
	public Relation createDeepRelation(XDI3Segment contextNodeXri, XDI3Segment arcXri, XDI3Segment targetContextNodeXri);

	/**
	 * Creates a new relation and adds it to this context node.
	 * @param arcXri The arc XRI of the relation.
	 * @param targetContextNode The target context node of the relation.
	 * @return The newly created relation.
	 */
	public Relation createRelation(XDI3Segment arcXri, ContextNode targetContextNode);

	/**
	 * Deep version of ContextNode.createRelation(XDI3Segment, ContextNode), operates at a context node further down in the graph.
	 */
	public Relation createDeepRelation(XDI3Segment contextNodeXri, XDI3Segment arcXri, ContextNode targetContextNode);

	/**
	 * Creates a new relation and adds it to this context node, or returns an existing relation.
	 * @param arcXri The arc XRI of the relation.
	 * @param targetContextNodeXri The target context node XRI of the relation.
	 * @return The newly created or existing relation.
	 */
	public Relation setRelation(XDI3Segment arcXri, XDI3Segment targetContextNodeXri);

	/**
	 * Deep version of ContextNode.setRelation(XDI3Segment, XDI3Segment), operates at a context node further down in the graph.
	 */
	public Relation setDeepRelation(XDI3Segment contextNodeXri, XDI3Segment arcXri, XDI3Segment targetContextNodeXri);

	/**
	 * Creates a new relation and adds it to this context node, or returns an existing relation.
	 * @param arcXri The arc XRI of the relation.
	 * @param targetContextNode The target context node of the relation.
	 * @return The newly created or existing relation.
	 */
	public Relation setRelation(XDI3Segment arcXri, ContextNode targetContextNode);

	/**
	 * Deep version of ContextNode.setRelation(XDI3Segment, ContextNode), operates at a context node further down in the graph.
	 */
	public Relation setDeepRelation(XDI3Segment contextNodeXri, XDI3Segment arcXri, ContextNode targetContextNode);

	/**
	 * Returns a relation at this context node. 
	 * @param arcXri The arc XRI to look for. 
	 * @param targetContextNodeXri The target context node XRI of the relation.
	 * @return The relation with the given arc XRI, or null.
	 */
	public Relation getRelation(XDI3Segment arcXri, XDI3Segment targetContextNodeXri);

	/**
	 * Deep version of ContextNode.getRelation(XDI3Segment, XDI3Segment), operates at a context node further down in the graph.
	 */
	public Relation getDeepRelation(XDI3Segment contextNodeXri, XDI3Segment arcXri, XDI3Segment targetContextNodeXri);

	/**
	 * Returns a relation at this context node. 
	 * @param arcXri The arc XRI to look for. 
	 * @return The relation with the given arc XRI, or null.
	 */
	public Relation getRelation(XDI3Segment arcXri);

	/**
	 * Deep version of ContextNode.getRelation(XDI3Segment), operates at a context node further down in the graph.
	 */
	public Relation getDeepRelation(XDI3Segment contextNodeXri, XDI3Segment arcXri);

	/**
	 * Returns relations at this context node. 
	 * @param arcXri The arc XRI to look for. 
	 * @return An iterator over relations with the given arc XRI, or null.
	 */
	public ReadOnlyIterator<Relation> getRelations(XDI3Segment arcXri);

	/**
	 * Deep version of ContextNode.getRelations(XDI3Segment), operates at a context node further down in the graph.
	 */
	public ReadOnlyIterator<Relation> getDeepRelations(XDI3Segment contextNodeXri, XDI3Segment arcXri);

	/**
	 * Returns the relations of this context node.
	 * @return An iterator over relations.
	 */
	public ReadOnlyIterator<Relation> getRelations();

	/**
	 * Deep version of ContextNode.getRelations(), operates at a context node further down in the graph.
	 */
	public ReadOnlyIterator<Relation> getDeepRelations(XDI3Segment contextNodeXri);

	/**
	 * Returns the incoming relations with a given arc XRI.
	 * @param arcXri The arc XRI to look for. 
	 * @return An iterator over relations with the given arc XRI, or null.
	 */
	public ReadOnlyIterator<Relation> getIncomingRelations(XDI3Segment arcXri);

	/**
	 * Returns the incoming relations of this context node.
	 * @return An iterator over relations.
	 */
	public ReadOnlyIterator<Relation> getIncomingRelations();

	/**
	 * Returns all relations of this context node.
	 * @return An iterator over relations.
	 */
	public ReadOnlyIterator<Relation> getAllRelations();

	/**
	 * Returns all incoming relations of this context node.
	 * @return An iterator over relations.
	 */
	public ReadOnlyIterator<Relation> getAllIncomingRelations();

	/**
	 * Checks if a relation with a given arc XRI and target context node XRI exists in this context node.
	 * @param arcXri The arc XRI of the relations. 
	 * @param targetContextNodeXri The target context node XRI of the relation.
	 * @return True if this context node has a relation with the given arc XRI and target context node XRI.
	 */
	public boolean containsRelation(XDI3Segment arcXri, XDI3Segment targetContextNodeXri);

	/**
	 * Checks if relations with a given arc XRI exist in this context node.
	 * @param arcXri The arc XRI of the relations. 
	 * @return True if this context node has relations with the given arc XRI.
	 */
	public boolean containsRelations(XDI3Segment arcXri);

	/**
	 * Checks if this context node has one or more relations.
	 * @return True if this context node has relations.
	 */
	public boolean containsRelations();

	/**
	 * Checks if incoming relations with a given arc XRI exist in this context node.
	 * @param arcXri The arc XRI of the incoming relations. 
	 * @return True if this context node has incoming relations with the given arc XRI.
	 */
	public boolean containsIncomingRelations(XDI3Segment arcXri);

	/**
	 * Checks if this context node has one or more incoming relations.
	 * @return True if this context node has incoming relations.
	 */
	public boolean containsIncomingRelations();

	/**
	 * Deletes the relation with a given arc XRI from this context node.
	 * @param arcXri The arc XRI of the relation.
	 * @param targetContextNodeXri The target context node XRI of the relation.
	 */
	public void deleteRelation(XDI3Segment arcXri, XDI3Segment targetContextNodeXri);

	/**
	 * Deletes the relation with a given arc XRI from this context node.
	 * @param arcXri The arc XRI of the relation.
	 */
	public void deleteRelations(XDI3Segment arcXri);

	/**
	 * Deletes all relations from this context node.
	 */
	public void deleteRelations();

	/**
	 * Deletes the incoming relations of this context node.
	 */
	public void deleteIncomingRelations();

	/**
	 * Returns the number of relations of this context node.
	 * @param arcXri The arc XRI to look for. 
	 * @return The number of relations.
	 */
	public long getRelationCount(XDI3Segment arcXri);

	/**
	 * Returns the number of relations of this context node.
	 * @return The number of relations.
	 */
	public long getRelationCount();

	/**
	 * Returns the number of all relations of this context node.
	 * @return The number of relations.
	 */
	public long getAllRelationCount();

	/*
	 * Methods related to literals of this context node
	 */

	/**
	 * Creates a new literal and adds it to this context node.
	 * @param literalData The literal data associated with the literal.
	 * @return The newly created literal.
	 */
	public Literal createLiteral(Object literalData);

	/**
	 * Creates a new literal and adds it to this context node.
	 * @param literalData The literal data string associated with the literal.
	 * @return The newly created literal.
	 */
	public Literal createLiteralString(String literalData);

	/**
	 * Creates a new literal and adds it to this context node.
	 * @param literalData The literal data number associated with the literal.
	 * @return The newly created literal.
	 */
	public Literal createLiteralNumber(Double literalData);

	/**
	 * Creates a new literal and adds it to this context node.
	 * @param literalData The literal data boolean associated with the literal.
	 * @return The newly created literal.
	 */
	public Literal createLiteralBoolean(Boolean literalData);

	/**
	 * Deep version of ContextNode.createLiteral(Object), operates at a context node further down in the graph.
	 */
	public Literal createDeepLiteral(XDI3Segment contextNodeXri, Object literalData);

	/**
	 * Deep version of ContextNode.createLiteralString(String), operates at a context node further down in the graph.
	 */
	public Literal createDeepLiteralString(XDI3Segment contextNodeXri, String literalData);

	/**
	 * Deep version of ContextNode.createLiteralNumber(Double), operates at a context node further down in the graph.
	 */
	public Literal createDeepLiteralNumber(XDI3Segment contextNodeXri, Double literalData);

	/**
	 * Deep version of ContextNode.createLiteralBoolean(Boolean), operates at a context node further down in the graph.
	 */
	public Literal createDeepLiteralBoolean(XDI3Segment contextNodeXri, Boolean literalData);

	/**
	 * Creates a new literal and adds it to this context node, or returns an existing literal.
	 * @param literalData The literal data associated with the literal.
	 * @return The newly created or existing literal.
	 */
	public Literal setLiteral(Object literalData);

	/**
	 * Creates a new literal and adds it to this context node, or returns an existing literal.
	 * @param literalData The literal data string associated with the literal.
	 * @return The newly created or existing literal.
	 */
	public Literal setLiteralString(String literalData);

	/**
	 * Creates a new literal and adds it to this context node, or returns an existing literal.
	 * @param literalData The literal data number associated with the literal.
	 * @return The newly created or existing literal.
	 */
	public Literal setLiteralNumber(Double literalData);

	/**
	 * Creates a new literal and adds it to this context node, or returns an existing literal.
	 * @param literalData The literal data boolean associated with the literal.
	 * @return The newly created or existing literal.
	 */
	public Literal setLiteralBoolean(Boolean literalData);

	/**
	 * Deep version of ContextNode.setLiteral(Object), operates at a context node further down in the graph.
	 */
	public Literal setDeepLiteral(XDI3Segment contextNodeXri, Object literalData);

	/**
	 * Deep version of ContextNode.setLiteralString(String), operates at a context node further down in the graph.
	 */
	public Literal setDeepLiteralString(XDI3Segment contextNodeXri, String literalData);

	/**
	 * Deep version of ContextNode.setLiteralNumber(Double), operates at a context node further down in the graph.
	 */
	public Literal setDeepLiteralNumber(XDI3Segment contextNodeXri, Double literalData);

	/**
	 * Deep version of ContextNode.setLiteralBoolean(Boolean), operates at a context node further down in the graph.
	 */
	public Literal setDeepLiteralBoolean(XDI3Segment contextNodeXri, Boolean literalData);

	/**
	 * Returns the literal of this context node.
	 * @param literalData The literal data associated with the literal.
	 * @return The literal.
	 */
	public Literal getLiteral(Object literalData);

	/**
	 * Returns the literal of this context node.
	 * @param literalData The literal data string associated with the literal.
	 * @return The literal.
	 */
	public Literal getLiteralString(String literalData);

	/**
	 * Returns the literal of this context node.
	 * @param literalData The literal data number associated with the literal.
	 * @return The literal.
	 */
	public Literal getLiteralNumber(Double literalData);

	/**
	 * Returns the literal of this context node.
	 * @param literalData The literal data boolean associated with the literal.
	 * @return The literal.
	 */
	public Literal getLiteralBoolean(Boolean literalData);

	/**
	 * Deep version of ContextNode.getLiteral(Object), operates at a context node further down in the graph.
	 */
	public Literal getDeepLiteral(XDI3Segment contextNodeXri, Object literalData);

	/**
	 * Deep version of ContextNode.getLiteralString(String), operates at a context node further down in the graph.
	 */
	public Literal getDeepLiteralString(XDI3Segment contextNodeXri, String literalData);

	/**
	 * Deep version of ContextNode.getLiteraNumber(Double), operates at a context node further down in the graph.
	 */
	public Literal getDeepLiteralNumber(XDI3Segment contextNodeXri, Double literalData);

	/**
	 * Deep version of ContextNode.getLiteralBoolean(Boolean), operates at a context node further down in the graph.
	 */
	public Literal getDeepLiteralBoolean(XDI3Segment contextNodeXri, Boolean literalData);

	/**
	 * Returns the literal of this context node.
	 * @return The literal.
	 */
	public Literal getLiteral();

	/**
	 * Deep version of ContextNode.getLiteral(), operates at a context node further down in the graph.
	 */
	public Literal getDeepLiteral(XDI3Segment contextNodeXri);

	/**
	 * Returns all literals of this context node.
	 * @return An iterator over literals.
	 */
	public ReadOnlyIterator<Literal> getAllLiterals();

	/**
	 * Checks if this context node has a literal with the given data.
	 * @param literalData The literal data associated with the literal.
	 * @return True if this context node has a literal with the given data.
	 */
	public boolean containsLiteral(Object literalData);

	/**
	 * Checks if this context node has a literal with the given data.
	 * @param literalData The literal data string associated with the literal.
	 * @return True if this context node has a literal with the given data.
	 */
	public boolean containsLiteralString(String literalData);

	/**
	 * Checks if this context node has a literal with the given data.
	 * @param literalData The literal data number associated with the literal.
	 * @return True if this context node has a literal with the given data.
	 */
	public boolean containsLiteralNumber(Double literalData);

	/**
	 * Checks if this context node has a literal with the given data.
	 * @param literalData The literal data boolean associated with the literal.
	 * @return True if this context node has a literal with the given data.
	 */
	public boolean containsLiteralBoolean(Boolean literalData);

	/**
	 * Checks if this context node has a literal.
	 * @return True if this context node has a literal.
	 */
	public boolean containsLiteral();

	/**
	 * Deletes the literal from this context node.
	 */
	public void deleteLiteral();

	/**
	 * Returns the number of all literals of this context node.
	 * @return The number of literals.
	 */
	public long getAllLiteralCount();

	/*
	 * Methods related to statements
	 */

	/**
	 * Gets the statement that represents this context node.
	 * @return A statement.
	 */
	public ContextNodeStatement getStatement();

	/**
	 * Gets all statements rooted in this context node.
	 * @return An iterator over statements.
	 */
	public ReadOnlyIterator<Statement> getAllStatements();

	/**
	 * Returns the number of all statements rooted in this context node.
	 * @return The number of statements.
	 */
	public long getAllStatementCount();
}
