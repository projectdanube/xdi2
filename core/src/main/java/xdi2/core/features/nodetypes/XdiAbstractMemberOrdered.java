package xdi2.core.features.nodetypes;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.constants.XDIConstants;
import xdi2.core.syntax.XDIArc;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;

public abstract class XdiAbstractMemberOrdered<EQC extends XdiCollection<EQC, EQI, C, U, O, I>, EQI extends XdiSubGraph<EQI>, C extends XdiCollection<EQC, EQI, C, U, O, I>, U extends XdiMemberUnordered<EQC, EQI, C, U, O, I>, O extends XdiMemberOrdered<EQC, EQI, C, U, O, I>, I extends XdiMember<EQC, EQI, C, U, O, I>> extends XdiAbstractMember<EQC, EQI, C, U, O, I> implements XdiMemberOrdered<EQC, EQI, C, U, O, I> {

	private static final long serialVersionUID = 8283064321616435273L;

	protected XdiAbstractMemberOrdered(ContextNode contextNode) {

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

		if (contextNode == null) return false;

		return XdiEntityMemberOrdered.isValid(contextNode) || 
				XdiAttributeMemberOrdered.isValid(contextNode);
	}

	/**
	 * Factory method that creates an XDI ordered instance bound to a given context node.
	 * @param contextNode The context node that is an XDI ordered instance.
	 * @return The XDI ordered instance.
	 */
	public static XdiMemberOrdered<?, ?, ?, ?, ?, ?> fromContextNode(ContextNode contextNode) {

		XdiMemberOrdered<?, ?, ?, ?, ?, ?> xdiElement;

		if ((xdiElement = XdiEntityMemberOrdered.fromContextNode(contextNode)) != null) return xdiElement;
		if ((xdiElement = XdiAttributeMemberOrdered.fromContextNode(contextNode)) != null) return xdiElement;

		return null;
	}

	/*
	 * Methods for XRIs
	 */

	public static XDIArc createarc(String identifier, boolean attribute) {

		return XDIArc.create("" + (attribute ? Character.valueOf(XDIConstants.XS_ATTRIBUTE.charAt(0)) : "") + XDIConstants.CS_MEMBER_ORDERED + identifier + (attribute ? Character.valueOf(XDIConstants.XS_ATTRIBUTE.charAt(1)) : ""));
	}

	public static boolean isValidarc(XDIArc arc, boolean attribute) {

		if (arc == null) return false;

		if (arc.isClassXs()) return false;
		if (attribute && ! arc.isAttributeXs()) return false;
		if (! attribute && arc.isAttributeXs()) return false;
		if (arc.hasXRef()) return false;

		if (! XDIConstants.CS_MEMBER_ORDERED.equals(arc.getCs())) return false;

		if (! arc.hasLiteral()) return false;

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

					return XdiAbstractMemberOrdered.fromContextNode(contextNode);
				}
			});
		}
	}
}
