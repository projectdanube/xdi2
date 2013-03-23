package xdi2.core.features.multiplicity;

import xdi2.core.ContextNode;

/**
 * An XDI attribute member of a collection (context function), represented as a context node.
 * 
 * @author markus
 */
public final class XdiAttributeMember extends XdiMember implements XdiAttribute {

	private static final long serialVersionUID = -1075885367630005576L;

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
				Multiplicity.isMemberArcXri(contextNode.getArcXri()) &&
				XdiAttributeCollection.isValid(contextNode.getContextNode());
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

	/*
	 * Instance methods
	 */

	/**
	 * Gets or returns the parent XDI attribute collection of this XDI attribute member.
	 * @return The parent XDI attribute collection.
	 */
	@Override
	public XdiAttributeCollection getCollection() {

		return new XdiAttributeCollection(this.getContextNode().getContextNode());
	}
}
