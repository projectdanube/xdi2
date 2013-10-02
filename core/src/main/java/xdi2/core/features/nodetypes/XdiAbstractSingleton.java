package xdi2.core.features.nodetypes;

import xdi2.core.ContextNode;
import xdi2.core.xri3.XDI3SubSegment;

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

		if (contextNode == null) return false;

		return XdiEntitySingleton.isValid(contextNode) || 
				XdiAttributeSingleton.isValid(contextNode);
	}

	/**
	 * Factory method that creates an XDI singleton bound to a given context node.
	 * @param contextNode The context node that is an XDI singleton.
	 * @return The XDI singleton.
	 */
	public static XdiSingleton<?> fromContextNode(ContextNode contextNode) {

		XdiSingleton<?> xdiSingleton;

		if ((xdiSingleton = XdiEntitySingleton.fromContextNode(contextNode)) != null) return xdiSingleton;
		if ((xdiSingleton = XdiAttributeSingleton.fromContextNode(contextNode)) != null) return xdiSingleton;

		return null;
	}

	/*
	 * Methods for XRIs
	 */

	public static boolean isValidArcXri(XDI3SubSegment arcXri) {

		return XdiEntitySingleton.isValidArcXri(arcXri) || 
				XdiAttributeSingleton.isValidArcXri(arcXri);
	}
}
