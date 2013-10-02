package xdi2.core.features.nodetypes;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.constants.XDIConstants;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;
import xdi2.core.xri3.XDI3SubSegment;

/**
 * An XDI value (context function), represented as a context node.
 * 
 * @author markus
 */
public final class XdiValue extends XdiAbstractSubGraph<XdiValue> {

	private static final long serialVersionUID = 3710989824639753381L;

	protected XdiValue(ContextNode contextNode) {

		super(contextNode);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI value.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI value.
	 */
	public static boolean isValid(ContextNode contextNode) {

		if (contextNode == null) return false;

		return isValidArcXri(contextNode.getArcXri()) &&
				XdiAbstractAttribute.isValid(contextNode.getContextNode());
	}

	/**
	 * Factory method that creates an XDI value bound to a given context node.
	 * @param contextNode The context node that is an XDI value.
	 * @return The XDI value.
	 */
	public static XdiValue fromContextNode(ContextNode contextNode) {

		if (! isValid(contextNode)) return null;

		return new XdiValue(contextNode);
	}

	/*
	 * Methods for XRIs
	 */

	public static XDI3SubSegment createArcXri() {

		return XDIConstants.XRI_SS_LITERAL;
	}

	public static boolean isValidArcXri(XDI3SubSegment arcXri) {

		if (arcXri == null) return false;

		if (arcXri.isClassXs()) return false;
		if (arcXri.isAttributeXs()) return false;
		if (arcXri.hasLiteral()) return false;
		if (arcXri.hasXRef()) return false;

		if (! XDIConstants.CS_VALUE.equals(arcXri.getCs())) return false;

		return true;
	}

	/*
	 * Helper classes
	 */

	public static class MappingContextNodeXdiValueIterator extends NotNullIterator<XdiValue> {

		public MappingContextNodeXdiValueIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, XdiValue> (contextNodes) {

				@Override
				public XdiValue map(ContextNode contextNode) {

					return XdiValue.fromContextNode(contextNode);
				}
			});
		}
	}
}
