package xdi2.core.features.nodetypes;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.constants.XDIConstants;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.util.GraphUtil;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;

public abstract class XdiAbstractInstanceUnordered<EQC extends XdiCollection<EQC, EQI, C, U, O, I>, EQI extends XdiSubGraph<EQI>, C extends XdiCollection<EQC, EQI, C, U, O, I>, U extends XdiInstanceUnordered<EQC, EQI, C, U, O, I>, O extends XdiInstanceOrdered<EQC, EQI, C, U, O, I>, I extends XdiInstance<EQC, EQI, C, U, O, I>> extends XdiAbstractInstance<EQC, EQI, C, U, O, I> implements XdiInstanceUnordered<EQC, EQI, C, U, O, I> {

	private static final long serialVersionUID = -8496645644143069191L;

	protected XdiAbstractInstanceUnordered(ContextNode contextNode) {

		super(contextNode);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI unordered instance.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI unordered instance.
	 */
	public static boolean isValid(ContextNode contextNode) {

		if (contextNode == null) throw new NullPointerException();

		if (XdiEntityInstanceUnordered.isValid(contextNode)) return true;
		if (XdiAttributeInstanceUnordered.isValid(contextNode)) return true;

		return false;
	}

	/**
	 * Factory method that creates an XDI unordered instance bound to a given context node.
	 * @param contextNode The context node that is an XDI unordered instance.
	 * @return The XDI unordered instance.
	 */
	public static XdiInstanceUnordered<?, ?, ?, ?, ?, ?> fromContextNode(ContextNode contextNode) {

		if (contextNode == null) throw new NullPointerException();

		XdiInstanceUnordered<?, ?, ?, ?, ?, ?> xdiInstance;

		if ((xdiInstance = XdiEntityInstanceUnordered.fromContextNode(contextNode)) != null) return xdiInstance;
		if ((xdiInstance = XdiAttributeInstanceUnordered.fromContextNode(contextNode)) != null) return xdiInstance;

		return null;
	}

	public static XdiInstanceUnordered<?, ?, ?, ?, ?, ?> fromXDIAddress(XDIAddress XDIaddress) {

		return fromContextNode(GraphUtil.contextNodeFromComponents(XDIaddress));
	}

	/*
	 * Methods for arcs
	 */

	public static XDIArc createXDIArc(boolean attribute, boolean immutable, boolean relative, String literal) {

		return XDIArc.fromComponents(
				XDIConstants.CS_INSTANCE_UNORDERED, 
				false, 
				false, 
				false, 
				attribute, 
				immutable, 
				relative, 
				literal, 
				null);
	}

	public static boolean isValidXDIArc(XDIArc XDIarc, boolean attribute) {

		if (XDIarc == null) throw new NullPointerException();

		if (! attribute) {

			if (! XDIConstants.CS_INSTANCE_UNORDERED.equals(XDIarc.getCs())) return false;
			if (XDIarc.isCollection()) return false;
			if (XDIarc.isAttribute()) return false;
			if (! XDIarc.hasLiteral() && ! XDIarc.hasXRef()) return false;
		} else {

			if (! XDIConstants.CS_INSTANCE_UNORDERED.equals(XDIarc.getCs())) return false;
			if (XDIarc.isCollection()) return false;
			if (! XDIarc.isAttribute()) return false;
			if (! XDIarc.hasLiteral() && ! XDIarc.hasXRef()) return false;
		}

		return true;
	}

	/*
	 * Helper classes
	 */

	public static class MappingContextNodeXdiInstanceUnorderedIterator extends NotNullIterator<XdiInstanceUnordered<?, ?, ?, ?, ?, ?>> {

		public MappingContextNodeXdiInstanceUnorderedIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, XdiInstanceUnordered<?, ?, ?, ?, ?, ?>> (contextNodes) {

				@Override
				public XdiInstanceUnordered<?, ?, ?, ?, ?, ?> map(ContextNode contextNode) {

					return XdiAbstractInstanceUnordered.fromContextNode(contextNode);
				}
			});
		}
	}
}
