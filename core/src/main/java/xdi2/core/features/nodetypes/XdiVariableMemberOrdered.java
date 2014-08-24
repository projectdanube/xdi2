package xdi2.core.features.nodetypes;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.syntax.XDIArc;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;

/**
 * An XDI ordered variable member, represented as a context node.
 * 
 * @author markus
 */
public final class XdiVariableMemberOrdered extends XdiAbstractMemberOrdered<XdiVariableCollection, XdiVariable, XdiVariableCollection, XdiVariableMemberUnordered, XdiVariableMemberOrdered, XdiVariableMember> implements XdiVariableMember {

	private static final long serialVersionUID = 1027868266675630350L;

	protected XdiVariableMemberOrdered(ContextNode contextNode) {

		super(contextNode);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI ordered variable member.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI ordered variable member.
	 */
	public static boolean isValid(ContextNode contextNode) {

		if (contextNode == null) return false;

		return
				isValidXDIArc(contextNode.getXDIArc()) &&
				XdiVariableCollection.isValid(contextNode.getContextNode());
	}

	/**
	 * Factory method that creates an XDI ordered variable member bound to a given context node.
	 * @param contextNode The context node that is an XDI ordered variable member.
	 * @return The XDI ordered variable member.
	 */
	public static XdiVariableMemberOrdered fromContextNode(ContextNode contextNode) {

		if (! isValid(contextNode)) return null;

		return new XdiVariableMemberOrdered(contextNode);
	}

	/*
	 * Methods for XRIs
	 */

	public static boolean isValidXDIArc(XDIArc arcXri) {

		return XdiAbstractMemberOrdered.isValidXDIArc(arcXri, XdiVariableCollection.class);
	}

	/*
	 * Instance methods
	 */

	/**
	 * Returns the parent XDI collection of this XDI ordered variable member.
	 * @return The parent XDI collection.
	 */
	@Override
	public XdiVariableCollection getXdiCollection() {

		return new XdiVariableCollection(this.getContextNode().getContextNode());
	}

	/*
	 * Helper classes
	 */

	public static class MappingContextNodeXdiVariableMemberOrderedIterator extends NotNullIterator<XdiVariableMemberOrdered> {

		public MappingContextNodeXdiVariableMemberOrderedIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, XdiVariableMemberOrdered> (contextNodes) {

				@Override
				public XdiVariableMemberOrdered map(ContextNode contextNode) {

					return XdiVariableMemberOrdered.fromContextNode(contextNode);
				}
			});
		}
	}
}
