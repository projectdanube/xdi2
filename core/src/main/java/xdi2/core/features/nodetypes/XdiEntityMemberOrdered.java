package xdi2.core.features.nodetypes;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.syntax.XDIArc;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;

/**
 * An XDI ordered entity member, represented as a context node.
 * 
 * @author markus
 */
public final class XdiEntityMemberOrdered extends XdiAbstractMemberOrdered<XdiEntityCollection, XdiEntity, XdiEntityCollection, XdiEntityMemberUnordered, XdiEntityMemberOrdered, XdiEntityMember> implements XdiEntityMember {

	private static final long serialVersionUID = 1027868266675630350L;

	protected XdiEntityMemberOrdered(ContextNode contextNode) {

		super(contextNode);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI ordered entity member.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI ordered entity member.
	 */
	public static boolean isValid(ContextNode contextNode) {

		if (contextNode == null) return false;

		return
				isValidXDIArc(contextNode.getXDIArc()) &&
				XdiEntityCollection.isValid(contextNode.getContextNode());
	}

	/**
	 * Factory method that creates an XDI ordered entity member bound to a given context node.
	 * @param contextNode The context node that is an XDI ordered entity member.
	 * @return The XDI ordered entity member.
	 */
	public static XdiEntityMemberOrdered fromContextNode(ContextNode contextNode) {

		if (! isValid(contextNode)) return null;

		return new XdiEntityMemberOrdered(contextNode);
	}

	/*
	 * Methods for XRIs
	 */

	public static boolean isValidXDIArc(XDIArc arc) {

		return XdiAbstractMemberOrdered.isValidXDIArc(arc, XdiEntityCollection.class);
	}

	/*
	 * Instance methods
	 */

	/**
	 * Returns the parent XDI collection of this XDI ordered entity member.
	 * @return The parent XDI collection.
	 */
	@Override
	public XdiEntityCollection getXdiCollection() {

		return new XdiEntityCollection(this.getContextNode().getContextNode());
	}

	/*
	 * Helper classes
	 */

	public static class MappingContextNodeXdiEntityMemberOrderedIterator extends NotNullIterator<XdiEntityMemberOrdered> {

		public MappingContextNodeXdiEntityMemberOrderedIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, XdiEntityMemberOrdered> (contextNodes) {

				@Override
				public XdiEntityMemberOrdered map(ContextNode contextNode) {

					return XdiEntityMemberOrdered.fromContextNode(contextNode);
				}
			});
		}
	}
}
