package xdi2.core.features.contextfunctions;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;
import xdi2.core.xri3.XDI3SubSegment;

/**
 * An XDI entity instance (context function), represented as a context node.
 * 
 * @author markus
 */
public final class XdiEntityElement extends XdiAbstractElement implements XdiEntity {

	private static final long serialVersionUID = 1027868266675630350L;

	protected XdiEntityElement(ContextNode contextNode) {

		super(contextNode);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI entity instance.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI entity instance.
	 */
	public static boolean isValid(ContextNode contextNode) {

		return
				isValidArcXri(contextNode.getArcXri()) &&
				XdiEntityClass.isValid(contextNode.getContextNode());
	}

	/**
	 * Factory method that creates an XDI entity instance bound to a given context node.
	 * @param contextNode The context node that is an XDI entity instance.
	 * @return The XDI entity instance.
	 */
	public static XdiEntityElement fromContextNode(ContextNode contextNode) {

		if (! isValid(contextNode)) return null;

		return new XdiEntityElement(contextNode);
	}

	/*
	 * Methods for XRIs
	 */

	public static boolean isValidArcXri(XDI3SubSegment arcXri) {

		return XdiAbstractElement.isValidArcXri(arcXri);
	}

	/*
	 * Instance methods
	 */

	/**
	 * Gets or returns the parent XDI class of this XDI entity instance.
	 * @return The parent XDI class.
	 */
	@Override
	public XdiEntityClass getXdiClass() {

		return new XdiEntityClass(this.getContextNode().getContextNode());
	}

	/*
	 * Helper classes
	 */

	public static class MappingContextNodeXdiEntityInstanceIterator extends NotNullIterator<XdiEntityElement> {

		public MappingContextNodeXdiEntityInstanceIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, XdiEntityElement> (contextNodes) {

				@Override
				public XdiEntityElement map(ContextNode contextNode) {

					return XdiEntityElement.fromContextNode(contextNode);
				}
			});
		}
	}
}
