package xdi2.core.features.nodetypes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;

public abstract class XdiAbstractSubGraph extends XdiAbstractContext implements XdiSubGraph {

	private static final long serialVersionUID = -6983495055390279007L;

	private static final Logger log = LoggerFactory.getLogger(XdiAbstractSubGraph.class);

	public XdiAbstractSubGraph(ContextNode contextNode) {

		super(contextNode);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI subgraph.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI subgraph.
	 */
	public static boolean isValid(ContextNode contextNode) {

		if (log.isTraceEnabled()) log.trace("isValid(" + contextNode + ")");

		return 
				XdiMetaClass.isValid(contextNode) ||
				XdiAbstractSingleton.isValid(contextNode) ||
				XdiAbstractClass.isValid(contextNode) ||
				XdiAbstractInstance.isValid(contextNode) ||
				XdiValue.isValid(contextNode) ||
				XdiVariable.isValid(contextNode);
	}

	/**
	 * Factory method that creates an XDI subgraph bound to a given context node.
	 * @param contextNode The context node that is an XDI subgraph.
	 * @return The XDI subgraph.
	 */
	public static XdiSubGraph fromContextNode(ContextNode contextNode) {

		if (log.isTraceEnabled()) log.trace("fromContextNode(" + contextNode + ")");

		XdiSubGraph xdiSubGraph;

		if ((xdiSubGraph = XdiMetaClass.fromContextNode(contextNode)) != null) return xdiSubGraph;
		if ((xdiSubGraph = XdiAbstractSingleton.fromContextNode(contextNode)) != null) return xdiSubGraph;
		if ((xdiSubGraph = XdiAbstractClass.fromContextNode(contextNode)) != null) return xdiSubGraph;
		if ((xdiSubGraph = XdiAbstractInstance.fromContextNode(contextNode)) != null) return xdiSubGraph;
		if ((xdiSubGraph = XdiValue.fromContextNode(contextNode)) != null) return xdiSubGraph;
		if ((xdiSubGraph = XdiVariable.fromContextNode(contextNode)) != null) return xdiSubGraph;

		return null;
	}
}
