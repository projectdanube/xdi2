package xdi2.core.features.contextfunctions;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;

/**
 * An XDI entity instance (context function), represented as a context node.
 * 
 * @author markus
 */
public final class XdiEntityInstance extends XdiAbstractInstance implements XdiEntity {

	private static final long serialVersionUID = 1027868266675630350L;

	protected XdiEntityInstance(ContextNode contextNode) {

		super(contextNode);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI entity instance.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI entity instance.
	 */
	public static boolean isValid(ContextNode contextNode) {

		return
				isValidArcXri(contextNode.getArcXri()) &&
				XdiEntityClass.isValid(contextNode.getContextNode());
	}

	/**
	 * Factory method that creates an XDI entity instance bound to a given context node.
	 * @param contextNode The context node that is an XDI entity instance.
	 * @return The XDI entity instance.
	 */
	public static XdiEntityInstance fromContextNode(ContextNode contextNode) {

		if (! isValid(contextNode)) return null;

		return new XdiEntityInstance(contextNode);
	}

	/*
	 * Instance methods
	 */

	/**
	 * Gets or returns the parent XDI class of this XDI entity instance.
	 * @return The parent XDI class.
	 */
	@Override
	public XdiEntityClass getXdiClass() {

		return new XdiEntityClass(this.getContextNode().getContextNode());
	}

	/*
	 * Helper classes
	 */

	public static class MappingContextNodeXdiEntityInstanceIterator extends NotNullIterator<XdiEntityInstance> {

		public MappingContextNodeXdiEntityInstanceIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, XdiEntityInstance> (contextNodes) {

				@Override
				public XdiEntityInstance map(ContextNode contextNode) {

					return XdiEntityInstance.fromContextNode(contextNode);
				}
			});
		}
	}
}
