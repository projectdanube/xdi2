package xdi2.core.features.nodetypes;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.syntax.XDIArc;
import xdi2.core.util.VariableUtil;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;

/**
 * An XDI variable (context function), represented as a context node.
 * 
 * @author markus
 */
public final class XdiVariable extends XdiAbstractSubGraph<XdiVariable> {

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

		if (contextNode == null) return false;

		return isValidarc(contextNode.getArc());
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
	 * Methods for XRIs
	 */

	/**
	 * Checks if a given XRI is an XDI variable XRI.
	 * @param arc An XDI variable XRI.
	 * @return True, if the XRI is an XDI variable XRI.
	 */
	public static boolean isValidarc(XDIArc arc) {

		return VariableUtil.isVariable(arc);
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
