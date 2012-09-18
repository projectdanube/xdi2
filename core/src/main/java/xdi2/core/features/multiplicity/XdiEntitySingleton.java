package xdi2.core.features.multiplicity;

import xdi2.core.ContextNode;

/**
 * An XDI entity singleton according to the multiplicity pattern, represented as a context node.
 * 
 * @author markus
 */
public class XdiEntitySingleton extends XdiSubGraph {

	private static final long serialVersionUID = -1075885367630005576L;

	protected XdiEntitySingleton(ContextNode contextNode) {

		super(contextNode);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI entity singleton.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI entity singleton.
	 */
	public static boolean isValid(ContextNode contextNode) {

		return Multiplicity.isEntitySingletonArcXri(contextNode.getArcXri());
	}

	/**
	 * Factory method that creates an XDI entity singleton bound to a given context node.
	 * @param contextNode The context node that is an XDI entity singleton.
	 * @return The XDI entity singleton.
	 */
	public static XdiEntitySingleton fromContextNode(ContextNode contextNode) {

		if (! isValid(contextNode)) return null;

		return new XdiEntitySingleton(contextNode);
	}
}
