package xdi2.core.features.nodetypes;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;
import xdi2.core.xri3.XDI3SubSegment;

/**
 * An XDI unordered variable member, represented as a context node.
 * 
 * @author markus
 */
public final class XdiVariableMemberUnordered extends XdiAbstractMemberUnordered<XdiVariableCollection, XdiVariable, XdiVariableCollection, XdiVariableMemberUnordered, XdiVariableMemberOrdered, XdiVariableMember> implements XdiVariableMember {

	private static final long serialVersionUID = 1027868266675630350L;

	protected XdiVariableMemberUnordered(ContextNode contextNode) {

		super(contextNode);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI unordered variable member.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI unordered variable member.
	 */
	public static boolean isValid(ContextNode contextNode) {

		if (contextNode == null) return false;

		return
				isValidArcXri(contextNode.getArcXri()) &&
				XdiVariableCollection.isValid(contextNode.getContextNode());
	}

	/**
	 * Factory method that creates an XDI unordered variable member bound to a given context node.
	 * @param contextNode The context node that is an XDI unordered variable member.
	 * @return The XDI unordered variable member.
	 */
	public static XdiVariableMemberUnordered fromContextNode(ContextNode contextNode) {

		if (! isValid(contextNode)) return null;

		return new XdiVariableMemberUnordered(contextNode);
	}

	/*
	 * Methods for XRIs
	 */

	public static boolean isValidArcXri(XDI3SubSegment arcXri) {

		return XdiAbstractMemberUnordered.isValidArcXri(arcXri, XdiVariableMemberUnordered.class);
	}

	/*
	 * Instance methods
	 */

	/**
	 * Returns the parent XDI collection of this XDI unordered variable member.
	 * @return The parent XDI collection.
	 */
	@Override
	public XdiVariableCollection getXdiCollection() {

		return new XdiVariableCollection(this.getContextNode().getContextNode());
	}

	/*
	 * Helper classes
	 */

	public static class MappingContextNodeXdiVariableMemberUnorderedIterator extends NotNullIterator<XdiVariableMemberUnordered> {

		public MappingContextNodeXdiVariableMemberUnorderedIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, XdiVariableMemberUnordered> (contextNodes) {

				@Override
				public XdiVariableMemberUnordered map(ContextNode contextNode) {

					return XdiVariableMemberUnordered.fromContextNode(contextNode);
				}
			});
		}
	}
}
