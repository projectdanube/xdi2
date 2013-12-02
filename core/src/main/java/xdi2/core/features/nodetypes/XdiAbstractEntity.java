package xdi2.core.features.nodetypes;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;
import xdi2.core.xri3.XDI3Segment;

public abstract class XdiAbstractEntity extends XdiAbstractSubGraph<XdiEntity> implements XdiEntity {

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

		if (contextNode == null) return false;

		return XdiEntitySingleton.isValid(contextNode) || 
				XdiEntityMemberUnordered.isValid(contextNode) ||
				XdiEntityMemberOrdered.isValid(contextNode);
	}

	/**
	 * Factory method that creates an XDI entity bound to a given context node.
	 * @param contextNode The context node that is an XDI entity.
	 * @return The XDI entity.
	 */
	public static XdiEntity fromContextNode(ContextNode contextNode) {

		XdiEntity xdiEntity = null;

		if ((xdiEntity = XdiEntitySingleton.fromContextNode(contextNode)) != null) return xdiEntity;
		if ((xdiEntity = XdiEntityMemberUnordered.fromContextNode(contextNode)) != null) return xdiEntity;
		if ((xdiEntity = XdiEntityMemberOrdered.fromContextNode(contextNode)) != null) return xdiEntity;

		return null;
	}

	static XdiInnerRoot getXdiInnerRoot(XdiEntity xdiEntity, XDI3Segment innerRootPredicateXri, boolean create) {

		XDI3Segment contextNodeXri = xdiEntity.getContextNode().getXri();

		XdiRoot xdiRoot = XdiLocalRoot.findLocalRoot(xdiEntity.getContextNode().getGraph()).findRoot(contextNodeXri, false);

		XDI3Segment innerRootSubjectXri = xdiRoot.getRelativePart(contextNodeXri);

		return xdiRoot.findInnerRoot(innerRootSubjectXri, innerRootPredicateXri, create);
	}

	/*
	 * Instance methods
	 */

	/**
	 * Returns an XDI inner root based on this XDI entity.
	 * @return The XDI inner root.
	 */
	@Override
	public XdiInnerRoot getXdiInnerRoot(XDI3Segment innerRootPredicateXri, boolean create) {

		return getXdiInnerRoot(this, innerRootPredicateXri, create);
	}

	/*
	 * Helper classes
	 */

	public static class MappingContextNodeXdiEntityIterator extends NotNullIterator<XdiEntity> {

		public MappingContextNodeXdiEntityIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, XdiEntity> (contextNodes) {

				@Override
				public XdiEntity map(ContextNode contextNode) {

					return XdiAbstractEntity.fromContextNode(contextNode);
				}
			});
		}
	}
}
