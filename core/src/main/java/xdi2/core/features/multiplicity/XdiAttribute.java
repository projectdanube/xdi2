package xdi2.core.features.multiplicity;

import xdi2.core.ContextNode;

public abstract class XdiAttribute extends XdiSubGraph {

	private static final long serialVersionUID = -4944747722611979113L;

	protected XdiAttribute(ContextNode contextNode) {

		super(contextNode);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI attribute.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI attribute.
	 */
	public static boolean isValid(ContextNode contextNode) {

		return XdiAttributeSingleton.isValid(contextNode) || XdiAttributeMember.isValid(contextNode);
	}

	/**
	 * Factory method that creates an XDI attribute bound to a given context node.
	 * @param contextNode The context node that is an XDI attribute.
	 * @return The XDI attribute.
	 */
	public static XdiAttribute fromContextNode(ContextNode contextNode) {

		if (XdiAttributeSingleton.isValid(contextNode)) return XdiAttributeSingleton.fromContextNode(contextNode);
		if (XdiAttributeMember.isValid(contextNode)) return XdiAttributeMember.fromContextNode(contextNode);

		return null;
	}
}
