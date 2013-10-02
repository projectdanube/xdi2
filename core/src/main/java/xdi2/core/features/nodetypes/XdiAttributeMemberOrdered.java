package xdi2.core.features.nodetypes;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;
import xdi2.core.xri3.XDI3SubSegment;

/**
 * An XDI ordered attribute members (context function), represented as a context node.
 * 
 * @author markus
 */
public final class XdiAttributeMemberOrdered extends XdiAbstractMemberOrdered<XdiAttribute, XdiAttributeCollection, XdiAttributeMemberUnordered, XdiAttributeMemberOrdered, XdiAttributeMember> implements XdiAttributeMember {

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
				isValidArcXri(contextNode.getArcXri()) &&
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

	public static boolean isValidArcXri(XDI3SubSegment arcXri) {

		return XdiAbstractMemberOrdered.isValidArcXri(arcXri, true);
	}

	/*
	 * Instance methods
	 */

	/**
	 * Gets or returns the parent XDI class of this XDI ordered attribute instance.
	 * @return The parent XDI class.
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

		XDI3SubSegment valueArcXri = XdiValue.createArcXri();

		ContextNode valueContextNode = create ? this.getContextNode().setContextNode(valueArcXri) : this.getContextNode().getContextNode(valueArcXri);
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
