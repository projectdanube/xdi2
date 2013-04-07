package xdi2.core.features.contextfunctions;

import xdi2.core.ContextNode;

public abstract class XdiAbstractEntity extends XdiAbstractSubGraph implements XdiEntity {

	private static final long serialVersionUID = 7648046902369626744L;

	protected XdiAbstractEntity(ContextNode contextNode) {

		super(contextNode);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI entity.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI entity.
	 */
	public static boolean isValid(ContextNode contextNode) {

		return XdiEntitySingleton.isValid(contextNode) || 
				XdiEntityInstance.isValid(contextNode) ||
				XdiEntityElement.isValid(contextNode);
	}

	/**
	 * Factory method that creates an XDI entity bound to a given context node.
	 * @param contextNode The context node that is an XDI entity.
	 * @return The XDI entity.
	 */
	public static XdiEntity fromContextNode(ContextNode contextNode) {

		XdiEntity xdiEntity = null;

		if ((xdiEntity = XdiEntitySingleton.fromContextNode(contextNode)) != null) return xdiEntity;
		if ((xdiEntity = XdiEntityInstance.fromContextNode(contextNode)) != null) return xdiEntity;
		if ((xdiEntity = XdiEntityElement.fromContextNode(contextNode)) != null) return xdiEntity;

		return null;
	}
}
