package xdi2.core;

import xdi2.core.Statement.ContextNodeStatement;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.syntax.XDIStatement;
import xdi2.core.util.iterators.ReadOnlyIterator;

/**
 * This interface represents a context node in an XDI graph.
 * 
 * @author markus
 */
public interface ContextNode extends Node, Comparable<ContextNode> {

	/*
	 * General methods
	 */

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
	 * Clears the context node. This is equivalent to calling delContextNodes(), delRelations() and delLiteralNode().
	 */
	public void clear();

	/**
	 * Checks if the context node is empty. 
	 * This is equivalent to calling ! ( containsContextNodes() || containsRelations() || containsLiteralNode() ).
	 */
	public boolean isEmpty();

	/*
	 * Methods related to context nodes of this context node
	 */

	/**
	 * Creates a new context node and adds it to this context node, or returns an existing context node.
	 * @param arc The arc of the new or existing context node.
	 * @return The newly created or existing context node.
	 */
	public ContextNode setContextNode(XDIArc XDIarc);

	/**
	 * Returns the context node with a given arc.
	 * @param arc The arc of the context node.
	 * @param subgraph This is simply a hint to the implementation whether 
	 * child context nodes will subsequently be requested. Implementations may 
	 * or may not actually use this parameter.
	 * @return The context node with the given arc, or null.
	 */
	public ContextNode getContextNode(XDIArc XDIarc, boolean subgraph);

	/**
	 * Returns the context node with a given arc.
	 * @param arc The arc of the context node.
	 * @return The context node with the given arc, or null.
	 */
	public ContextNode getContextNode(XDIArc XDIarc);

	/**
	 * Returns the context nodes under this context node.
	 * @return An iterator over context nodes.
	 */
	public ReadOnlyIterator<ContextNode> getContextNodes();

	/**
	 * Returns all context nodes under this context node.
	 * @return An iterator over context nodes.
	 */
	public ReadOnlyIterator<ContextNode> getAllContextNodes();

	/**
	 * Returns all leaf context nodes under this context node.
	 * @return An iterator over leaf context nodes.
	 */
	public ReadOnlyIterator<ContextNode> getAllLeafContextNodes();

	/**
	 * Checks if a context node with a given arc exists under this context node.
	 * @param arc The arc to look for. 
	 * @return True if this context node has a context node with the given arc.
	 */
	public boolean containsContextNode(XDIArc XDIarc);

	/**
	 * Checks if this context node has one or more context nodes.
	 * @return True if this context node has context nodes.
	 */
	public boolean containsContextNodes();

	/**
	 * Deletes the context node with a given arc.
	 * @param arc The arc of the context node.
	 */
	public void delContextNode(XDIArc XDIarc);

	/**
	 * Deletes all context nodes from this context node.
	 */
	public void delContextNodes();

	/**
	 * Returns the number of context nodes under this context node.
	 * @return The number of context nodes.
	 */
	public long getContextNodeCount();

	/**
	 * Returns the number of all context nodes under this context node.
	 * @return The number of context nodes.
	 */
	public long getAllContextNodeCount();

	/*
	 * Methods related to relations of this context node
	 */

	/**
	 * Creates a new relation and adds it to this context node, or returns an existing relation.
	 * @param XDIarc The arc of the relation.
	 * @param targetXDIAddress The target context node address of the relation.
	 * @return The newly created or existing relation.
	 */
	public Relation setRelation(XDIAddress XDIarc, XDIAddress targetXDIAddress);

	/**
	 * Creates a new relation and adds it to this context node, or returns an existing relation.
	 * @param arc The arc of the relation.
	 * @param targetContextNode The target context node of the relation.
	 * @return The newly created or existing relation.
	 */
	public Relation setRelation(XDIAddress XDIaddress, Node targetNode);

	/**
	 * Returns a relation at this context node. 
	 * @param arc The arc to look for. 
	 * @param targetXDIAddress The target context node XRI of the relation.
	 * @return The relation with the given arc, or null.
	 */
	public Relation getRelation(XDIAddress XDIaddress, XDIAddress targetXDIAddress);

	/**
	 * Returns a relation at this context node. 
	 * @param arc The arc to look for. 
	 * @return The relation with the given arc, or null.
	 */
	public Relation getRelation(XDIAddress XDIaddress);

	/**
	 * Returns relations at this context node. 
	 * @param arc The arc to look for. 
	 * @return An iterator over relations with the given arc, or null.
	 */
	public ReadOnlyIterator<Relation> getRelations(XDIAddress XDIaddress);

	/**
	 * Returns the relations of this context node.
	 * @return An iterator over relations.
	 */
	public ReadOnlyIterator<Relation> getRelations();

	/**
	 * Returns the incoming relations with a given arc.
	 * @param arc The arc to look for. 
	 * @return An iterator over relations with the given arc, or null.
	 */
	public ReadOnlyIterator<Relation> getIncomingRelations(XDIAddress XDIaddress);

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
	 * Checks if a relation with a given arc and target context node XRI exists in this context node.
	 * @param arc The arc of the relations. 
	 * @param targetXDIAddress The target context node XRI of the relation.
	 * @return True if this context node has a relation with the given arc and target context node XRI.
	 */
	public boolean containsRelation(XDIAddress XDIaddress, XDIAddress targetXDIAddress);

	/**
	 * Checks if relations with a given arc exist in this context node.
	 * @param arc The arc of the relations. 
	 * @return True if this context node has relations with the given arc.
	 */
	public boolean containsRelations(XDIAddress XDIaddress);

	/**
	 * Checks if this context node has one or more relations.
	 * @return True if this context node has relations.
	 */
	public boolean containsRelations();

	/**
	 * Checks if incoming relations with a given arc exist in this context node.
	 * @param arc The arc of the incoming relations. 
	 * @return True if this context node has incoming relations with the given arc.
	 */
	public boolean containsIncomingRelations(XDIAddress XDIaddress);

	/**
	 * Checks if this context node has one or more incoming relations.
	 * @return True if this context node has incoming relations.
	 */
	public boolean containsIncomingRelations();

	/**
	 * Deletes the relation with a given arc from this context node.
	 * @param arc The arc of the relation.
	 * @param targetXDIAddress The target context node XRI of the relation.
	 */
	public void delRelation(XDIAddress XDIaddress, XDIAddress targetXDIAddress);

	/**
	 * Deletes the relation with a given arc from this context node.
	 * @param arc The arc of the relation.
	 */
	public void delRelations(XDIAddress XDIaddress);

	/**
	 * Deletes all relations from this context node.
	 */
	public void delRelations();

	/**
	 * Deletes the incoming relations of this context node.
	 */
	public void delIncomingRelations();

	/**
	 * Returns the number of relations of this context node.
	 * @param arc The arc to look for. 
	 * @return The number of relations.
	 */
	public long getRelationCount(XDIAddress XDIaddress);

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
	 * Creates a new literal and adds it to this context node, or returns an existing literal.
	 * @param literalData The literal data associated with the literal.
	 * @return The newly created or existing literal.
	 */
	public LiteralNode setLiteralNode(Object literalData);

	/**
	 * Creates a new literal and adds it to this context node, or returns an existing literal.
	 * @param literalData The literal data string associated with the literal.
	 * @return The newly created or existing literal.
	 */
	public LiteralNode setLiteralString(String literalData);

	/**
	 * Creates a new literal and adds it to this context node, or returns an existing literal.
	 * @param literalData The literal data number associated with the literal.
	 * @return The newly created or existing literal.
	 */
	public LiteralNode setLiteralNumber(Double literalData);

	/**
	 * Creates a new literal and adds it to this context node, or returns an existing literal.
	 * @param literalData The literal data boolean associated with the literal.
	 * @return The newly created or existing literal.
	 */
	public LiteralNode setLiteralBoolean(Boolean literalData);

	/**
	 * Returns the literal of this context node.
	 * @return The literal.
	 */
	public LiteralNode getLiteralNode();

	/**
	 * Returns the literal of this context node.
	 * @param literalData The literal data associated with the literal.
	 * @return The literal.
	 */
	public LiteralNode getLiteralNode(Object literalData);

	/**
	 * Returns the literal of this context node.
	 * @param literalData The literal data string associated with the literal.
	 * @return The literal.
	 */
	public LiteralNode getLiteralString(String literalData);

	/**
	 * Returns the literal of this context node.
	 * @param literalData The literal data number associated with the literal.
	 * @return The literal.
	 */
	public LiteralNode getLiteralNumber(Double literalData);

	/**
	 * Returns the literal of this context node.
	 * @param literalData The literal data boolean associated with the literal.
	 * @return The literal.
	 */
	public LiteralNode getLiteralBoolean(Boolean literalData);

	/**
	 * Returns all literals of this context node.
	 * @return An iterator over literals.
	 */
	public ReadOnlyIterator<LiteralNode> getAllLiterals();

	/**
	 * Checks if this context node has a literal with the given data.
	 * @param literalData The literal data associated with the literal.
	 * @return True if this context node has a literal with the given data.
	 */
	public boolean containsLiteralNode(Object literalData);

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
	public boolean containsLiteralNode();

	/**
	 * Deletes the literal from this context node.
	 */
	public void delLiteralNode();

	/**
	 * Returns the number of all literals of this context node.
	 * @return The number of literals.
	 */
	public long getAllLiteralCount();

	/*
	 * Deep methods for nodes
	 */

	public Node setDeepNode(XDIAddress relativeNodeXDIAddress);

	public Node getDeepNode(XDIAddress relativeNodeXDIAddress, boolean subgraph);

	public Node getDeepNode(XDIAddress relativeNodeXDIAddress);

	/*
	 * Deep methods for context nodes
	 */

	public ContextNode setDeepContextNode(XDIAddress relativeContextNodeXDIAddress);

	public ContextNode getDeepContextNode(XDIAddress relativeContextNodeXDIAddress, boolean subgraph);

	public ContextNode getDeepContextNode(XDIAddress relativeContextNodeXDIAddress);

	/*
	 * Deep methods for literal nodes
	 */

	public LiteralNode setDeepLiteralNode(XDIAddress relativeLiteralNodeXDIAddress);

	public LiteralNode getDeepLiteralNode(XDIAddress relativeLiteralNodeXDIAddress, boolean subgraph);

	public LiteralNode getDeepLiteralNode(XDIAddress relativeLiteralNodeXDIAddress);

	/*
	 * Deep methods for relations
	 */

	public Relation setDeepRelation(XDIAddress relativeContextNodeXDIAddress, XDIAddress XDIaddress, XDIAddress targetXDIAddress);

	public Relation setDeepRelation(XDIAddress relativeContextNodeXDIAddress, XDIAddress XDIaddress, Node targetNode);

	public Relation getDeepRelation(XDIAddress relativeContextNodeXDIAddress, XDIAddress XDIaddress, XDIAddress targetXDIAddress);

	public Relation getDeepRelation(XDIAddress relativeContextNodeXDIAddress, XDIAddress XDIaddress);

	public ReadOnlyIterator<Relation> getDeepRelations(XDIAddress relativeContextNodeXDIAddress, XDIAddress XDIaddress);

	/*
	 * Methods related to statements
	 */

	/**
	 * Gets the statement that represents this context node.
	 * @return A statement.
	 */
	public ContextNodeStatement getStatement();

	/**
	 * Sets a statement in this context node.
	 */
	public Statement setStatement(XDIStatement XDIstatement);

	/**
	 * Gets a statement in this context node.
	 */
	public Statement getStatement(XDIStatement XDIstatement);

	/**
	 * Gets all statements in this context node.
	 * @return An iterator over statements.
	 */
	public ReadOnlyIterator<Statement> getAllStatements();

	/**
	 * Check if a statement exists in this context node.
	 */
	public boolean containsStatement(XDIStatement XDIstatement);

	/**
	 * Returns the number of all statements in this context node.
	 * @return The number of statements.
	 */
	public long getAllStatementCount();
}
