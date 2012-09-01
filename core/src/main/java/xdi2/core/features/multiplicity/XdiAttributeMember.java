package xdi2.core.features.multiplicity;

import xdi2.core.ContextNode;

/**
 * An XDI attribute member of a collection according to the multiplicity pattern, represented as a context node.
 * 
 * @author markus
 */
public class XdiAttributeMember extends XdiSubGraph {

	private static final long serialVersionUID = 1027868266675630350L;

	protected XdiAttributeMember(ContextNode contextNode) {

		super(contextNode);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI attribute member.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI attribute member.
	 */
	public static boolean isValid(ContextNode contextNode) {

		return
				Multiplicity.isAttributeMemberArcXri(contextNode.getArcXri()) &&
				XdiCollection.isValid(contextNode.getContextNode());
	}

	/**
	 * Factory method that creates an XDI attribute member bound to a given context node.
	 * @param contextNode The context node that is an XDI attribute member.
	 * @return The XDI attribute member.
	 */
	public static XdiAttributeMember fromContextNode(ContextNode contextNode) {

		if (! isValid(contextNode)) return null;

		return new XdiAttributeMember(contextNode);
	}
}
