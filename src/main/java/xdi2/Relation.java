package xdi2;

import java.io.Serializable;

import xdi2.xri3.impl.XRI3Segment;
import xdi2.xri3.impl.XRI3SubSegment;

/**
 * This interface represents a predicate in an XDI graph. Methods include
 * creating and finding references, literals and inner graphs of the predicate.
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
	public XRI3SubSegment getArcXri();

	/**
	 * Get the relation XRI.
	 * @return The relation XRI associated with the relation.
	 */
	public XRI3Segment getRelationXri();

	/**
	 * Set the relation XRI.
	 * @param relationXri The relation XRI associated with the relation.
	 */
	public void setRelationXri(XRI3Segment relationXri);
}
