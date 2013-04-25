package xdi2.core.features.nodetypes;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.constants.XDIConstants;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;
import xdi2.core.xri3.XDI3SubSegment;

/**
 * An XDI attribute class (context function), represented as a context node.
 * 
 * @author markus
 */
public final class XdiAttributeClass extends XdiAbstractClass<XdiAttributeInstanceUnordered, XdiAttributeInstanceOrdered, XdiAttributeInstance> {

	private static final long serialVersionUID = -8518618921427437445L;

	protected XdiAttributeClass(ContextNode contextNode) {

		super(contextNode, XdiAttributeInstanceUnordered.class, XdiAttributeInstanceOrdered.class, XdiAttributeInstance.class);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI attribute class.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI attribute class.
	 */
	public static boolean isValid(ContextNode contextNode) {

		return isValidArcXri(contextNode.getArcXri());
	}

	/**
	 * Factory method that creates an XDI attribute class bound to a given context node.
	 * @param contextNode The context node that is an XDI attribute class.
	 * @return The XDI attribute class.
	 */
	public static XdiAttributeClass fromContextNode(ContextNode contextNode) {

		if (! isValid(contextNode)) return null;

		return new XdiAttributeClass(contextNode);
	}

	/*
	 * Methods for XRIs
	 */

	public static XDI3SubSegment createArcXri(XDI3SubSegment arcXri) {

		return XDI3SubSegment.create("" + XDIConstants.XS_CLASS.charAt(0) + XDIConstants.XS_ATTRIBUTE.charAt(0) + arcXri + XDIConstants.XS_ATTRIBUTE.charAt(1) + XDIConstants.XS_CLASS.charAt(1));
	}

	public static boolean isValidArcXri(XDI3SubSegment arcXri) {

		if (arcXri == null) return false;

		if (! arcXri.isClassXs()) return false;
		if (! arcXri.isAttributeXs()) return false;

		if (XDIConstants.CS_PLUS.equals(arcXri.getCs()) || XDIConstants.CS_DOLLAR.equals(arcXri.getCs())) {

			if (! arcXri.hasLiteral() && ! arcXri.hasXRef()) return false;
		} else {

			return false;
		}

		return true;
	}

	/*
	 * Helper classes
	 */

	public static class MappingContextNodeXdiAttributeClassIterator extends NotNullIterator<XdiAttributeClass> {

		public MappingContextNodeXdiAttributeClassIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, XdiAttributeClass> (contextNodes) {

				@Override
				public XdiAttributeClass map(ContextNode contextNode) {

					return XdiAttributeClass.fromContextNode(contextNode);
				}
			});
		}
	}
}
