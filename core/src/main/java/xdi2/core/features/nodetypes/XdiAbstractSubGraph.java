package xdi2.core.features.nodetypes;

import xdi2.core.ContextNode;

public abstract class XdiAbstractSubGraph<EQ extends XdiContext<EQ>> extends XdiAbstractContext<EQ> implements XdiSubGraph<EQ> {

	private static final long serialVersionUID = -6983495055390279007L;

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

		if (contextNode == null) return false;

		return 
				XdiMetaClass.isValid(contextNode) ||
				XdiAbstractSingleton.isValid(contextNode) ||
				XdiAbstractCollection.isValid(contextNode) ||
				XdiAbstractMember.isValid(contextNode) ||
				XdiValue.isValid(contextNode) ||
				XdiVariable.isValid(contextNode);
	}

	/**
	 * Factory method that creates an XDI subgraph bound to a given context node.
	 * @param contextNode The context node that is an XDI subgraph.
	 * @return The XDI subgraph.
	 */
	public static XdiSubGraph<?> fromContextNode(ContextNode contextNode) {

		XdiSubGraph<?> xdiSubGraph;

		if ((xdiSubGraph = XdiMetaClass.fromContextNode(contextNode)) != null) return xdiSubGraph;
		if ((xdiSubGraph = XdiAbstractSingleton.fromContextNode(contextNode)) != null) return xdiSubGraph;
		if ((xdiSubGraph = XdiAbstractCollection.fromContextNode(contextNode)) != null) return xdiSubGraph;
		if ((xdiSubGraph = XdiAbstractMember.fromContextNode(contextNode)) != null) return xdiSubGraph;
		if ((xdiSubGraph = XdiValue.fromContextNode(contextNode)) != null) return xdiSubGraph;
		if ((xdiSubGraph = XdiVariable.fromContextNode(contextNode)) != null) return xdiSubGraph;

		return null;
	}
}
