package xdi2;

import java.io.Serializable;

import xdi2.xri3.impl.XRI3Segment;

/**
 * This interface represents a statement in an XDI graph.
 * A statement consists of a subject, predicate and object.
 * 
 * @author markus
 */
public interface Statement extends Serializable, Comparable<Statement> {

	/*
	 * General methods
	 */

	/**
	 * Returns the graph that contains this statements.
	 * @return The graph that contains this statements.
	 */
	public Graph getGraph();

	/**
	 * Gets the subject of this statement.
	 * @return A subject.
	 */
	public ContextNode getSubject();

	/**
	 * Gets the predicate of this statement.
	 * @return A predicate.
	 */
	public XRI3Segment getPredicate();

	/**
	 * Gets the object of this statement.
	 * @return A object.
	 */
	public XRI3Segment getObject();
}
