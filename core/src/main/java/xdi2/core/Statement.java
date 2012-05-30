package xdi2.core;

import java.io.Serializable;

import xdi2.core.xri3.impl.XRI3;
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
	 * @return A object.
	 */
	public XRI3Segment getObject();

	/**
	 * Gets the corresponding XRI for the statement.
	 * @return An XRI.
	 */
	public XRI3 getXRI3();

	/**
	 * Returns the graph that contains this statements.
	 * @return The graph that contains this statements.
	 */
	public Graph getGraph();

	/**
	 * Delete the statement.
	 */
	public void delete();

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
