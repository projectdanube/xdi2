package xdi2.core.features.nodetypes;

import java.security.MessageDigest;
import java.util.Iterator;
import java.util.UUID;

import org.apache.commons.codec.binary.Hex;

import xdi2.core.ContextNode;
import xdi2.core.constants.XDIConstants;
import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.util.GraphUtil;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;

public abstract class XdiAbstractInstanceUnordered<EQC extends XdiCollection<EQC, EQI, C, U, O, I>, EQI extends XdiSubGraph<EQI>, C extends XdiCollection<EQC, EQI, C, U, O, I>, U extends XdiMemberUnordered<EQC, EQI, C, U, O, I>, O extends XdiMemberOrdered<EQC, EQI, C, U, O, I>, I extends XdiMember<EQC, EQI, C, U, O, I>> extends XdiAbstractInstance<EQC, EQI, C, U, O, I> implements XdiMemberUnordered<EQC, EQI, C, U, O, I> {

	private static final long serialVersionUID = -8496645644143069191L;

	protected XdiAbstractInstanceUnordered(ContextNode contextNode) {

		super(contextNode);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI unordered member.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI unordered member.
	 */
	public static boolean isValid(ContextNode contextNode) {

		if (contextNode == null) throw new NullPointerException();

		if (XdiEntityInstanceUnordered.isValid(contextNode)) return true;
		if (XdiAttributeInstanceUnordered.isValid(contextNode)) return true;

		return false;
	}

	/**
	 * Factory method that creates an XDI unordered member bound to a given context node.
	 * @param contextNode The context node that is an XDI unordered member.
	 * @return The XDI unordered member.
	 */
	public static XdiMemberUnordered<?, ?, ?, ?, ?, ?> fromContextNode(ContextNode contextNode) {

		if (contextNode == null) throw new NullPointerException();

		XdiMemberUnordered<?, ?, ?, ?, ?, ?> xdiMember;

		if ((xdiMember = XdiEntityInstanceUnordered.fromContextNode(contextNode)) != null) return xdiMember;
		if ((xdiMember = XdiAttributeInstanceUnordered.fromContextNode(contextNode)) != null) return xdiMember;

		return null;
	}

	public static XdiMemberUnordered<?, ?, ?, ?, ?, ?> fromXDIAddress(XDIAddress XDIaddress) {

		return fromContextNode(GraphUtil.contextNodeFromComponents(XDIaddress));
	}

	/*
	 * Methods for arcs
	 */

	public static XDIArc createXDIArc(String identifier, boolean immutable, boolean relative, Class<? extends XdiCollection<?, ?, ?, ?, ?, ?>> clazz) {

		if (XdiEntityCollection.class.isAssignableFrom(clazz)) {

			return XDIArc.create("" + XDIConstants.CS_INSTANCE_UNORDERED + (immutable ? XDIConstants.S_IMMUTABLE : "") + (relative ? XDIConstants.S_RELATIVE : "") + identifier);
		} else if (XdiAttributeCollection.class.isAssignableFrom(clazz)) {

			return XDIArc.create("" + XDIConstants.XS_ATTRIBUTE.charAt(0) + XDIConstants.CS_INSTANCE_UNORDERED + (immutable ? XDIConstants.S_IMMUTABLE : "") + (relative ? XDIConstants.S_RELATIVE : "") + identifier + XDIConstants.XS_ATTRIBUTE.charAt(1));
		} else {

			throw new IllegalArgumentException("Unknown class for unordered member " + clazz.getName());
		}
	}

	public static boolean isValidXDIArc(XDIArc XDIarc, Class<? extends XdiCollection<?, ?, ?, ?, ?, ?>> clazz) {

		if (XDIarc == null) throw new NullPointerException();

		if (XdiEntityCollection.class.isAssignableFrom(clazz)) {

			if (! XDIConstants.CS_INSTANCE_UNORDERED.equals(XDIarc.getCs())) return false;
			if (XDIarc.isCollection()) return false;
			if (XDIarc.isAttribute()) return false;
			if (! XDIarc.hasLiteral()) return false;
			if (XDIarc.hasXRef()) return false;
		} else if (XdiAttributeCollection.class.isAssignableFrom(clazz)) {

			if (! XDIConstants.CS_INSTANCE_UNORDERED.equals(XDIarc.getCs())) return false;
			if (XDIarc.isCollection()) return false;
			if (! XDIarc.isAttribute()) return false;
			if (! XDIarc.hasLiteral()) return false;
			if (XDIarc.hasXRef()) return false;
		} else {

			throw new IllegalArgumentException("Unknown class for unordered member " + clazz.getName());
		}

		return true;
	}

	public static XDIArc createUuidXDIArc(String uuid, boolean immutable, boolean relative, Class<? extends XdiCollection<?, ?, ?, ?, ?, ?>> clazz) {

		return createXDIArc(":uuid:" + uuid, immutable, relative, clazz);
	}

	public static XDIArc createRandomUuidXDIArc(boolean immutable, boolean relative, Class<? extends XdiCollection<?, ?, ?, ?, ?, ?>> clazz) {

		String uuid = UUID.randomUUID().toString().toLowerCase();

		return createUuidXDIArc(uuid, immutable, relative, clazz);
	}

	public static XDIArc createDigestXDIArc(String string, String algorithm, boolean immutable, boolean relative, Class<? extends XdiCollection<?, ?, ?, ?, ?, ?>> clazz) {

		byte[] output;

		try {

			MessageDigest digest = MessageDigest.getInstance(algorithm);
			digest.update(string.getBytes("UTF-8"));
			output = digest.digest();
		} catch (Exception ex) {

			throw new Xdi2RuntimeException(ex.getMessage(), ex);
		}

		String hex = new String(Hex.encodeHex(output));

		return createXDIArc(":" + algorithm.toLowerCase().replace("-", "") + ":" + hex, immutable, relative, clazz);
	}

	public static XDIArc createDigestXDIArc(String string, boolean immutable, boolean relative, Class<? extends XdiCollection<?, ?, ?, ?, ?, ?>> clazz) {

		return createDigestXDIArc(string, "SHA-512", immutable, relative, clazz);
	}

	/*
	 * Helper classes
	 */

	public static class MappingContextNodeXdiMemberUnorderedIterator extends NotNullIterator<XdiMemberUnordered<?, ?, ?, ?, ?, ?>> {

		public MappingContextNodeXdiMemberUnorderedIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, XdiMemberUnordered<?, ?, ?, ?, ?, ?>> (contextNodes) {

				@Override
				public XdiMemberUnordered<?, ?, ?, ?, ?, ?> map(ContextNode contextNode) {

					return XdiAbstractInstanceUnordered.fromContextNode(contextNode);
				}
			});
		}
	}
}
