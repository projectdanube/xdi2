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

		if (contextNode == null) throw new NullPointerException();

		if (contextNode.getXDIArc() == null || ! isValidXDIArc(contextNode.getXDIArc())) return false;
		if (contextNode.getContextNode() == null || ! XdiAttributeCollection.isValid(contextNode.getContextNode())) return false;

		return true;
	}

	/**
	 * Factory method that creates an XDI unordered attribute member bound to a given context node.
	 * @param contextNode The context node that is an XDI unordered attribute member.
	 * @return The XDI unordered attribute member.
	 */
	public static XdiAttributeMemberUnordered fromContextNode(ContextNode contextNode) {

		if (contextNode == null) throw new NullPointerException();

		if (! isValid(contextNode)) return null;

		return new XdiAttributeMemberUnordered(contextNode);
	}

	/*
	 * Methods for arcs
	 */

	public static boolean isValidXDIArc(XDIArc XDIarc) {

		if (XDIarc == null) throw new NullPointerException();

		if (! XdiAbstractMemberUnordered.isValidXDIArc(XDIarc, XdiAttributeCollection.class)) return false;

		return true;
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
