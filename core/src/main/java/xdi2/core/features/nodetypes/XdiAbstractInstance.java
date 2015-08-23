package xdi2.core.features.nodetypes;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.syntax.XDIXRef;
import xdi2.core.util.GraphUtil;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;

public abstract class XdiAbstractInstance<EQC extends XdiCollection<EQC, EQI, C, U, O, I>, EQI extends XdiSubGraph<EQI>, C extends XdiCollection<EQC, EQI, C, U, O, I>, U extends XdiInstanceUnordered<EQC, EQI, C, U, O, I>, O extends XdiInstanceOrdered<EQC, EQI, C, U, O, I>, I extends XdiInstance<EQC, EQI, C, U, O, I>> extends XdiAbstractSubGraph<EQI> implements XdiInstance<EQC, EQI, C, U, O, I> {

	private static final long serialVersionUID = 3673396905245169194L;

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

		if (contextNode == null) throw new NullPointerException();

		if (XdiAbstractInstanceUnordered.isValid(contextNode)) return true;
		if (XdiAbstractInstanceOrdered.isValid(contextNode)) return true;

		return false;
	}

	/**
	 * Factory method that creates an XDI instance bound to a given context node.
	 * @param contextNode The context node that is an XDI instance.
	 * @return The XDI instance.
	 */
	public static XdiInstance<?, ?, ?, ?, ?, ?> fromContextNode(ContextNode contextNode) {

		if (contextNode == null) throw new NullPointerException();

		XdiInstance<?, ?, ?, ?, ?, ?> xdiInstance = null;

		if ((xdiInstance = XdiAbstractInstanceUnordered.fromContextNode(contextNode)) != null) return xdiInstance;
		if ((xdiInstance = XdiAbstractInstanceOrdered.fromContextNode(contextNode)) != null) return xdiInstance;

		return null;
	}

	public static XdiInstance<?, ?, ?, ?, ?, ?> fromXDIAddress(XDIAddress XDIaddress) {

		return fromContextNode(GraphUtil.contextNodeFromComponents(XDIaddress));
	}

	/*
	 * Methods for arcs
	 */

	public static XDIArc createXDIArc(Character cs, boolean attribute, boolean immutable, boolean relative, String literal, XDIXRef xref) {

		return XDIArc.fromComponents(
				cs, 
				false, 
				false, 
				false, 
				attribute, 
				immutable, 
				relative, 
				literal, 
				xref);
	}

	public static boolean isValidXDIArc(XDIArc XDIarc, boolean attribute) {

		if (XDIarc == null) throw new NullPointerException();

		if (XdiAbstractInstanceUnordered.isValidXDIArc(XDIarc, attribute)) return true; 
		if (XdiAbstractInstanceOrdered.isValidXDIArc(XDIarc, attribute)) return true;

		return false;
	}

	/*
	 * Helper classes
	 */

	public static class MappingContextNodeXdiInstanceIterator extends NotNullIterator<XdiInstance<?, ?, ?, ?, ?, ?>> {

		public MappingContextNodeXdiInstanceIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, XdiInstance<?, ?, ?, ?, ?, ?>> (contextNodes) {

				@Override
				public XdiInstance<?, ?, ?, ?, ?, ?> map(ContextNode contextNode) {

					return XdiAbstractInstance.fromContextNode(contextNode);
				}
			});
		}
	}
}
