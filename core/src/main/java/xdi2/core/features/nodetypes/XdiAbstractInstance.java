package xdi2.core.features.nodetypes;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;
import xdi2.core.xri3.XDI3Constants;
import xdi2.core.xri3.XDI3SubSegment;

public abstract class XdiAbstractInstance extends XdiAbstractSubGraph implements XdiInstance {

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

	@Override
	public abstract XdiClass getXdiClass();

	/*
	 * Methods for XRIs
	 */

	public static XDI3SubSegment createArcXri(String identifier, boolean mutable) {

		Character cs = mutable ? XDI3Constants.CS_STAR : XDI3Constants.CS_BANG;
		
		return XDI3SubSegment.create("" + cs + identifier);
	}

	public static boolean isValidArcXri(XDI3SubSegment arcXri) {

		if (arcXri == null) return false;

		if (arcXri.isSingleton()) return false;
		if (arcXri.isAttribute()) return false;
		if (arcXri.hasXRef()) return false;

		if (! XDI3Constants.CS_STAR.equals(arcXri.getCs()) && ! XDI3Constants.CS_BANG.equals(arcXri.getCs())) return false;

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
