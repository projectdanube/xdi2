package xdi2.core.features.contextfunctions;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;
import xdi2.core.xri3.XDI3Constants;
import xdi2.core.xri3.XDI3SubSegment;

public abstract class XdiAbstractInstance extends XdiAbstractSubGraph {

	private static final long serialVersionUID = -8496645644143069191L;

	protected XdiAbstractInstance(ContextNode contextNode) {

		super(contextNode);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI instance.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI instance.
	 */
	public static boolean isValid(ContextNode contextNode) {

		return XdiEntityInstance.isValid(contextNode) || 
				XdiAttributeInstance.isValid(contextNode);
	}

	/**
	 * Factory method that creates an XDI instance bound to a given context node.
	 * @param contextNode The context node that is an XDI instance.
	 * @return The XDI instance.
	 */
	public static XdiAbstractInstance fromContextNode(ContextNode contextNode) {

		XdiAbstractInstance xdiInstance;

		if ((xdiInstance = XdiEntityInstance.fromContextNode(contextNode)) != null) return xdiInstance;
		if ((xdiInstance = XdiAttributeInstance.fromContextNode(contextNode)) != null) return xdiInstance;

		return null;
	}

	/*
	 * Instance methods
	 */

	public abstract XdiAbstractClass getXdiClass();

	/*
	 * Methods for XRIs
	 */

	public static XDI3SubSegment createArcXri(String identifier) {

		return XDI3SubSegment.create("" + XDI3Constants.CS_BANG + identifier);
	}

	public static boolean isValidArcXri(XDI3SubSegment arcXri) {

		if (arcXri == null) return false;

		if (! XDI3Constants.CS_BANG.equals(arcXri.getCs())) return false;

		if (! arcXri.hasLiteral()) return false;

		return true;
	}

	/*
	 * Helper classes
	 */

	public static class MappingContextNodeXdiInstanceIterator extends NotNullIterator<XdiAbstractInstance> {

		public MappingContextNodeXdiInstanceIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, XdiAbstractInstance> (contextNodes) {

				@Override
				public XdiAbstractInstance map(ContextNode contextNode) {

					return XdiAbstractInstance.fromContextNode(contextNode);
				}
			});
		}
	}
}
