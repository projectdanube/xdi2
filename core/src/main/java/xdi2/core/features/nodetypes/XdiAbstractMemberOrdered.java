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
	 * Checks if a context node is a valid XDI ordered member.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI ordered member.
	 */
	public static boolean isValid(ContextNode contextNode) {

		if (contextNode == null) throw new NullPointerException();

		if (XdiEntityMemberOrdered.isValid(contextNode)) return true;
		if (XdiAttributeMemberOrdered.isValid(contextNode)) return true;
		if (XdiVariableMemberOrdered.isValid(contextNode)) return true;

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

		if ((xdiElement = XdiEntityMemberOrdered.fromContextNode(contextNode)) != null) return xdiElement;
		if ((xdiElement = XdiAttributeMemberOrdered.fromContextNode(contextNode)) != null) return xdiElement;
		if ((xdiElement = XdiVariableMemberOrdered.fromContextNode(contextNode)) != null) return xdiElement;

		return null;
	}

	/*
	 * Methods for arcs
	 */

	public static XDIArc createXDIArc(String identifier, Class<? extends XdiCollection<?, ?, ?, ?, ?, ?>> clazz) {

		if (XdiEntityCollection.class.isAssignableFrom(clazz)) {

			return XDIArc.create("" + XDIConstants.CS_MEMBER_ORDERED + identifier);
		} else if (XdiAttributeCollection.class.isAssignableFrom(clazz)) {

			return XDIArc.create("" + XDIConstants.XS_ATTRIBUTE.charAt(0) + XDIConstants.CS_MEMBER_ORDERED + identifier + XDIConstants.XS_ATTRIBUTE.charAt(1));
		} else if (XdiVariableCollection.class.isAssignableFrom(clazz)) {

			return XDIArc.create("" + XDIConstants.XS_VARIABLE.charAt(0) + XDIConstants.CS_MEMBER_ORDERED + identifier + XDIConstants.XS_VARIABLE.charAt(1));
		} else {

			throw new IllegalArgumentException("Unknown class for ordered member " + clazz.getName());
		}
	}

	public static boolean isValidXDIArc(XDIArc XDIarc, Class<? extends XdiCollection<?, ?, ?, ?, ?, ?>> clazz) {

		if (XDIarc == null) throw new NullPointerException();

		if (XdiEntityCollection.class.isAssignableFrom(clazz)) {

			if (! XDIConstants.CS_MEMBER_ORDERED.equals(XDIarc.getCs())) return false;
			if (XDIarc.isClassXs()) return false;
			if (XDIarc.isAttributeXs()) return false;
			if (! XDIarc.hasLiteral()) return false;
			if (XDIarc.hasXRef()) return false;
		} else if (XdiAttributeCollection.class.isAssignableFrom(clazz)) {

			if (! XDIConstants.CS_MEMBER_ORDERED.equals(XDIarc.getCs())) return false;
			if (XDIarc.isClassXs()) return false;
			if (! XDIarc.isAttributeXs()) return false;
			if (! XDIarc.hasLiteral()) return false;
			if (XDIarc.hasXRef()) return false;
		} else if (XdiVariableCollection.class.isAssignableFrom(clazz)) {

			if (XDIarc.hasCs()) return false;
			if (XDIarc.isClassXs()) return false;
			if (XDIarc.isAttributeXs()) return false;
			if (XDIarc.hasLiteral()) return false;
			if (! XDIarc.hasXRef()) return false;
			if (! XDIConstants.XS_VARIABLE.equals(XDIarc.getXRef().getXs())) return false;
			if (! XDIarc.getXRef().hasXDIAddress()) return false;
			if (XDIarc.getXRef().hasPartialSubjectAndPredicate()) return false;
			if (XDIarc.getXRef().hasLiteral()) return false;
			if (XDIarc.getXRef().hasIri()) return false;
			if (XDIarc.getXRef().getXDIAddress().getNumXDIArcs() != 1) return false;
			if (! XDIConstants.CS_MEMBER_ORDERED.equals(XDIarc.getXRef().getXDIAddress().getFirstXDIArc())) return false;
			if (XDIarc.getXRef().getXDIAddress().getFirstXDIArc().isClassXs()) return false;
			if (XDIarc.getXRef().getXDIAddress().getFirstXDIArc().isAttributeXs()) return false;
			if (! XDIarc.getXRef().getXDIAddress().getFirstXDIArc().hasLiteral()) return false;
			if (XDIarc.getXRef().getXDIAddress().getFirstXDIArc().hasXRef()) return false;
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

					return XdiAbstractMemberOrdered.fromContextNode(contextNode);
				}
			});
		}
	}
}
