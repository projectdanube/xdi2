package xdi2.core.features.nodetypes;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;
import xdi2.core.xri3.XDI3Constants;
import xdi2.core.xri3.XDI3SubSegment;

public abstract class XdiAbstractElement extends XdiAbstractSubGraph implements XdiElement {

	private static final long serialVersionUID = 8283064321616435273L;

	protected XdiAbstractElement(ContextNode contextNode) {

		super(contextNode);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI element.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI element.
	 */
	public static boolean isValid(ContextNode contextNode) {

		return XdiEntityElement.isValid(contextNode) || 
				XdiAttributeElement.isValid(contextNode);
	}

	/**
	 * Factory method that creates an XDI element bound to a given context node.
	 * @param contextNode The context node that is an XDI element.
	 * @return The XDI element.
	 */
	public static XdiAbstractElement fromContextNode(ContextNode contextNode) {

		XdiAbstractElement xdiElement;

		if ((xdiElement = XdiEntityElement.fromContextNode(contextNode)) != null) return xdiElement;
		if ((xdiElement = XdiAttributeElement.fromContextNode(contextNode)) != null) return xdiElement;

		return null;
	}

	/*
	 * Instance methods
	 */

	@Override
	public abstract XdiClass getXdiClass();

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

	public static class MappingContextNodeXdiElementClassIterator extends NotNullIterator<XdiAbstractElement> {

		public MappingContextNodeXdiElementClassIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, XdiAbstractElement> (contextNodes) {

				@Override
				public XdiAbstractElement map(ContextNode contextNode) {

					return XdiAbstractElement.fromContextNode(contextNode);
				}
			});
		}
	}
}
