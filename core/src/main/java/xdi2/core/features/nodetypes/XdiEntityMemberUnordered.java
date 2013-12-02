package xdi2.core.features.nodetypes;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3SubSegment;

/**
 * An XDI unordered entity member (context function), represented as a context node.
 * 
 * @author markus
 */
public final class XdiEntityMemberUnordered extends XdiAbstractMemberUnordered<XdiEntityCollection, XdiEntity, XdiEntityCollection, XdiEntityMemberUnordered, XdiEntityMemberOrdered, XdiEntityMember> implements XdiEntityMember {

	private static final long serialVersionUID = 1027868266675630350L;

	protected XdiEntityMemberUnordered(ContextNode contextNode) {

		super(contextNode);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI unordered entity instance.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI unordered entity instance.
	 */
	public static boolean isValid(ContextNode contextNode) {

		if (contextNode == null) return false;

		return
				isValidArcXri(contextNode.getArcXri(), false) &&
				XdiEntityCollection.isValid(contextNode.getContextNode());
	}

	/**
	 * Factory method that creates an XDI unordered entity instance bound to a given context node.
	 * @param contextNode The context node that is an XDI unordered entity instance.
	 * @return The XDI unordered entity instance.
	 */
	public static XdiEntityMemberUnordered fromContextNode(ContextNode contextNode) {

		if (! isValid(contextNode)) return null;

		return new XdiEntityMemberUnordered(contextNode);
	}

	/*
	 * Methods for XRIs
	 */

	public static boolean isValidArcXri(XDI3SubSegment arcXri) {

		return XdiAbstractMemberUnordered.isValidArcXri(arcXri, false);
	}

	/*
	 * Instance methods
	 */

	/**
	 * Returns the parent XDI collection of this XDI unordered entity instance.
	 * @return The parent XDI collection.
	 */
	@Override
	public XdiEntityCollection getXdiCollection() {

		return new XdiEntityCollection(this.getContextNode().getContextNode());
	}

	/**
	 * Returns an XDI inner root based on this XDI entity.
	 * @return The XDI inner root.
	 */
	@Override
	public XdiInnerRoot getXdiInnerRoot(XDI3Segment innerRootPredicateXri, boolean create) {

		return XdiAbstractEntity.getXdiInnerRoot(this, innerRootPredicateXri, create);
	}

	/*
	 * Helper classes
	 */

	public static class MappingContextNodeXdiEntityMemberUnorderedIterator extends NotNullIterator<XdiEntityMemberUnordered> {

		public MappingContextNodeXdiEntityMemberUnorderedIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, XdiEntityMemberUnordered> (contextNodes) {

				@Override
				public XdiEntityMemberUnordered map(ContextNode contextNode) {

					return XdiEntityMemberUnordered.fromContextNode(contextNode);
				}
			});
		}
	}
}
