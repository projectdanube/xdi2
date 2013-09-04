package xdi2.core.features.nodetypes;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;

public abstract class XdiAbstractInstance<C extends XdiClass<C, U, O, I>, U extends XdiInstanceUnordered<C, U, O, I>, O extends XdiInstanceOrdered<C, U, O, I>, I extends XdiInstance<C, U, O, I>> extends XdiAbstractSubGraph implements XdiInstance<C, U, O, I> {

	private static final long serialVersionUID = 3673396905245169194L;

	protected XdiAbstractInstance(ContextNode contextNode) {

		super(contextNode);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI instance.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI instance.
	 */
	public static boolean isValid(ContextNode contextNode) {

		return XdiAbstractInstanceUnordered.isValid(contextNode) ||
				XdiAbstractInstanceOrdered.isValid(contextNode);
	}

	/**
	 * Factory method that creates an XDI instance bound to a given context node.
	 * @param contextNode The context node that is an XDI instance.
	 * @return The XDI instance.
	 */
	public static XdiInstance<?, ?, ?, ?> fromContextNode(ContextNode contextNode) {

		XdiInstance<?, ?, ?, ?> xdiInstance = null;

		if ((xdiInstance = XdiAbstractInstanceUnordered.fromContextNode(contextNode)) != null) return xdiInstance;
		if ((xdiInstance = XdiAbstractInstanceOrdered.fromContextNode(contextNode)) != null) return xdiInstance;

		return null;
	}

	/*
	 * Helper classes
	 */

	public static class MappingContextNodeXdiInstanceIterator extends NotNullIterator<XdiInstance<?, ?, ?, ?>> {

		public MappingContextNodeXdiInstanceIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, XdiInstance<?, ?, ?, ?>> (contextNodes) {

				@Override
				public XdiInstance<?, ?, ?, ?> map(ContextNode contextNode) {

					return XdiAbstractInstance.fromContextNode(contextNode);
				}
			});
		}
	}
}
