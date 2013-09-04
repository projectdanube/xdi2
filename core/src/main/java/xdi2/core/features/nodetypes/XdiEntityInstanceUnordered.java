package xdi2.core.features.nodetypes;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;
import xdi2.core.xri3.XDI3SubSegment;

/**
 * An XDI unordered entity instance (context function), represented as a context node.
 * 
 * @author markus
 */
public final class XdiEntityInstanceUnordered extends XdiAbstractInstanceUnordered<XdiEntityClass, XdiEntityInstanceUnordered, XdiEntityInstanceOrdered, XdiEntityInstance> implements XdiEntityInstance {

	private static final long serialVersionUID = 1027868266675630350L;

	protected XdiEntityInstanceUnordered(ContextNode contextNode) {

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

		return
				isValidArcXri(contextNode.getArcXri(), false) &&
				( XdiEntityClass.isValid(contextNode.getContextNode()) || XdiEntityInstanceUnordered.isValid(contextNode.getContextNode()) );
	}

	/**
	 * Factory method that creates an XDI unordered entity instance bound to a given context node.
	 * @param contextNode The context node that is an XDI unordered entity instance.
	 * @return The XDI unordered entity instance.
	 */
	public static XdiEntityInstanceUnordered fromContextNode(ContextNode contextNode) {

		if (! isValid(contextNode)) return null;

		return new XdiEntityInstanceUnordered(contextNode);
	}

	/*
	 * Methods for XRIs
	 */

	public static boolean isValidArcXri(XDI3SubSegment arcXri) {

		return XdiAbstractInstanceUnordered.isValidArcXri(arcXri, false);
	}

	/*
	 * Instance methods
	 */

	/**
	 * Gets or returns the parent XDI class of this XDI unordered entity instance.
	 * @return The parent XDI class.
	 */
	@Override
	public XdiEntityClass getXdiClass() {

		return new XdiEntityClass(this.getContextNode().getContextNode());
	}

	/*
	 * Helper classes
	 */

	public static class MappingContextNodeXdiEntityInstanceUnorderedIterator extends NotNullIterator<XdiEntityInstanceUnordered> {

		public MappingContextNodeXdiEntityInstanceUnorderedIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, XdiEntityInstanceUnordered> (contextNodes) {

				@Override
				public XdiEntityInstanceUnordered map(ContextNode contextNode) {

					return XdiEntityInstanceUnordered.fromContextNode(contextNode);
				}
			});
		}
	}
}
