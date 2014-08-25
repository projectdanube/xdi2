package xdi2.core.features.nodetypes;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.syntax.XDIArc;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;

public abstract class XdiAbstractVariable extends XdiAbstractSubGraph<XdiAbstractVariable> {

	private static final long serialVersionUID = -5443590668167159237L;

	protected XdiAbstractVariable(ContextNode contextNode) {

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

		if (contextNode == null) throw new NullPointerException();

		if (XdiVariableSingleton.isValid(contextNode)) return true; 
		if (XdiVariableMemberUnordered.isValid(contextNode)) return true;
		if (XdiVariableMemberOrdered.isValid(contextNode)) return true;

		return false;
	}

	/**
	 * Factory method that creates an XDI variable bound to a given context node.
	 * @param contextNode The context node that is an XDI variable.
	 * @return The XDI variable.
	 */
	public static XdiVariable fromContextNode(ContextNode contextNode) {

		if (contextNode == null) throw new NullPointerException();

		XdiVariable xdiVariable = null;

		if ((xdiVariable = XdiVariableSingleton.fromContextNode(contextNode)) != null) return xdiVariable;
		if ((xdiVariable = XdiVariableMemberUnordered.fromContextNode(contextNode)) != null) return xdiVariable;
		if ((xdiVariable = XdiVariableMemberOrdered.fromContextNode(contextNode)) != null) return xdiVariable;

		return null;
	}

	/*
	 * Methods for arcs
	 */

	public static boolean isVariableXDIarc(XDIArc XDIarc) {

		if (XDIarc == null) throw new NullPointerException();

		if (XdiVariableSingleton.isVariableSingletonXDIArc(XDIarc)) return true;
		if (XdiVariableMemberUnordered.isVariableMemberUnorderedXDIArc(XDIarc)) return true;
		if (XdiVariableMemberOrdered.isVariableMemberOrderedXDIArc(XDIarc)) return true;

		return false;
	}

	/*
	 * Helper classes
	 */

	public static class MappingContextNodeXdiVariableIterator extends NotNullIterator<XdiVariable> {

		public MappingContextNodeXdiVariableIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, XdiVariable> (contextNodes) {

				@Override
				public XdiVariable map(ContextNode contextNode) {

					return XdiAbstractVariable.fromContextNode(contextNode);
				}
			});
		}
	}
}
