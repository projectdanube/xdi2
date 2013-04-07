package xdi2.core.features.contextfunctions;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;
import xdi2.core.xri3.XDI3Constants;
import xdi2.core.xri3.XDI3SubSegment;

/**
 * An XDI relative singleton (context function), represented as a context node.
 * 
 * @author markus
 */
public final class XdiRelativeSingleton extends XdiAbstractSingleton {

	private static final long serialVersionUID = 7600443284706530972L;

	protected XdiRelativeSingleton(ContextNode contextNode) {

		super(contextNode);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI relative singleton.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI relative singleton.
	 */
	public static boolean isValid(ContextNode contextNode) {

		return isValidArcXri(contextNode.getArcXri());
	}

	/**
	 * Factory method that creates an XDI relative singleton bound to a given context node.
	 * @param contextNode The context node that is an XDI relative singleton.
	 * @return The XDI relative singleton.
	 */
	public static XdiRelativeSingleton fromContextNode(ContextNode contextNode) {

		if (! isValid(contextNode)) return null;

		return new XdiRelativeSingleton(contextNode);
	}

	/*
	 * Methods for XRIs
	 */

	public static boolean isValidArcXri(XDI3SubSegment arcXri) {

		if (arcXri == null) return false;

		if (! XDI3Constants.CS_STAR.equals(arcXri.getCs())) return false;

		if (! arcXri.hasLiteral() && ! arcXri.hasXRef()) return false;

		return true;
	}

	/*
	 * Helper classes
	 */

	public static class MappingContextNodeXdiRelativeSingletonIterator extends NotNullIterator<XdiRelativeSingleton> {

		public MappingContextNodeXdiRelativeSingletonIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, XdiRelativeSingleton> (contextNodes) {

				@Override
				public XdiRelativeSingleton map(ContextNode contextNode) {

					return XdiRelativeSingleton.fromContextNode(contextNode);
				}
			});
		}
	}
}
