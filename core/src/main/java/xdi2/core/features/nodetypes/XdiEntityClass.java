package xdi2.core.features.nodetypes;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.constants.XDIConstants;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;
import xdi2.core.xri3.XDI3SubSegment;

/**
 * An XDI entity class (context function), represented as a context node.
 * 
 * @author markus
 */
public final class XdiEntityClass extends XdiAbstractClass<XdiEntityClass, XdiEntityInstanceUnordered, XdiEntityInstanceOrdered, XdiEntityInstance> implements XdiClass<XdiEntityClass, XdiEntityInstanceUnordered, XdiEntityInstanceOrdered, XdiEntityInstance> {

	private static final long serialVersionUID = -8518618921427437445L;

	protected XdiEntityClass(ContextNode contextNode) {

		super(contextNode, XdiEntityInstanceUnordered.class, XdiEntityInstanceOrdered.class, XdiEntityInstance.class);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI entity class.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI entity class.
	 */
	public static boolean isValid(ContextNode contextNode) {

		return 
				isValidArcXri(contextNode.getArcXri()) &&
				( ! XdiAttributeClass.isValid(contextNode.getContextNode()) && ! XdiAttributeInstanceUnordered.isValid(contextNode.getContextNode()) && ! XdiAttributeInstanceOrdered.isValid(contextNode.getContextNode()) );
	}

	/**
	 * Factory method that creates an XDI entity class bound to a given context node.
	 * @param contextNode The context node that is an XDI entity class.
	 * @return The XDI entity class.
	 */
	public static XdiEntityClass fromContextNode(ContextNode contextNode) {

		if (! isValid(contextNode)) return null;

		return new XdiEntityClass(contextNode);
	}

	/*
	 * Methods for XRIs
	 */

	public static XDI3SubSegment createArcXri(XDI3SubSegment arcXri) {

		return XDI3SubSegment.create("" + XDIConstants.XS_CLASS.charAt(0) + arcXri + XDIConstants.XS_CLASS.charAt(1));
	}

	public static boolean isValidArcXri(XDI3SubSegment arcXri) {

		if (arcXri == null) return false;

		if (! arcXri.isClassXs()) return false;
		if (arcXri.isAttributeXs()) return false;

		if (XDIConstants.CS_PLUS.equals(arcXri.getCs()) || XDIConstants.CS_DOLLAR.equals(arcXri.getCs())) {

			if (! arcXri.hasLiteral() && ! arcXri.hasXRef()) return false;
		} else if (XDIConstants.CS_EQUALS.equals(arcXri.getCs()) || XDIConstants.CS_AT.equals(arcXri.getCs()) || XDIConstants.CS_STAR.equals(arcXri.getCs()) || XDIConstants.CS_BANG.equals(arcXri.getCs())) {

			if (arcXri.hasLiteral() || arcXri.hasXRef()) return false;
		} else {

			return false;
		}

		return true;
	}

	/*
	 * Helper classes
	 */

	public static class MappingContextNodeXdiEntityClassIterator extends NotNullIterator<XdiEntityClass> {

		public MappingContextNodeXdiEntityClassIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, XdiEntityClass> (contextNodes) {

				@Override
				public XdiEntityClass map(ContextNode contextNode) {

					return XdiEntityClass.fromContextNode(contextNode);
				}
			});
		}
	}
}
