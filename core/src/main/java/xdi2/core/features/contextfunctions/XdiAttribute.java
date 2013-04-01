package xdi2.core.features.contextfunctions;

import xdi2.core.ContextNode;
import xdi2.core.xri3.XDI3SubSegment;

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

		XdiAttribute xdiAttribute;

		if ((xdiAttribute = XdiAttributeSingleton.fromContextNode(contextNode)) != null) return xdiAttribute;
		if ((xdiAttribute = XdiAttributeMember.fromContextNode(contextNode)) != null) return xdiAttribute;

		return null;
	}

	/*
	 * Methods for XDI attribute XRIs
	 */

	public static boolean isAttributeArcXri(XDI3SubSegment arcXri) {

		return XdiAttributeSingleton.isAttributeSingletonArcXri(arcXri) || XdiAttributeMember.isAttributeMemberArcXri(arcXri);
	}
}
