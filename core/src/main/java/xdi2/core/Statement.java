package xdi2.core;

import java.io.Serializable;

import xdi2.core.xri3.impl.XRI3Reference;
import xdi2.core.xri3.impl.XRI3Segment;

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
	 * Gets the subject of this statement.
	 * @return A subject.
	 */
	public XRI3Segment getSubject();

	/**
	 * Gets the predicate of this statement.
	 * @return A predicate.
	 */
	public XRI3Segment getPredicate();

	/**
	 * Gets the object of this statement.
	 * @return An object.
	 */
	public XRI3Segment getObject();

	/**
	 * Returns the graph that contains this statement.
	 * @return The graph that contains this statement.
	 */
	public Graph getGraph();

	/**
	 * Returns the context node XRI of this statement.
	 */
	public XRI3Segment getContextNodeXri();

	/**
	 * Delete the statement.
	 */
	public void delete();

	/**
	 * Expresses the statement as an XRI reference in the form subject/predicate/object
	 * @return An XRI reference.
	 */
	public XRI3Reference toXriReference();

	/**
	 * Expresses the statement as an XRI segment in the form (subject/predicate/object)
	 * @return An XRI segment.
	 */
	public XRI3Segment toXriSegment();

	/**
	 * Returns the String representation of this statement.
	 * @param pretty A boolean flag to check whether to prettyprint the output.
	 * @return The String representation of this statement.
	 */
	public String toString(boolean pretty);

	/*
	 * Sub-interfaces
	 */

	public interface ContextNodeStatement extends Statement {

		public ContextNode getContextNode();
	}

	public interface RelationStatement extends Statement {

		public Relation getRelation();
	}

	public interface LiteralStatement extends Statement {

		public Literal getLiteral();
	}
}
