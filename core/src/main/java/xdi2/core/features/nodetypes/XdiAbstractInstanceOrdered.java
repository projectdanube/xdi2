package xdi2.core.features.nodetypes;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.constants.XDIConstants;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.util.GraphUtil;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;

public abstract class XdiAbstractInstanceOrdered<EQC extends XdiCollection<EQC, EQI, C, U, O, I>, EQI extends XdiSubGraph<EQI>, C extends XdiCollection<EQC, EQI, C, U, O, I>, U extends XdiMemberUnordered<EQC, EQI, C, U, O, I>, O extends XdiMemberOrdered<EQC, EQI, C, U, O, I>, I extends XdiMember<EQC, EQI, C, U, O, I>> extends XdiAbstractInstance<EQC, EQI, C, U, O, I> implements XdiMemberOrdered<EQC, EQI, C, U, O, I> {

	private static final long serialVersionUID = 8283064321616435273L;

	protected XdiAbstractInstanceOrdered(ContextNode contextNode) {

		super(contextNode);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI ordered member.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI ordered member.
	 */
	public static boolean isValid(ContextNode contextNode) {

		if (contextNode == null) throw new NullPointerException();

		if (XdiEntityInstanceOrdered.isValid(contextNode)) return true;
		if (XdiAttributeInstanceOrdered.isValid(contextNode)) return true;

		return false;
	}

	/**
	 * Factory method that creates an XDI ordered member bound to a given context node.
	 * @param contextNode The context node that is an XDI ordered member.
	 * @return The XDI ordered member.
	 */
	public static XdiMemberOrdered<?, ?, ?, ?, ?, ?> fromContextNode(ContextNode contextNode) {

		if (contextNode == null) throw new NullPointerException();

		XdiMemberOrdered<?, ?, ?, ?, ?, ?> xdiElement;

		if ((xdiElement = XdiEntityInstanceOrdered.fromContextNode(contextNode)) != null) return xdiElement;
		if ((xdiElement = XdiAttributeInstanceOrdered.fromContextNode(contextNode)) != null) return xdiElement;

		return null;
	}

	public static XdiMemberOrdered<?, ?, ?, ?, ?, ?> fromXDIAddress(XDIAddress XDIaddress) {

		return fromContextNode(GraphUtil.contextNodeFromComponents(XDIaddress));
	}

	/*
	 * Methods for arcs
	 */

	public static XDIArc createXDIArc(String identifier, boolean immutable, boolean relative, Class<? extends XdiCollection<?, ?, ?, ?, ?, ?>> clazz) {

		if (XdiEntityCollection.class.isAssignableFrom(clazz)) {

			return XDIArc.create("" + XDIConstants.CS_INSTANCE_ORDERED + (immutable ? XDIConstants.S_IMMUTABLE : "") + (relative ? XDIConstants.S_RELATIVE : "") + identifier);
		} else if (XdiAttributeCollection.class.isAssignableFrom(clazz)) {

			return XDIArc.create("" + XDIConstants.XS_ATTRIBUTE.charAt(0) + XDIConstants.CS_INSTANCE_ORDERED + (immutable ? XDIConstants.S_IMMUTABLE : "") + (relative ? XDIConstants.S_RELATIVE : "") + identifier + XDIConstants.XS_ATTRIBUTE.charAt(1));
		} else {

			throw new IllegalArgumentException("Unknown class for ordered member " + clazz.getName());
		}
	}

	public static boolean isValidXDIArc(XDIArc XDIarc, Class<? extends XdiCollection<?, ?, ?, ?, ?, ?>> clazz) {

		if (XDIarc == null) throw new NullPointerException();

		if (XdiEntityCollection.class.isAssignableFrom(clazz)) {

			if (! XDIConstants.CS_INSTANCE_ORDERED.equals(XDIarc.getCs())) return false;
			if (XDIarc.isCollection()) return false;
			if (XDIarc.isAttribute()) return false;
			if (! XDIarc.hasLiteral()) return false;
			if (XDIarc.hasXRef()) return false;
		} else if (XdiAttributeCollection.class.isAssignableFrom(clazz)) {

			if (! XDIConstants.CS_INSTANCE_ORDERED.equals(XDIarc.getCs())) return false;
			if (XDIarc.isCollection()) return false;
			if (! XDIarc.isAttribute()) return false;
			if (! XDIarc.hasLiteral()) return false;
			if (XDIarc.hasXRef()) return false;
		} else {

			throw new IllegalArgumentException("Unknown class for ordered member " + clazz.getName());
		}

		return true;
	}

	/*
	 * Helper classes
	 */

	public static class MappingContextNodeXdiMemberOrderedIterator extends NotNullIterator<XdiMemberOrdered<?, ?, ?, ?, ?, ?>> {

		public MappingContextNodeXdiMemberOrderedIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, XdiMemberOrdered<?, ?, ?, ?, ?, ?>> (contextNodes) {

				@Override
				public XdiMemberOrdered<?, ?, ?, ?, ?, ?> map(ContextNode contextNode) {

					return XdiAbstractInstanceOrdered.fromContextNode(contextNode);
				}
			});
		}
	}
}
