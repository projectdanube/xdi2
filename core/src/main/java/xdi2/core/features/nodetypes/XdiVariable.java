package xdi2.core.features.nodetypes;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.util.VariableUtil;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;
import xdi2.core.xri3.XDI3SubSegment;

/**
 * An XDI variable (context function), represented as a context node.
 * 
 * @author markus
 */
public final class XdiVariable extends XdiAbstractSubGraph {

	private static final long serialVersionUID = -5443590668167159237L;

	protected XdiVariable(ContextNode contextNode) {

		super(contextNode);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI variable.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI variable.
	 */
	public static boolean isValid(ContextNode contextNode) {

		return isVariableArcXri(contextNode.getArcXri());
	}

	/**
	 * Factory method that creates an XDI variable bound to a given context node.
	 * @param contextNode The context node that is an XDI variable.
	 * @return The XDI variable.
	 */
	public static XdiVariable fromContextNode(ContextNode contextNode) {

		if (! isValid(contextNode)) return null;

		return new XdiVariable(contextNode);
	}

	/*
	 * Methods for XDI variable XRIs
	 */

	/**
	 * Checks if a given XRI is an XDI variable XRI.
	 * @param arcXri An XDI variable XRI.
	 * @return True, if the XRI is an XDI variable XRI.
	 */
	public static boolean isVariableArcXri(XDI3SubSegment arcXri) {

		return VariableUtil.isVariable(arcXri);
	}

	/*
	 * Helper classes
	 */

	public static class MappingContextNodeXdiVariableIterator extends NotNullIterator<XdiVariable> {

		public MappingContextNodeXdiVariableIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, XdiVariable> (contextNodes) {

				@Override
				public XdiVariable map(ContextNode contextNode) {

					return XdiVariable.fromContextNode(contextNode);
				}
			});
		}
	}
}
