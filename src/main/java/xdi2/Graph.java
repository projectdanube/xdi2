package xdi2;

import java.io.Serializable;

import org.eclipse.higgins.xdi4j.exceptions.MessagingException;

import xdi2.xri3.impl.XRI3;
import xdi2.xri3.impl.XRI3Segment;

/**
 * This interface represents a whole XDI graph.
 * XDI graphs consist of context nodes, arcs, relations, and literals.
 * 
 * @author markus
 */
public interface Graph extends Serializable, Comparable<Graph> {

	/*
	 * General methods
	 */

	/**
	 * Gets the root context node of this graph.
	 * @return A context node.
	 */
	public ContextNode getRootContextNode();

	/**
	 * Finds a context node in this graph.
	 * @return A context node.
	 */
	public ContextNode findContextNode(XRI3Segment xri);

	/*
	 * Methods related to messages
	 */

	/**
	 * A simple way to apply an XDI operation to this graph,
	 * based on a given input graph.
	 */
	public Graph applyOperation(Graph operationGraph, XRI3Segment operationXri) throws MessagingException;

	/**
	 * A simple way to apply an XDI operation to this graph,
	 * based on a given input address.
	 */
	public Graph applyOperation(XRI3 address, XRI3Segment operationXri) throws MessagingException;

	/*
	 * Methods related to transactions
	 */

	/**
	 * Starts a new transaction.
	 */
	public void beginTransaction();

	/**
	 * Commits the changes made by the transaction.
	 */
	public void commitTransaction();

	/**
	 * Rolls back the changes made by the transaction.
	 */
	public void rollbackTransaction();
}
