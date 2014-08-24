package xdi2.core.features.nodetypes;

import java.security.MessageDigest;
import java.util.Iterator;
import java.util.UUID;

import org.apache.commons.codec.binary.Hex;

import xdi2.core.ContextNode;
import xdi2.core.constants.XDIConstants;
import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.core.syntax.XDIArc;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;

public abstract class XdiAbstractMemberUnordered<EQC extends XdiCollection<EQC, EQI, C, U, O, I>, EQI extends XdiSubGraph<EQI>, C extends XdiCollection<EQC, EQI, C, U, O, I>, U extends XdiMemberUnordered<EQC, EQI, C, U, O, I>, O extends XdiMemberOrdered<EQC, EQI, C, U, O, I>, I extends XdiMember<EQC, EQI, C, U, O, I>> extends XdiAbstractMember<EQC, EQI, C, U, O, I> implements XdiMemberUnordered<EQC, EQI, C, U, O, I> {

	private static final long serialVersionUID = -8496645644143069191L;

	protected XdiAbstractMemberUnordered(ContextNode contextNode) {

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

		if (contextNode == null) return false;

		return XdiEntityMemberUnordered.isValid(contextNode) || 
				XdiAttributeMemberUnordered.isValid(contextNode);
	}

	/**
	 * Factory method that creates an XDI unordered instance bound to a given context node.
	 * @param contextNode The context node that is an XDI unordered instance.
	 * @return The XDI unordered instance.
	 */
	public static XdiMemberUnordered<?, ?, ?, ?, ?, ?> fromContextNode(ContextNode contextNode) {

		XdiMemberUnordered<?, ?, ?, ?, ?, ?> xdiMember;

		if ((xdiMember = XdiEntityMemberUnordered.fromContextNode(contextNode)) != null) return xdiMember;
		if ((xdiMember = XdiAttributeMemberUnordered.fromContextNode(contextNode)) != null) return xdiMember;

		return null;
	}

	/*
	 * Methods for XRIs
	 */

	public static boolean isValidXDIArc(XDIArc arc, Class<? extends XdiCollection<?, ?, ?, ?, ?, ?>> clazz) {

		if (arc == null) return false;

		if (XdiEntityCollection.class.isAssignableFrom(clazz)) {

			if (! XDIConstants.CS_MEMBER_UNORDERED.equals(arc.getCs())) return false;
			if (arc.isClassXs()) return false;
			if (arc.isAttributeXs()) return false;
			if (! arc.hasLiteral()) return false;
			if (arc.hasXRef()) return false;
		} else if (XdiAttributeCollection.class.isAssignableFrom(clazz)) {

			if (! XDIConstants.CS_MEMBER_UNORDERED.equals(arc.getCs())) return false;
			if (arc.isClassXs()) return false;
			if (! arc.isAttributeXs()) return false;
			if (! arc.hasLiteral()) return false;
			if (arc.hasXRef()) return false;
		} else if (XdiVariableCollection.class.isAssignableFrom(clazz)) {

			if (arc.hasCs()) return false;
			if (arc.isClassXs()) return false;
			if (arc.isAttributeXs()) return false;
			if (arc.hasLiteral()) return false;
			if (! arc.hasXRef()) return false;
			if (! XDIConstants.XS_VARIABLE.equals(arc.getXRef().getXs())) return false;
			if (! arc.getXRef().hasXDIAddress()) return false;
			if (arc.getXRef().hasPartialSubjectAndPredicate()) return false;
			if (arc.getXRef().hasLiteral()) return false;
			if (arc.getXRef().hasIri()) return false;
			if (arc.getXRef().getXDIAddress().getNumXDIArcs() != 1) return false;
			if (! XDIConstants.CS_MEMBER_UNORDERED.equals(arc.getXRef().getXDIAddress().getFirstXDIArc())) return false;
			if (arc.getXRef().getXDIAddress().getFirstXDIArc().isClassXs()) return false;
			if (arc.getXRef().getXDIAddress().getFirstXDIArc().isAttributeXs()) return false;
			if (! arc.getXRef().getXDIAddress().getFirstXDIArc().hasLiteral()) return false;
			if (arc.getXRef().getXDIAddress().getFirstXDIArc().hasXRef()) return false;
		}

		return true;
	}

	public static XDIArc createXDIArc(String identifier, Class<? extends XdiCollection<?, ?, ?, ?, ?, ?>> clazz) {

		if (XdiEntityCollection.class.isAssignableFrom(clazz)) {

			return XDIArc.create("" + XDIConstants.CS_MEMBER_UNORDERED + identifier);
		} else if (XdiAttributeCollection.class.isAssignableFrom(clazz)) {

			return XDIArc.create("" + XDIConstants.XS_ATTRIBUTE.charAt(0) + XDIConstants.CS_MEMBER_UNORDERED + identifier + XDIConstants.XS_ATTRIBUTE.charAt(1));
		} else if (XdiVariableCollection.class.isAssignableFrom(clazz)) {

			return XDIArc.create("" + XDIConstants.XS_VARIABLE.charAt(0) + XDIConstants.CS_MEMBER_UNORDERED + identifier + XDIConstants.XS_VARIABLE.charAt(1));
		} else {

			throw new IllegalArgumentException("Unknown class for unordered member " + clazz.getName());
		}
	}

	public static XDIArc createUuidXDIArc(String uuid, Class<? extends XdiCollection<?, ?, ?, ?, ?, ?>> clazz) {

		return createXDIArc(":uuid:" + uuid, clazz);
	}

	public static XDIArc createRandomUuidXDIArc(Class<? extends XdiCollection<?, ?, ?, ?, ?, ?>> clazz) {

		String uuid = UUID.randomUUID().toString().toLowerCase();

		return createUuidXDIArc(uuid, clazz);
	}

	public static XDIArc createDigestXDIArc(String string, String algorithm, Class<? extends XdiCollection<?, ?, ?, ?, ?, ?>> clazz) {

		byte[] output;

		try {

			MessageDigest digest = MessageDigest.getInstance(algorithm);
			digest.update(string.getBytes("UTF-8"));
			output = digest.digest();
		} catch (Exception ex) {

			throw new Xdi2RuntimeException(ex.getMessage(), ex);
		}

		String hex = new String(Hex.encodeHex(output));

		return createXDIArc(":" + algorithm.toLowerCase().replace("-", "") + ":" + hex, clazz);
	}

	public static XDIArc createDigestXDIArc(String string, Class<? extends XdiCollection<?, ?, ?, ?, ?, ?>> clazz) {

		return createDigestXDIArc(string, "SHA-512", clazz);
	}

	/*
	 * Helper classes
	 */

	public static class MappingContextNodeXdiMemberUnorderedIterator extends NotNullIterator<XdiMemberUnordered<?, ?, ?, ?, ?, ?>> {

		public MappingContextNodeXdiMemberUnorderedIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, XdiMemberUnordered<?, ?, ?, ?, ?, ?>> (contextNodes) {

				@Override
				public XdiMemberUnordered<?, ?, ?, ?, ?, ?> map(ContextNode contextNode) {

					return XdiAbstractMemberUnordered.fromContextNode(contextNode);
				}
			});
		}
	}
}
