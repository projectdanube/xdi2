package xdi2.core.features.multiplicity;

import xdi2.core.ContextNode;

/**
 * An XDI member of a collection (context function), represented as a context node.
 * 
 * @author markus
 */
public abstract class XdiMember extends XdiSubGraph {

	private static final long serialVersionUID = -1075885367630005576L;

	protected XdiMember(ContextNode contextNode) {

		super(contextNode);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI member.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI member.
	 */
	public static boolean isValid(ContextNode contextNode) {

		return XdiEntityMember.isValid(contextNode) || XdiAttributeMember.isValid(contextNode);
	}

	/**
	 * Factory method that creates an XDI member bound to a given context node.
	 * @param contextNode The context node that is an XDI member.
	 * @return The XDI member.
	 */
	public static XdiMember fromContextNode(ContextNode contextNode) {

		if (XdiEntityMember.isValid(contextNode)) return XdiEntityMember.fromContextNode(contextNode);
		if (XdiAttributeMember.isValid(contextNode)) return XdiAttributeMember.fromContextNode(contextNode);

		return null;
	}

	/*
	 * Instance methods
	 */

	/**
	 * Gets or returns the parent XDI collection of this XDI entity member.
	 * @return The parent XDI collection.
	 */
	public abstract XdiCollection getCollection();
}
