package xdi2.core.features.nodetypes;

import xdi2.core.ContextNode;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.util.GraphUtil;

public abstract class XdiAbstractSingleton<EQ extends XdiSubGraph<EQ>> extends XdiAbstractSubGraph<EQ> implements XdiSingleton<EQ> {

	private static final long serialVersionUID = -1976646316893343570L;

	protected XdiAbstractSingleton(ContextNode contextNode) {

		super(contextNode);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI singleton.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI singleton.
	 */
	public static boolean isValid(ContextNode contextNode) {

		if (contextNode == null) throw new NullPointerException();

		if (XdiEntitySingleton.isValid(contextNode)) return true; 
		if (XdiAttributeSingleton.isValid(contextNode)) return true;

		return false;
	}

	/**
	 * Factory method that creates an XDI singleton bound to a given context node.
	 * @param contextNode The context node that is an XDI singleton.
	 * @return The XDI singleton.
	 */
	public static XdiSingleton<?> fromContextNode(ContextNode contextNode) {

		if (contextNode == null) throw new NullPointerException();

		XdiSingleton<?> xdiSingleton;

		if ((xdiSingleton = XdiEntitySingleton.fromContextNode(contextNode)) != null) return xdiSingleton;
		if ((xdiSingleton = XdiAttributeSingleton.fromContextNode(contextNode)) != null) return xdiSingleton;

		return null;
	}

	public static XdiSingleton<?> fromXDIAddress(XDIAddress XDIaddress) {

		return fromContextNode(GraphUtil.contextNodeFromComponents(XDIaddress));
	}

	/*
	 * Methods for arcs
	 */

	public static boolean isValidXDIArc(XDIArc XDIarc) {

		if (XDIarc == null) throw new NullPointerException();

		if (XdiEntitySingleton.isValidXDIArc(XDIarc)) return true; 
		if (XdiAttributeSingleton.isValidXDIArc(XDIarc)) return true;

		return false;
	}
}
