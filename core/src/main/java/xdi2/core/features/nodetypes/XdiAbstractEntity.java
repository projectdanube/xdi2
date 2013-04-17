package xdi2.core.features.nodetypes;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;

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
				XdiEntityInstanceUnordered.isValid(contextNode) ||
				XdiEntityInstanceOrdered.isValid(contextNode);
	}

	/**
	 * Factory method that creates an XDI entity bound to a given context node.
	 * @param contextNode The context node that is an XDI entity.
	 * @return The XDI entity.
	 */
	public static XdiEntity fromContextNode(ContextNode contextNode) {

		XdiEntity xdiEntity = null;

		if ((xdiEntity = XdiEntitySingleton.fromContextNode(contextNode)) != null) return xdiEntity;
		if ((xdiEntity = XdiEntityInstanceUnordered.fromContextNode(contextNode)) != null) return xdiEntity;
		if ((xdiEntity = XdiEntityInstanceOrdered.fromContextNode(contextNode)) != null) return xdiEntity;

		return null;
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
