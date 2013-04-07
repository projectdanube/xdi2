package xdi2.core.features.contextfunctions;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;
import xdi2.core.xri3.XDI3Constants;
import xdi2.core.xri3.XDI3SubSegment;

/**
 * An XDI entity singleton (context function), represented as a context node.
 * 
 * @author markus
 */
public final class XdiEntitySingleton extends XdiSingleton implements XdiEntity {

	private static final long serialVersionUID = 7600443284706530972L;

	protected XdiEntitySingleton(ContextNode contextNode) {

		super(contextNode);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI entity singleton.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI entity singleton.
	 */
	public static boolean isValid(ContextNode contextNode) {

		return isValidArcXri(contextNode.getArcXri());
	}

	/**
	 * Factory method that creates an XDI entity singleton bound to a given context node.
	 * @param contextNode The context node that is an XDI entity singleton.
	 * @return The XDI entity singleton.
	 */
	public static XdiEntitySingleton fromContextNode(ContextNode contextNode) {

		if (! isValid(contextNode)) return null;

		return new XdiEntitySingleton(contextNode);
	}

	/*
	 * Methods for XRIs
	 */

	public static XDI3SubSegment createArcXri(XDI3SubSegment arcXri) {

		return XDI3SubSegment.create("" + XDI3Constants.CS_BANG + XDI3Constants.CF_ENTITY_SINGLETON.charAt(0) + arcXri + XDI3Constants.CF_ENTITY_SINGLETON.charAt(1));
	}

	public static boolean isValidArcXri(XDI3SubSegment arcXri) {

		if (arcXri == null) return false;

		if (! XDI3Constants.CS_BANG.equals(arcXri.getCs())) return false;

		if (! arcXri.hasXRef()) return false;
		if (! XDI3Constants.CF_ENTITY_SINGLETON.equals(arcXri.getXRef().getCf())) return false;

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

	public static class MappingContextNodeXdiEntitySingletonIterator extends NotNullIterator<XdiEntitySingleton> {

		public MappingContextNodeXdiEntitySingletonIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, XdiEntitySingleton> (contextNodes) {

				@Override
				public XdiEntitySingleton map(ContextNode contextNode) {

					return XdiEntitySingleton.fromContextNode(contextNode);
				}
			});
		}
	}
}
