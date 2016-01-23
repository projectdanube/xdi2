package xdi2.core.features.nodetypes;

import xdi2.core.ContextNode;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.util.GraphUtil;

public abstract class XdiAbstractSubGraph<EQ extends XdiSubGraph<EQ>> extends XdiAbstractContext<EQ> implements XdiSubGraph<EQ> {

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

		if (contextNode == null) throw new NullPointerException();

		if (XdiAbstractSingleton.isValid(contextNode)) return true;
		if (XdiAbstractCollection.isValid(contextNode)) return true;
		if (XdiAbstractInstance.isValid(contextNode)) return true;

		return false;
	}

	/**
	 * Factory method that creates an XDI subgraph bound to a given context node.
	 * @param contextNode The context node that is an XDI subgraph.
	 * @return The XDI subgraph.
	 */
	public static XdiSubGraph<?> fromContextNode(ContextNode contextNode) {

		if (contextNode == null) throw new NullPointerException();

		XdiSubGraph<?> xdiSubGraph;

		if ((xdiSubGraph = XdiAbstractSingleton.fromContextNode(contextNode)) != null) return xdiSubGraph;
		if ((xdiSubGraph = XdiAbstractCollection.fromContextNode(contextNode)) != null) return xdiSubGraph;
		if ((xdiSubGraph = XdiAbstractInstance.fromContextNode(contextNode)) != null) return xdiSubGraph;

		return null;
	}

	public static XdiSubGraph<?> fromXDIAddress(XDIAddress XDIaddress) {

		return fromContextNode(GraphUtil.contextNodeFromComponents(XDIaddress));
	}
}
