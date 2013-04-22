package xdi2.core.features.nodetypes;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;
import xdi2.core.xri3.XDI3Constants;
import xdi2.core.xri3.XDI3SubSegment;

public abstract class XdiAbstractInstanceOrdered extends XdiAbstractInstance implements XdiInstanceOrdered {

	private static final long serialVersionUID = 8283064321616435273L;

	protected XdiAbstractInstanceOrdered(ContextNode contextNode) {

		super(contextNode);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI ordered instance.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI ordered instance.
	 */
	public static boolean isValid(ContextNode contextNode) {

		return XdiEntityInstanceOrdered.isValid(contextNode) || 
				XdiAttributeInstanceOrdered.isValid(contextNode);
	}

	/**
	 * Factory method that creates an XDI ordered instance bound to a given context node.
	 * @param contextNode The context node that is an XDI ordered instance.
	 * @return The XDI ordered instance.
	 */
	public static XdiAbstractInstanceOrdered fromContextNode(ContextNode contextNode) {

		XdiAbstractInstanceOrdered xdiElement;

		if ((xdiElement = XdiEntityInstanceOrdered.fromContextNode(contextNode)) != null) return xdiElement;
		if ((xdiElement = XdiAttributeInstanceOrdered.fromContextNode(contextNode)) != null) return xdiElement;

		return null;
	}

	/*
	 * Methods for XRIs
	 */

	public static XDI3SubSegment createArcXri(String identifier) {

		return XDI3SubSegment.create("" + XDI3Constants.CS_ORDER + identifier);
	}

	public static boolean isValidArcXri(XDI3SubSegment arcXri) {

		if (arcXri == null) return false;

		if (arcXri.isSingleton()) return false;
		if (arcXri.isAttribute()) return false;
		if (arcXri.hasXRef()) return false;

		if (! XDI3Constants.CS_ORDER.equals(arcXri.getCs())) return false;

		if (! arcXri.hasLiteral()) return false;

		return true;
	}

	/*
	 * Helper classes
	 */

	public static class MappingContextNodeXdiInstanceOrderedIterator extends NotNullIterator<XdiInstanceOrdered> {

		public MappingContextNodeXdiInstanceOrderedIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, XdiInstanceOrdered> (contextNodes) {

				@Override
				public XdiInstanceOrdered map(ContextNode contextNode) {

					return XdiAbstractInstanceOrdered.fromContextNode(contextNode);
				}
			});
		}
	}
}
