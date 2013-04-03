package xdi2.core.features.contextfunctions;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;
import xdi2.core.xri3.XDI3SubSegment;

public abstract class XdiInstance extends XdiSubGraph {

	private static final long serialVersionUID = -8496645644143069191L;

	protected XdiInstance(ContextNode contextNode) {

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
	public static XdiInstance fromContextNode(ContextNode contextNode) {

		XdiInstance xdiInstance;

		if ((xdiInstance = XdiEntityInstance.fromContextNode(contextNode)) != null) return xdiInstance;
		if ((xdiInstance = XdiAttributeInstance.fromContextNode(contextNode)) != null) return xdiInstance;

		return null;
	}

	/*
	 * Instance methods
	 */

	public abstract XdiClass getXdiClass();

	/*
	 * Methods for XRIs
	 */

	public static boolean isValidArcXri(XDI3SubSegment arcXri) {

		return XdiEntityInstance.isValidArcXri(arcXri) || 
				XdiAttributeInstance.isValidArcXri(arcXri);
	}

	/*
	 * Helper classes
	 */

	public static class MappingContextNodeXdiInstanceIterator extends NotNullIterator<XdiInstance> {

		public MappingContextNodeXdiInstanceIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, XdiInstance> (contextNodes) {

				@Override
				public XdiInstance map(ContextNode contextNode) {

					return XdiInstance.fromContextNode(contextNode);
				}
			});
		}
	}
}
