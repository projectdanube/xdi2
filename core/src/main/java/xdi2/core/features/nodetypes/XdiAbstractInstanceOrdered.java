package xdi2.core.features.nodetypes;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.constants.XDIConstants;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.util.GraphUtil;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;

public abstract class XdiAbstractInstanceOrdered<EQC extends XdiCollection<EQC, EQI, C, U, O, I>, EQI extends XdiSubGraph<EQI>, C extends XdiCollection<EQC, EQI, C, U, O, I>, U extends XdiInstanceUnordered<EQC, EQI, C, U, O, I>, O extends XdiInstanceOrdered<EQC, EQI, C, U, O, I>, I extends XdiInstance<EQC, EQI, C, U, O, I>> extends XdiAbstractInstance<EQC, EQI, C, U, O, I> implements XdiInstanceOrdered<EQC, EQI, C, U, O, I> {

	private static final long serialVersionUID = 8283064321616435273L;

	protected XdiAbstractInstanceOrdered(ContextNode contextNode) {

		super(contextNode);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI ordered instance.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI ordered instance.
	 */
	public static boolean isValid(ContextNode contextNode) {

		if (contextNode == null) throw new NullPointerException();

		if (XdiEntityInstanceOrdered.isValid(contextNode)) return true;
		if (XdiAttributeInstanceOrdered.isValid(contextNode)) return true;

		return false;
	}

	/**
	 * Factory method that creates an XDI ordered instance bound to a given context node.
	 * @param contextNode The context node that is an XDI ordered instance.
	 * @return The XDI ordered instance.
	 */
	public static XdiInstanceOrdered<?, ?, ?, ?, ?, ?> fromContextNode(ContextNode contextNode) {

		if (contextNode == null) throw new NullPointerException();

		XdiInstanceOrdered<?, ?, ?, ?, ?, ?> xdiElement;

		if ((xdiElement = XdiEntityInstanceOrdered.fromContextNode(contextNode)) != null) return xdiElement;
		if ((xdiElement = XdiAttributeInstanceOrdered.fromContextNode(contextNode)) != null) return xdiElement;

		return null;
	}

	public static XdiInstanceOrdered<?, ?, ?, ?, ?, ?> fromXDIAddress(XDIAddress XDIaddress) {

		return fromContextNode(GraphUtil.contextNodeFromComponents(XDIaddress));
	}

	/*
	 * Methods for arcs
	 */

	public static XDIArc createXDIArc(boolean attribute, boolean immutable, boolean relative, String literal) {

		return XDIArc.fromComponents(
				XDIConstants.CS_INSTANCE_ORDERED, 
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

			if (! XDIConstants.CS_INSTANCE_ORDERED.equals(XDIarc.getCs())) return false;
			if (XDIarc.isCollection()) return false;
			if (XDIarc.isAttribute()) return false;
			if (! XDIarc.hasLiteral()) return false;
			if (XDIarc.hasXRef()) return false;
		} else {

			if (! XDIConstants.CS_INSTANCE_ORDERED.equals(XDIarc.getCs())) return false;
			if (XDIarc.isCollection()) return false;
			if (! XDIarc.isAttribute()) return false;
			if (! XDIarc.hasLiteral()) return false;
			if (XDIarc.hasXRef()) return false;
		}

		return true;
	}

	/*
	 * Helper classes
	 */

	public static class MappingContextNodeXdiInstanceOrderedIterator extends NotNullIterator<XdiInstanceOrdered<?, ?, ?, ?, ?, ?>> {

		public MappingContextNodeXdiInstanceOrderedIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, XdiInstanceOrdered<?, ?, ?, ?, ?, ?>> (contextNodes) {

				@Override
				public XdiInstanceOrdered<?, ?, ?, ?, ?, ?> map(ContextNode contextNode) {

					return XdiAbstractInstanceOrdered.fromContextNode(contextNode);
				}
			});
		}
	}
}
