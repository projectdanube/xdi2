package xdi2.core.features.multiplicity;

import xdi2.core.ContextNode;

/**
 * An XDI entity member of a collection according to the multiplicity pattern, represented as a context node.
 * 
 * @author markus
 */
public class XdiEntityMember extends XdiSubGraph {

	private static final long serialVersionUID = -1075885367630005576L;

	protected XdiEntityMember(ContextNode contextNode) {

		super(contextNode);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI entity member.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI entity member.
	 */
	public static boolean isValid(ContextNode contextNode) {

		return
				Multiplicity.isEntityMemberArcXri(contextNode.getArcXri()) &&
				XdiCollection.isValid(contextNode.getContextNode());
	}

	/**
	 * Factory method that creates an XDI entity member bound to a given context node.
	 * @param contextNode The context node that is an XDI entity member.
	 * @return The XDI entity member.
	 */
	public static XdiEntityMember fromContextNode(ContextNode contextNode) {

		if (! isValid(contextNode)) return null;

		return new XdiEntityMember(contextNode);
	}
}
