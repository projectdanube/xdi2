package xdi2.core;

import java.io.Serializable;

import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;

public interface Node extends Serializable {

	/*
	 * General methods
	 */

	/**
	 * Get the graph of this node.
	 * @return The graph of this node.
	 */
	public Graph getGraph();

	/**
	 * Every node has a parent context node, except the root context node.
	 * @return The parent context node of this node, or null.
	 */
	public ContextNode getContextNode();

	/**
	 * Every node has ancestor context nodes, except the root context node.
	 * @param arcs The number of arcs to follow up the graph.
	 * @return The ancestor context node of this node, or null.
	 */
	public ContextNode getContextNode(int arcs);

	/**
	 * Gets the address of this node.
	 * This returns the empty string for the root context node.
	 * @return The address of this node.
	 */
	public XDIAddress getXDIAddress();

	/**
	 * Every node has an associated arc.
	 * This returns null for the root context node.
	 * @return The arc associated with this node.
	 */
	public XDIArc getXDIArc();

	/**
	 * Deletes this node.
	 */
	public void delete();
}
