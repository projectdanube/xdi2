package xdi2.core.features.nodetypes;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;
import xdi2.core.xri3.XDI3SubSegment;

/**
 * An XDI unordered attribute instance (context function), represented as a context node.
 * 
 * @author markus
 */
public final class XdiAttributeInstanceUnordered extends XdiAbstractInstanceUnordered<XdiAttributeClass, XdiAttributeInstanceUnordered, XdiAttributeInstanceOrdered, XdiAttributeInstance> implements XdiAttributeInstance {

	private static final long serialVersionUID = 1027868266675630350L;

	protected XdiAttributeInstanceUnordered(ContextNode contextNode) {

		super(contextNode);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI unordered attribute instance.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI unordered attribute instance.
	 */
	public static boolean isValid(ContextNode contextNode) {

		return
				isValidArcXri(contextNode.getArcXri(), true) &&
				( XdiAttributeClass.isValid(contextNode.getContextNode()) || XdiAttributeInstanceUnordered.isValid(contextNode.getContextNode()) );
	}

	/**
	 * Factory method that creates an XDI unordered attribute instance bound to a given context node.
	 * @param contextNode The context node that is an XDI unordered attribute instance.
	 * @return The XDI unordered attribute instance.
	 */
	public static XdiAttributeInstanceUnordered fromContextNode(ContextNode contextNode) {

		if (! isValid(contextNode)) return null;

		return new XdiAttributeInstanceUnordered(contextNode);
	}

	/*
	 * Methods for XRIs
	 */

	public static boolean isValidArcXri(XDI3SubSegment arcXri) {

		return XdiAbstractInstanceUnordered.isValidArcXri(arcXri, true);
	}

	/*
	 * Instance methods
	 */

	/**
	 * Gets or returns the parent XDI class of this XDI unordered attribute instance.
	 * @return The parent XDI class.
	 */
	@Override
	public XdiAttributeClass getXdiClass() {

		return new XdiAttributeClass(this.getContextNode().getContextNode());
	}

	/**
	 * Creates or returns an XDI value under this XDI attribute element.
	 * @param create Whether or not to create the context node if it doesn't exist.
	 * @return The XDI value.
	 */
	@Override
	public XdiValue getXdiValue(boolean create) {

		XDI3SubSegment valueArcXri = XdiValue.createArcXri();

		ContextNode valueContextNode = create ? this.getContextNode().setContextNode(valueArcXri) : this.getContextNode().getContextNode(valueArcXri);
		if (valueContextNode == null) return null;

		return new XdiValue(valueContextNode);
	}

	/*
	 * Helper classes
	 */

	public static class MappingContextNodeXdiAttributeInstanceUnorderedIterator extends NotNullIterator<XdiAttributeInstanceUnordered> {

		public MappingContextNodeXdiAttributeInstanceUnorderedIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, XdiAttributeInstanceUnordered> (contextNodes) {

				@Override
				public XdiAttributeInstanceUnordered map(ContextNode contextNode) {

					return XdiAttributeInstanceUnordered.fromContextNode(contextNode);
				}
			});
		}
	}
}
