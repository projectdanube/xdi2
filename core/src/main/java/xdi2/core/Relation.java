package xdi2.core;

import java.io.Serializable;

import xdi2.core.Statement.RelationStatement;
import xdi2.core.syntax.XDIAddress;

/**
 * This interface represents a relation in an XDI graph.
 * 
 * @author markus
 */
public interface Relation extends Serializable, Comparable<Relation> {

	/*
	 * General methods
	 */

	/**
	 * Get the graph of this relation.
	 * @return The graph of this relation.
	 */
	public Graph getGraph();

	/**
	 * Every relation has a context node from which it originates.
	 * @return The context node of this relation.
	 */
	public ContextNode getContextNode();

	/**
	 * Deletes this relation.
	 */
	public void delete();

	/**
	 * Every relation has an associated arc.
	 * @return The arc associated with the relation.
	 */
	public XDIAddress getArc();

	/**
	 * Get the target context node address.
	 * @return The target context node address of the relation.
	 */
	public XDIAddress getTargetContextNodeAddress();

	/**
	 * Follows the relation to the target context node.
	 * @return The target context node of the relation.
	 */
	public ContextNode follow();

	/*
	 * Methods related to statements
	 */

	/**
	 * Gets the statement that represents this relation.
	 * @return A statement.
	 */
	public RelationStatement getStatement();
}
