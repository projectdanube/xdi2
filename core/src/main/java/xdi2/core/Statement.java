package xdi2.core;

import java.io.Serializable;

import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.syntax.XDIStatement;

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
	public XDIAddress getSubject();

	/**
	 * Gets the predicate of this statement.
	 * @return A predicate.
	 */
	public XDIAddress getPredicate();

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
	public XDIStatement getXDIStatement();

	/*
	 * Sub-interfaces
	 */

	public interface ContextNodeStatement extends Statement {

		@Override
		public XDIAddress getSubject();

		@Override
		public XDIAddress getPredicate();

		@Override
		public XDIArc getObject();

		public ContextNode getContextNode();
	}

	public interface RelationStatement extends Statement {

		@Override
		public XDIAddress getSubject();

		@Override
		public XDIAddress getPredicate();

		@Override
		public XDIAddress getObject();

		public Relation getRelation();
	}

	public interface LiteralStatement extends Statement {

		@Override
		public XDIAddress getSubject();

		@Override
		public XDIAddress getPredicate();

		@Override
		public Object getObject();

		public Literal getLiteral();
	}
}
