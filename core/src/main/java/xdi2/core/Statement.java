package xdi2.core;

import java.io.Serializable;

import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;
import xdi2.core.xri3.XDI3SubSegment;

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
	public XDI3Segment getSubject();

	/**
	 * Gets the predicate of this statement.
	 * @return A predicate.
	 */
	public XDI3Segment getPredicate();

	/**
	 * Gets the object of this statement.
	 * @return An object.
	 */
	public Object getObject();

	/**
	 * Returns the graph that contains this statement.
	 * @return The graph that contains this statement.
	 */
	public Graph getGraph();

	/**
	 * Checks if this statement is implied by other statements in the graph.
	 * @return True, if this statement is implied by other statements in the graph.
	 */
	public boolean isImplied();

	/**
	 * Delete the statement.
	 */
	public void delete();

	/**
	 * Expresses the statement as an XDI statement in the form subject/predicate/object
	 * @return An XDI statement.
	 */
	public XDI3Statement getXri();

	/*
	 * Sub-interfaces
	 */

	public interface ContextNodeStatement extends Statement {

		@Override
		public XDI3Segment getSubject();

		@Override
		public XDI3Segment getPredicate();

		@Override
		public XDI3SubSegment getObject();

		public ContextNode getContextNode();
	}

	public interface RelationStatement extends Statement {

		@Override
		public XDI3Segment getSubject();

		@Override
		public XDI3Segment getPredicate();

		@Override
		public XDI3Segment getObject();

		public Relation getRelation();
	}

	public interface LiteralStatement extends Statement {

		@Override
		public XDI3Segment getSubject();

		@Override
		public XDI3Segment getPredicate();

		@Override
		public Object getObject();

		public Literal getLiteral();
	}
}
