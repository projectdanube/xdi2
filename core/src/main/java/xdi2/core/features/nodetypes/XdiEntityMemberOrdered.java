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
public class XdiEntityMemberOrdered extends XdiAbstractMemberOrdered<XdiEntityCollection, XdiEntity, XdiEntityCollection, XdiEntityMemberUnordered, XdiEntityMemberOrdered, XdiEntityMember> implements XdiEntityMember {

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

		if (contextNode == null) throw new NullPointerException();

		if (contextNode.getXDIArc() == null || ! isValidXDIArc(contextNode.getXDIArc())) return false;
		if (contextNode.getContextNode() == null || ! XdiEntityCollection.isValid(contextNode.getContextNode())) return false;

		return true;
	}

	/**
	 * Factory method that creates an XDI ordered entity member bound to a given context node.
	 * @param contextNode The context node that is an XDI ordered entity member.
	 * @return The XDI ordered entity member.
	 */
	public static XdiEntityMemberOrdered fromContextNode(ContextNode contextNode) {

		if (contextNode == null) throw new NullPointerException();

		if (! isValid(contextNode)) return null;

		return contextNode.getXDIArc().isVariable() ? new Variable(contextNode) : new XdiEntityMemberOrdered(contextNode);
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
	 * Methods for arcs
	 */

	public static boolean isValidXDIArc(XDIArc XDIarc) {

		if (XDIarc == null) throw new NullPointerException();

		if (! XdiAbstractMemberOrdered.isValidXDIArc(XDIarc, XdiEntityCollection.class)) return false;

		return true;
	}

	/*
	 * Variable class
	 */

	public static class Variable extends XdiEntityMemberOrdered implements XdiVariable<XdiEntity> {

		private static final long serialVersionUID = 3754055146796087109L;

		private Variable(ContextNode contextNode) {

			super(contextNode);
		}

		public static boolean isValid(ContextNode contextNode) {

			return contextNode.getXDIArc().isVariable() && XdiEntityMemberOrdered.isValid(contextNode);
		}

		public static Variable fromContextNode(ContextNode contextNode) {

			if (contextNode == null) throw new NullPointerException();

			if (! isValid(contextNode)) return null;

			return new Variable(contextNode);
		}
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
