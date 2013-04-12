package xdi2.core.features.nodetypes;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;
import xdi2.core.xri3.XDI3SubSegment;

/**
 * An XDI attribute instance (context function), represented as a context node.
 * 
 * @author markus
 */
public final class XdiAttributeInstance extends XdiAbstractInstance implements XdiAttribute {

	private static final long serialVersionUID = 1027868266675630350L;

	protected XdiAttributeInstance(ContextNode contextNode) {

		super(contextNode);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI attribute instance.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI attribute instance.
	 */
	public static boolean isValid(ContextNode contextNode) {

		return
				isValidArcXri(contextNode.getArcXri()) &&
				XdiAttributeClass.isValid(contextNode.getContextNode());
	}

	/**
	 * Factory method that creates an XDI attribute instance bound to a given context node.
	 * @param contextNode The context node that is an XDI attribute instance.
	 * @return The XDI attribute instance.
	 */
	public static XdiAttributeInstance fromContextNode(ContextNode contextNode) {

		if (! isValid(contextNode)) return null;

		return new XdiAttributeInstance(contextNode);
	}

	/*
	 * Instance methods
	 */

	/**
	 * Gets or returns the parent XDI class of this XDI attribute instance.
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

		ContextNode valueContextNode = this.getContextNode().getContextNode(valueArcXri);
		if (valueContextNode == null && create) valueContextNode = this.getContextNode().createContextNode(valueArcXri);
		if (valueContextNode == null) return null;

		return new XdiValue(valueContextNode);
	}

	/*
	 * Helper classes
	 */

	public static class MappingContextNodeXdiAttributeInstanceIterator extends NotNullIterator<XdiAttributeInstance> {

		public MappingContextNodeXdiAttributeInstanceIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, XdiAttributeInstance> (contextNodes) {

				@Override
				public XdiAttributeInstance map(ContextNode contextNode) {

					return XdiAttributeInstance.fromContextNode(contextNode);
				}
			});
		}
	}
}
