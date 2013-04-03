package xdi2.core.features.contextfunctions;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;
import xdi2.core.xri3.XDI3Constants;
import xdi2.core.xri3.XDI3SubSegment;

/**
 * An XDI attribute singleton (context function), represented as a context node.
 * 
 * @author markus
 */
public final class XdiAttributeSingleton extends XdiSingleton implements XdiAttribute {

	private static final long serialVersionUID = -5769813522592588864L;

	protected XdiAttributeSingleton(ContextNode contextNode) {

		super(contextNode);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI attribute singleton.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI attribute singleton.
	 */
	public static boolean isValid(ContextNode contextNode) {

		return isValidArcXri(contextNode.getArcXri());
	}

	/**
	 * Factory method that creates an XDI attribute singleton bound to a given context node.
	 * @param contextNode The context node that is an XDI attribute singleton.
	 * @return The XDI attribute singleton.
	 */
	public static XdiAttributeSingleton fromContextNode(ContextNode contextNode) {

		if (! isValid(contextNode)) return null;

		return new XdiAttributeSingleton(contextNode);
	}

	/*
	 * Methods for XRIs
	 */

	public static boolean isValidArcXri(XDI3SubSegment arcXri) {

		if (arcXri == null) return false;

		if (! XDI3Constants.CS_BANG.equals(arcXri.getCs())) return false;

		if (! arcXri.hasXRef()) return false;
		if (! XDI3Constants.CF_ATTRIBUTE_SINGLETON.equals(arcXri.getXRef().getCf())) return false;

		if (! arcXri.getXRef().hasSegment()) return false;

		if (XDI3Constants.CS_DOLLAR.equals(arcXri.getXRef().getSegment().getFirstSubSegment().getCs())) {

		} else if (XDI3Constants.CS_PLUS.equals(arcXri.getXRef().getSegment().getFirstSubSegment().getCs())) {

		} else {

			return false;
		}

		return true;
	}

	/*
	 * Helper classes
	 */

	public static class MappingContextNodeXdiAttributeSingletonIterator extends NotNullIterator<XdiAttributeSingleton> {

		public MappingContextNodeXdiAttributeSingletonIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, XdiAttributeSingleton> (contextNodes) {

				@Override
				public XdiAttributeSingleton map(ContextNode contextNode) {

					return XdiAttributeSingleton.fromContextNode(contextNode);
				}
			});
		}
	}
}
