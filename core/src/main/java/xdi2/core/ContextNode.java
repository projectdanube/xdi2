package xdi2.core;

import java.io.Serializable;

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
	 * Every context node has a parent context node, except the root context node.
	 * @return The parent context node of this context node, or null.
	 */
	public ContextNode getContextNode();

	/**
	 * Every context node has ancestor context nodes, except the root context node.
	 * @param arcs The number of arcs to follow up the graph.
	 * @return The ancestor context node of this context node, or null.
	 */
	public ContextNode getContextNode(int arcs);

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
	 * Clears the context node. This is equivalent to calling delContextNodes(), delRelations() and delLiteral().
	 */
	public void clear();

	/**
	 * Checks if the context node is empty. 
	 * This is equivalent to calling ! ( containsContextNodes() || containsRelations() || containsLiteral() ).
	 */
	public boolean isEmpty();

	/**
	 * Every context node has an associated arc.
	 * This returns null for the root context node.
	 * @return The arc associated with this context node.
	 */
	public XDIArc getXDIArc();

	/**
	 * Gets the address of this context node.
	 * This returns the empty string for the root context node.
	 * @return The address of this context node.
	 */
	public XDIAddress getXDIAddress();

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
	 * Deep version of ContextNode.setContextNode(XDIArc), operates at a context node further down in the graph.
	 */
	public ContextNode setDeepContextNode(XDIAddress relativeContextNodeXDIAddress);

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
	 * Deep version of ContextNode.getContextNode(XDIArc, boolean), operates at a context node further down in the graph.
	 */
	public ContextNode getDeepContextNode(XDIAddress relativeContextNodeXDIAddress, boolean subgraph);

	/**
	 * Deep version of ContextNode.getContextNode(XDIArc), operates at a context node further down in the graph.
	 */
	public ContextNode getDeepContextNode(XDIAddress relativeContextNodeXDIAddress);

	/**
	 * Returns the context nodes under this context node.
	 * @return An iterator over context nodes.
	 */
	public ReadOnlyIterator<ContextNode> getContextNodes();

	/**
	 * Deep version of ContextNode.getContextNodes(), operates at a context node further down in the graph.
	 */
	public ReadOnlyIterator<ContextNode> getDeepContextNodes(XDIAddress relativeContextNodeXDIAddress);

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
	 * @param targetContextNodeXDIAddress The target context node address of the relation.
	 * @return The newly created or existing relation.
	 */
	public Relation setRelation(XDIAddress XDIarc, XDIAddress targetContextNodeXDIAddress);

	/**
	 * Deep version of ContextNode.setRelation(XDIAddress, XDIAddress), operates at a context node further down in the graph.
	 */
	public Relation setDeepRelation(XDIAddress relativeContextNodeXDIAddress, XDIAddress XDIarc, XDIAddress targetContextNodeXDIAddress);

	/**
	 * Creates a new relation and adds it to this context node, or returns an existing relation.
	 * @param arc The arc of the relation.
	 * @param targetContextNode The target context node of the relation.
	 * @return The newly created or existing relation.
	 */
	public Relation setRelation(XDIAddress XDIaddress, ContextNode targetContextNode);

	/**
	 * Deep version of ContextNode.setRelation(XDIAddress, ContextNode), operates at a context node further down in the graph.
	 */
	public Relation setDeepRelation(XDIAddress relativeContextNodeXDIAddress, XDIAddress XDIaddress, ContextNode targetContextNode);

	/**
	 * Returns a relation at this context node. 
	 * @param arc The arc to look for. 
	 * @param targetContextNodeXDIAddress The target context node XRI of the relation.
	 * @return The relation with the given arc, or null.
	 */
	public Relation getRelation(XDIAddress XDIaddress, XDIAddress targetContextNodeXDIAddress);

	/**
	 * Deep version of ContextNode.getRelation(XDIAddress, XDIAddress), operates at a context node further down in the graph.
	 */
	public Relation getDeepRelation(XDIAddress relativeContextNodeXDIAddress, XDIAddress XDIaddress, XDIAddress targetContextNodeXDIAddress);

	/**
	 * Returns a relation at this context node. 
	 * @param arc The arc to look for. 
	 * @return The relation with the given arc, or null.
	 */
	public Relation getRelation(XDIAddress XDIaddress);

	/**
	 * Deep version of ContextNode.getRelation(XDIAddress), operates at a context node further down in the graph.
	 */
	public Relation getDeepRelation(XDIAddress relativeContextNodeXDIAddress, XDIAddress XDIaddress);

	/**
	 * Returns relations at this context node. 
	 * @param arc The arc to look for. 
	 * @return An iterator over relations with the given arc, or null.
	 */
	public ReadOnlyIterator<Relation> getRelations(XDIAddress XDIaddress);

	/**
	 * Deep version of ContextNode.getRelations(XDIAddress), operates at a context node further down in the graph.
	 */
	public ReadOnlyIterator<Relation> getDeepRelations(XDIAddress relativeContextNodeXDIAddress, XDIAddress XDIaddress);

	/**
	 * Returns the relations of this context node.
	 * @return An iterator over relations.
	 */
	public ReadOnlyIterator<Relation> getRelations();

	/**
	 * Deep version of ContextNode.getRelations(), operates at a context node further down in the graph.
	 */
	public ReadOnlyIterator<Relation> getDeepRelations(XDIAddress relativeContextNodeXDIAddress);

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
	 * @param targetContextNodeXDIAddress The target context node XRI of the relation.
	 * @return True if this context node has a relation with the given arc and target context node XRI.
	 */
	public boolean containsRelation(XDIAddress XDIaddress, XDIAddress targetContextNodeXDIAddress);

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
	 * @param targetContextNodeXDIAddress The target context node XRI of the relation.
	 */
	public void delRelation(XDIAddress XDIaddress, XDIAddress targetContextNodeXDIAddress);

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
	public Literal setDeepLiteral(XDIAddress relativeContextNodeXDIAddress, Object literalData);

	/**
	 * Deep version of ContextNode.setLiteralString(String), operates at a context node further down in the graph.
	 */
	public Literal setDeepLiteralString(XDIAddress relativeContextNodeXDIAddress, String literalData);

	/**
	 * Deep version of ContextNode.setLiteralNumber(Double), operates at a context node further down in the graph.
	 */
	public Literal setDeepLiteralNumber(XDIAddress relativeContextNodeXDIAddress, Double literalData);

	/**
	 * Deep version of ContextNode.setLiteralBoolean(Boolean), operates at a context node further down in the graph.
	 */
	public Literal setDeepLiteralBoolean(XDIAddress relativeContextNodeXDIAddress, Boolean literalData);

	/**
	 * Returns the literal of this context node.
	 * @return The literal.
	 */
	public Literal getLiteral();

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
	public Literal getDeepLiteral(XDIAddress relativeContextNodeXDIAddress, Object literalData);

	/**
	 * Deep version of ContextNode.getLiteralString(String), operates at a context node further down in the graph.
	 */
	public Literal getDeepLiteralString(XDIAddress relativeContextNodeXDIAddress, String literalData);

	/**
	 * Deep version of ContextNode.getLiteraNumber(Double), operates at a context node further down in the graph.
	 */
	public Literal getDeepLiteralNumber(XDIAddress relativeContextNodeXDIAddress, Double literalData);

	/**
	 * Deep version of ContextNode.getLiteralBoolean(Boolean), operates at a context node further down in the graph.
	 */
	public Literal getDeepLiteralBoolean(XDIAddress relativeContextNodeXDIAddress, Boolean literalData);

	/**
	 * Deep version of ContextNode.getLiteral(), operates at a context node further down in the graph.
	 */
	public Literal getDeepLiteral(XDIAddress relativeContextNodeXDIAddress);

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
	public void delLiteral();

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
