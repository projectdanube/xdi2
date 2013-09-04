package xdi2.core.features.nodetypes;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;
import xdi2.core.xri3.XDI3SubSegment;

/**
 * An XDI ordered attribute instance (context function), represented as a context node.
 * 
 * @author markus
 */
public final class XdiAttributeInstanceOrdered extends XdiAbstractInstanceOrdered<XdiAttributeClass, XdiAttributeInstanceUnordered, XdiAttributeInstanceOrdered, XdiAttributeInstance> implements XdiAttributeInstance {

	private static final long serialVersionUID = 3562576098019686485L;

	protected XdiAttributeInstanceOrdered(ContextNode contextNode) {

		super(contextNode);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI ordered attribute instance.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI ordered attribute instance.
	 */
	public static boolean isValid(ContextNode contextNode) {

		return
				isValidArcXri(contextNode.getArcXri()) &&
				( XdiAttributeClass.isValid(contextNode.getContextNode()) || XdiAttributeInstanceOrdered.isValid(contextNode.getContextNode()) );
	}

	/**
	 * Factory method that creates an XDI ordered attribute instance bound to a given context node.
	 * @param contextNode The context node that is an XDI ordered attribute instance.
	 * @return The XDI ordered attribute instance.
	 */
	public static XdiAttributeInstanceOrdered fromContextNode(ContextNode contextNode) {

		if (! isValid(contextNode)) return null;

		return new XdiAttributeInstanceOrdered(contextNode);
	}

	/*
	 * Methods for XRIs
	 */

	public static boolean isValidArcXri(XDI3SubSegment arcXri) {

		return XdiAbstractInstanceOrdered.isValidArcXri(arcXri, true);
	}

	/*
	 * Instance methods
	 */

	/**
	 * Gets or returns the parent XDI class of this XDI ordered attribute instance.
	 * @return The parent XDI class.
	 */
	@Override
	public XdiAttributeClass getXdiClass() {

		return new XdiAttributeClass(this.getContextNode().getContextNode());
	}

	/**
	 * Creates or returns an XDI value under this XDI ordered attribute instance.
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

	public static class MappingContextNodeXdiAttributeInstanceOrderedIterator extends NotNullIterator<XdiAttributeInstanceOrdered> {

		public MappingContextNodeXdiAttributeInstanceOrderedIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, XdiAttributeInstanceOrdered> (contextNodes) {

				@Override
				public XdiAttributeInstanceOrdered map(ContextNode contextNode) {

					return XdiAttributeInstanceOrdered.fromContextNode(contextNode);
				}
			});
		}
	}
}
