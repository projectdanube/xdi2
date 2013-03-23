package xdi2.core.features.multiplicity;

import java.util.Iterator;

import xdi2.core.ContextNode;

/**
 * An XDI collection (context function), represented as a context node.
 * 
 * @author markus
 */
public abstract class XdiCollection extends XdiSubGraph {

	private static final long serialVersionUID = -1272132324340649395L;

	protected XdiCollection(ContextNode contextNode) {

		super(contextNode);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI collection.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI collection.
	 */
	public static boolean isValid(ContextNode contextNode) {

		return XdiEntityCollection.isValid(contextNode) || XdiAttributeCollection.isValid(contextNode);
	}

	/**
	 * Factory method that creates an XDI collection bound to a given context node.
	 * @param contextNode The context node that is an XDI collection.
	 * @return The XDI collection.
	 */
	public static XdiCollection fromContextNode(ContextNode contextNode) {

		if (XdiEntityCollection.isValid(contextNode)) return XdiEntityCollection.fromContextNode(contextNode);
		if (XdiAttributeCollection.isValid(contextNode)) return XdiAttributeCollection.fromContextNode(contextNode);

		return null;
	}

	/*
	 * Instance methods
	 */

	public abstract XdiMember createMember();
	public abstract Iterator<? extends XdiMember> members();
	public abstract int membersSize();
}
