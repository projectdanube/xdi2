package xdi2.core.features.nodetypes;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;
import xdi2.core.xri3.XDI3Constants;
import xdi2.core.xri3.XDI3SubSegment;

/**
 * An XDI organizational singleton (context function), represented as a context node.
 * 
 * @author markus
 */
public final class XdiOrganizationalSingleton extends XdiAbstractSingleton {

	private static final long serialVersionUID = 7600443284706530972L;

	protected XdiOrganizationalSingleton(ContextNode contextNode) {

		super(contextNode);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI organizational singleton.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI organizational singleton.
	 */
	public static boolean isValid(ContextNode contextNode) {

		return isValidArcXri(contextNode.getArcXri());
	}

	/**
	 * Factory method that creates an XDI organizational singleton bound to a given context node.
	 * @param contextNode The context node that is an XDI organizational singleton.
	 * @return The XDI organizational singleton.
	 */
	public static XdiOrganizationalSingleton fromContextNode(ContextNode contextNode) {

		if (! isValid(contextNode)) return null;

		return new XdiOrganizationalSingleton(contextNode);
	}

	/*
	 * Methods for XRIs
	 */

	public static boolean isValidArcXri(XDI3SubSegment arcXri) {

		if (arcXri == null) return false;

		if (arcXri.isSingleton()) return false;
		if (arcXri.isAttribute()) return false;

		if (! XDI3Constants.CS_AT.equals(arcXri.getCs())) return false;

		if (! arcXri.hasLiteral() && ! arcXri.hasXRef()) return false;

		return true;
	}

	/*
	 * Helper classes
	 */

	public static class MappingContextNodeXdiPersonalSingletonIterator extends NotNullIterator<XdiOrganizationalSingleton> {

		public MappingContextNodeXdiPersonalSingletonIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, XdiOrganizationalSingleton> (contextNodes) {

				@Override
				public XdiOrganizationalSingleton map(ContextNode contextNode) {

					return XdiOrganizationalSingleton.fromContextNode(contextNode);
				}
			});
		}
	}
}
