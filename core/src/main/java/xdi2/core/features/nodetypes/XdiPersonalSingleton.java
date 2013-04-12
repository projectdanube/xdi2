package xdi2.core.features.nodetypes;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;
import xdi2.core.xri3.XDI3Constants;
import xdi2.core.xri3.XDI3SubSegment;

/**
 * An XDI personal singleton (context function), represented as a context node.
 * 
 * @author markus
 */
public final class XdiPersonalSingleton extends XdiAbstractSingleton {

	private static final long serialVersionUID = 7600443284706530972L;

	protected XdiPersonalSingleton(ContextNode contextNode) {

		super(contextNode);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI personal singleton.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI personal singleton.
	 */
	public static boolean isValid(ContextNode contextNode) {

		return isValidArcXri(contextNode.getArcXri());
	}

	/**
	 * Factory method that creates an XDI personal singleton bound to a given context node.
	 * @param contextNode The context node that is an XDI personal singleton.
	 * @return The XDI personal singleton.
	 */
	public static XdiPersonalSingleton fromContextNode(ContextNode contextNode) {

		if (! isValid(contextNode)) return null;

		return new XdiPersonalSingleton(contextNode);
	}

	/*
	 * Methods for XRIs
	 */

	public static boolean isValidArcXri(XDI3SubSegment arcXri) {

		if (arcXri == null) return false;

		if (arcXri.isSingleton()) return false;
		if (arcXri.isAttribute()) return false;

		if (! XDI3Constants.CS_EQUALS.equals(arcXri.getCs())) return false;

		if (! arcXri.hasLiteral() && ! arcXri.hasXRef()) return false;

		return true;
	}

	/*
	 * Helper classes
	 */

	public static class MappingContextNodeXdiPersonalSingletonIterator extends NotNullIterator<XdiPersonalSingleton> {

		public MappingContextNodeXdiPersonalSingletonIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, XdiPersonalSingleton> (contextNodes) {

				@Override
				public XdiPersonalSingleton map(ContextNode contextNode) {

					return XdiPersonalSingleton.fromContextNode(contextNode);
				}
			});
		}
	}
}
