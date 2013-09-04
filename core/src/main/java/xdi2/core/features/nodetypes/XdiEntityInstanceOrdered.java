package xdi2.core.features.nodetypes;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;
import xdi2.core.xri3.XDI3SubSegment;

/**
 * An XDI ordered entity instance (context function), represented as a context node.
 * 
 * @author markus
 */
public final class XdiEntityInstanceOrdered extends XdiAbstractInstanceOrdered<XdiEntityClass, XdiEntityInstanceUnordered, XdiEntityInstanceOrdered, XdiEntityInstance> implements XdiEntityInstance {

	private static final long serialVersionUID = 1027868266675630350L;

	protected XdiEntityInstanceOrdered(ContextNode contextNode) {

		super(contextNode);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI ordered entity instance.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI ordered entity instance.
	 */
	public static boolean isValid(ContextNode contextNode) {

		return
				isValidArcXri(contextNode.getArcXri()) &&
				( XdiEntityClass.isValid(contextNode.getContextNode()) || XdiEntityInstanceOrdered.isValid(contextNode.getContextNode()) );
	}

	/**
	 * Factory method that creates an XDI ordered entity instance bound to a given context node.
	 * @param contextNode The context node that is an XDI ordered entity instance.
	 * @return The XDI ordered entity instance.
	 */
	public static XdiEntityInstanceOrdered fromContextNode(ContextNode contextNode) {

		if (! isValid(contextNode)) return null;

		return new XdiEntityInstanceOrdered(contextNode);
	}

	/*
	 * Methods for XRIs
	 */

	public static boolean isValidArcXri(XDI3SubSegment arcXri) {

		return XdiAbstractInstanceOrdered.isValidArcXri(arcXri, false);
	}

	/*
	 * Instance methods
	 */

	/**
	 * Gets or returns the parent XDI class of this XDI ordered entity instance.
	 * @return The parent XDI class.
	 */
	@Override
	public XdiEntityClass getXdiClass() {

		return new XdiEntityClass(this.getContextNode().getContextNode());
	}

	/*
	 * Helper classes
	 */

	public static class MappingContextNodeXdiEntityInstanceOrderedIterator extends NotNullIterator<XdiEntityInstanceOrdered> {

		public MappingContextNodeXdiEntityInstanceOrderedIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, XdiEntityInstanceOrdered> (contextNodes) {

				@Override
				public XdiEntityInstanceOrdered map(ContextNode contextNode) {

					return XdiEntityInstanceOrdered.fromContextNode(contextNode);
				}
			});
		}
	}
}
