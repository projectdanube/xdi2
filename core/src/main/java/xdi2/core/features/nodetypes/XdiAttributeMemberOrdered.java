package xdi2.core.features.nodetypes;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.syntax.XDIArc;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;

/**
 * An XDI ordered attribute members (context function), represented as a context node.
 * 
 * @author markus
 */
public final class XdiAttributeMemberOrdered extends XdiAbstractMemberOrdered<XdiAttributeCollection, XdiAttribute, XdiAttributeCollection, XdiAttributeMemberUnordered, XdiAttributeMemberOrdered, XdiAttributeMember> implements XdiAttributeMember {

	private static final long serialVersionUID = 3562576098019686485L;

	protected XdiAttributeMemberOrdered(ContextNode contextNode) {

		super(contextNode);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI ordered attribute instance.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI ordered attribute instance.
	 */
	public static boolean isValid(ContextNode contextNode) {

		if (contextNode == null) return false;

		return
				isValidarc(contextNode.getArc()) &&
				XdiAttributeCollection.isValid(contextNode.getContextNode());
	}

	/**
	 * Factory method that creates an XDI ordered attribute instance bound to a given context node.
	 * @param contextNode The context node that is an XDI ordered attribute instance.
	 * @return The XDI ordered attribute instance.
	 */
	public static XdiAttributeMemberOrdered fromContextNode(ContextNode contextNode) {

		if (! isValid(contextNode)) return null;

		return new XdiAttributeMemberOrdered(contextNode);
	}

	/*
	 * Methods for XRIs
	 */

	public static boolean isValidarc(XDIArc arc) {

		return XdiAbstractMemberOrdered.isValidarc(arc, true);
	}

	/*
	 * Instance methods
	 */

	/**
	 * Returns the parent XDI collection of this XDI ordered attribute instance.
	 * @return The parent XDI collection.
	 */
	@Override
	public XdiAttributeCollection getXdiCollection() {

		return new XdiAttributeCollection(this.getContextNode().getContextNode());
	}

	/**
	 * Creates or returns an XDI value under this XDI ordered attribute instance.
	 * @param create Whether or not to create the context node if it doesn't exist.
	 * @return The XDI value.
	 */
	@Override
	public XdiValue getXdiValue(boolean create) {

		XDIArc valuearc = XdiValue.createarc();

		ContextNode valueContextNode = create ? this.getContextNode().setContextNode(valuearc) : this.getContextNode().getContextNode(valuearc, false);
		if (valueContextNode == null) return null;

		return new XdiValue(valueContextNode);
	}

	/*
	 * Helper classes
	 */

	public static class MappingContextNodeXdiAttributeMemberOrderedIterator extends NotNullIterator<XdiAttributeMemberOrdered> {

		public MappingContextNodeXdiAttributeMemberOrderedIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, XdiAttributeMemberOrdered> (contextNodes) {

				@Override
				public XdiAttributeMemberOrdered map(ContextNode contextNode) {

					return XdiAttributeMemberOrdered.fromContextNode(contextNode);
				}
			});
		}
	}
}
