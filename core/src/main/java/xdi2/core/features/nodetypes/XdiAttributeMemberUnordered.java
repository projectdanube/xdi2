package xdi2.core.features.nodetypes;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.syntax.XDIArc;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;

/**
 * An XDI unordered attribute member, represented as a context node.
 * 
 * @author markus
 */
public final class XdiAttributeMemberUnordered extends XdiAbstractMemberUnordered<XdiAttributeCollection, XdiAttribute, XdiAttributeCollection, XdiAttributeMemberUnordered, XdiAttributeMemberOrdered, XdiAttributeMember> implements XdiAttributeMember {

	private static final long serialVersionUID = 1027868266675630350L;

	protected XdiAttributeMemberUnordered(ContextNode contextNode) {

		super(contextNode);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI unordered attribute member.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI unordered attribute member.
	 */
	public static boolean isValid(ContextNode contextNode) {

		if (contextNode == null) return false;

		return
				isValidXDIArc(contextNode.getXDIArc()) &&
				XdiAttributeCollection.isValid(contextNode.getContextNode());
	}

	/**
	 * Factory method that creates an XDI unordered attribute member bound to a given context node.
	 * @param contextNode The context node that is an XDI unordered attribute member.
	 * @return The XDI unordered attribute member.
	 */
	public static XdiAttributeMemberUnordered fromContextNode(ContextNode contextNode) {

		if (! isValid(contextNode)) return null;

		return new XdiAttributeMemberUnordered(contextNode);
	}

	/*
	 * Methods for XRIs
	 */

	public static boolean isValidXDIArc(XDIArc arc) {

		return XdiAbstractMemberUnordered.isValidXDIArc(arc, XdiAttributeCollection.class);
	}

	/*
	 * Instance methods
	 */

	/**
	 * Returns the parent XDI collection of this XDI unordered attribute member.
	 * @return The parent XDI collection.
	 */
	@Override
	public XdiAttributeCollection getXdiCollection() {

		return new XdiAttributeCollection(this.getContextNode().getContextNode());
	}

	/**
	 * Creates or returns an XDI value under this XDI attribute member.
	 * @param create Whether or not to create the context node if it doesn't exist.
	 * @return The XDI value.
	 */
	@Override
	public XdiValue getXdiValue(boolean create) {

		XDIArc valuearc = XdiValue.createXDIArc();

		ContextNode valueContextNode = create ? this.getContextNode().setContextNode(valuearc) : this.getContextNode().getContextNode(valuearc, false);
		if (valueContextNode == null) return null;

		return new XdiValue(valueContextNode);
	}

	/*
	 * Helper classes
	 */

	public static class MappingContextNodeXdiAttributeMemberUnorderedIterator extends NotNullIterator<XdiAttributeMemberUnordered> {

		public MappingContextNodeXdiAttributeMemberUnorderedIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, XdiAttributeMemberUnordered> (contextNodes) {

				@Override
				public XdiAttributeMemberUnordered map(ContextNode contextNode) {

					return XdiAttributeMemberUnordered.fromContextNode(contextNode);
				}
			});
		}
	}
}
