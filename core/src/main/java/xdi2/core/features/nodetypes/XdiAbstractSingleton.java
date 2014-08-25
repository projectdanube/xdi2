package xdi2.core.features.nodetypes;

import xdi2.core.ContextNode;
import xdi2.core.syntax.XDIArc;

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
		if (XdiVariableSingleton.isValid(contextNode)) return true;

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
		if ((xdiSingleton = XdiVariableSingleton.fromContextNode(contextNode)) != null) return xdiSingleton;

		return null;
	}

	/*
	 * Methods for arcs
	 */

	public static boolean isSingletonXDIArc(XDIArc XDIarc) {

		if (XDIarc == null) throw new NullPointerException();

		if (XdiEntitySingleton.isEntitySingletonXDIArc(XDIarc)) return true; 
		if (XdiAttributeSingleton.isAttributeSingletonXDIArc(XDIarc)) return true;
		if (XdiVariableSingleton.isVariableSingletonXDIArc(XDIarc)) return true;

		return false;
	}
}
