package xdi2.core.features.nodetypes;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.constants.XDIConstants;
import xdi2.core.syntax.XDIArc;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;

/**
 * An XDI variable singleton, represented as a context node.
 * 
 * @author markus
 */
public final class XdiVariableSingleton extends XdiAbstractSingleton<XdiVariable> implements XdiVariable {

	private static final long serialVersionUID = 3095667439821943614L;

	protected XdiVariableSingleton(ContextNode contextNode) {

		super(contextNode);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI variable singleton.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI variable singleton.
	 */
	public static boolean isValid(ContextNode contextNode) {

		if (contextNode == null) throw new NullPointerException();

		if (contextNode.getXDIArc() == null || ! isValidXDIArc(contextNode.getXDIArc())) return false;
		if (XdiAttributeCollection.isValid(contextNode.getContextNode())) return false;
		if (XdiAbstractAttribute.isValid(contextNode.getContextNode())) return false;

		return true;
	}

	/**
	 * Factory method that creates an XDI entity singleton bound to a given context node.
	 * @param contextNode The context node that is an XDI entity singleton.
	 * @return The XDI entity singleton.
	 */
	public static XdiVariableSingleton fromContextNode(ContextNode contextNode) {

		if (contextNode == null) throw new NullPointerException();

		if (! isValid(contextNode)) return null;

		return new XdiVariableSingleton(contextNode);
	}

	/*
	 * Methods for arcs
	 */

	public static XDIArc createVariableXDIArc(XDIArc XDIarc) {

		return XDIarc;
	}

	public static boolean isValidXDIArc(XDIArc XDIarc) {

		if (XDIarc == null) throw new NullPointerException();

		if (XDIarc.hasCs()) return false;
		if (XDIarc.isClassXs()) return false;
		if (XDIarc.isAttributeXs()) return false;
		if (XDIarc.hasLiteral()) return false;
		if (! XDIarc.hasXRef()) return false;
		if (! XDIConstants.XS_VARIABLE.equals(XDIarc.getXRef().getXs())) return false;

		return true;
	}

	/*
	 * Helper classes
	 */

	public static class MappingContextNodeXdiEntitySingletonIterator extends NotNullIterator<XdiVariableSingleton> {

		public MappingContextNodeXdiEntitySingletonIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, XdiVariableSingleton> (contextNodes) {

				@Override
				public XdiVariableSingleton map(ContextNode contextNode) {

					return XdiVariableSingleton.fromContextNode(contextNode);
				}
			});
		}
	}
}
