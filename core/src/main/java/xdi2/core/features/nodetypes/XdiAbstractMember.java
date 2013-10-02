package xdi2.core.features.nodetypes;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;

public abstract class XdiAbstractMember<EQ extends XdiSubGraph<EQ>, C extends XdiCollection<EQ, C, U, O, I>, U extends XdiMemberUnordered<EQ, C, U, O, I>, O extends XdiMemberOrdered<EQ, C, U, O, I>, I extends XdiMember<EQ, C, U, O, I>> extends XdiAbstractSubGraph<EQ> implements XdiMember<EQ, C, U, O, I> {

	private static final long serialVersionUID = 3673396905245169194L;

	protected XdiAbstractMember(ContextNode contextNode) {

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

		if (contextNode == null) return false;

		return XdiAbstractMemberUnordered.isValid(contextNode) ||
				XdiAbstractMemberOrdered.isValid(contextNode);
	}

	/**
	 * Factory method that creates an XDI instance bound to a given context node.
	 * @param contextNode The context node that is an XDI instance.
	 * @return The XDI instance.
	 */
	public static XdiMember<?, ?, ?, ?, ?> fromContextNode(ContextNode contextNode) {

		XdiMember<?, ?, ?, ?, ?> xdiMember = null;

		if ((xdiMember = XdiAbstractMemberUnordered.fromContextNode(contextNode)) != null) return xdiMember;
		if ((xdiMember = XdiAbstractMemberOrdered.fromContextNode(contextNode)) != null) return xdiMember;

		return null;
	}

	/*
	 * Helper classes
	 */

	public static class MappingContextNodeXdiMemberIterator extends NotNullIterator<XdiMember<?, ?, ?, ?, ?>> {

		public MappingContextNodeXdiMemberIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, XdiMember<?, ?, ?, ?, ?>> (contextNodes) {

				@Override
				public XdiMember<?, ?, ?, ?, ?> map(ContextNode contextNode) {

					return XdiAbstractMember.fromContextNode(contextNode);
				}
			});
		}
	}
}
