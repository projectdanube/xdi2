package xdi2.core.features.nodetypes;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;

public abstract class XdiAbstractAttribute extends XdiAbstractSubGraph implements XdiAttribute {

	private static final long serialVersionUID = 7648046902369626744L;

	protected XdiAbstractAttribute(ContextNode contextNode) {

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

		return XdiAttributeSingleton.isValid(contextNode) || 
				XdiAttributeMemberUnordered.isValid(contextNode) ||
				XdiAttributeMemberOrdered.isValid(contextNode);
	}

	/**
	 * Factory method that creates an XDI attribute bound to a given context node.
	 * @param contextNode The context node that is an XDI attribute.
	 * @return The XDI attribute.
	 */
	public static XdiAttribute fromContextNode(ContextNode contextNode) {

		XdiAttribute xdiAttribute = null;

		if ((xdiAttribute = XdiAttributeSingleton.fromContextNode(contextNode)) != null) return xdiAttribute;
		if ((xdiAttribute = XdiAttributeMemberUnordered.fromContextNode(contextNode)) != null) return xdiAttribute;
		if ((xdiAttribute = XdiAttributeMemberOrdered.fromContextNode(contextNode)) != null) return xdiAttribute;

		return null;
	}

	/*
	 * Helper classes
	 */

	public static class MappingContextNodeXdiAttributeIterator extends NotNullIterator<XdiAttribute> {

		public MappingContextNodeXdiAttributeIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, XdiAttribute> (contextNodes) {

				@Override
				public XdiAttribute map(ContextNode contextNode) {

					return XdiAbstractAttribute.fromContextNode(contextNode);
				}
			});
		}
	}
}
