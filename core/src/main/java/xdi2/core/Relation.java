package xdi2.core;

import java.io.Serializable;

import xdi2.core.Statement.RelationStatement;
import xdi2.core.xri3.impl.XRI3Segment;

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
	 * Every relation has an associated arc XRI.
	 * @return The arc XRI associated with the relation.
	 */
	public XRI3Segment getArcXri();

	/**
	 * Get the target context node XRI.
	 * @return The target context node XRI of the relation.
	 */
	public XRI3Segment getTargetContextNodeXri();

	/**
	 * Follows the relation to the target context node.
	 * @return The target context node of the relation.
	 */
	public ContextNode follow();

	/*
	 * Methods related to statements
	 */

	/**
	 * Gets the statement that contains this literal.
	 * @return A statement.
	 */
	public RelationStatement getStatement();
}
