package xdi2.core.features.contextfunctions;

import xdi2.core.ContextNode;
import xdi2.core.xri3.XDI3SubSegment;

public abstract class XdiEntity extends XdiSubGraph {

	private static final long serialVersionUID = -1976646316893343570L;

	protected XdiEntity(ContextNode contextNode) {

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

		return XdiEntitySingleton.isValid(contextNode) || XdiEntityMember.isValid(contextNode);
	}

	/**
	 * Factory method that creates an XDI entity bound to a given context node.
	 * @param contextNode The context node that is an XDI entity.
	 * @return The XDI entity.
	 */
	public static XdiEntity fromContextNode(ContextNode contextNode) {

		XdiEntity xdiEntity;

		if ((xdiEntity = XdiEntitySingleton.fromContextNode(contextNode)) != null) return xdiEntity;
		if ((xdiEntity = XdiEntityMember.fromContextNode(contextNode)) != null) return xdiEntity;

		return null;
	}

	/*
	 * Methods for XDI entity XRIs
	 */

	public static boolean isEntityArcXri(XDI3SubSegment arcXri) {

		return XdiEntitySingleton.isEntitySingletonArcXri(arcXri) || XdiEntityMember.isEntityMemberArcXri(arcXri);
	}
}
