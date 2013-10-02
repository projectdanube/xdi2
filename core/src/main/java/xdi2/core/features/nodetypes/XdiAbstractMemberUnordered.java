package xdi2.core.features.nodetypes;

import java.security.MessageDigest;
import java.util.Iterator;
import java.util.UUID;

import org.apache.commons.codec.binary.Hex;

import xdi2.core.ContextNode;
import xdi2.core.constants.XDIConstants;
import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;
import xdi2.core.xri3.XDI3SubSegment;

public abstract class XdiAbstractMemberUnordered<EQ extends XdiSubGraph<EQ>, C extends XdiCollection<EQ, C, U, O, I>, U extends XdiMemberUnordered<EQ, C, U, O, I>, O extends XdiMemberOrdered<EQ, C, U, O, I>, I extends XdiMember<EQ, C, U, O, I>> extends XdiAbstractMember<EQ, C, U, O, I> implements XdiMemberUnordered<EQ, C, U, O, I> {

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
	public static XdiMemberUnordered<?, ?, ?, ?, ?> fromContextNode(ContextNode contextNode) {

		XdiMemberUnordered<?, ?, ?, ?, ?> xdiMember;

		if ((xdiMember = XdiEntityMemberUnordered.fromContextNode(contextNode)) != null) return xdiMember;
		if ((xdiMember = XdiAttributeMemberUnordered.fromContextNode(contextNode)) != null) return xdiMember;

		return null;
	}

	/*
	 * Methods for XRIs
	 */

	public static XDI3SubSegment createArcXri(String identifier, boolean attribute) {

		return XDI3SubSegment.create("" + (attribute ? Character.valueOf(XDIConstants.XS_ATTRIBUTE.charAt(0)) : "") + XDIConstants.CS_BANG + identifier + (attribute ? Character.valueOf(XDIConstants.XS_ATTRIBUTE.charAt(1)) : ""));
	}

	public static XDI3SubSegment createArcXriFromUuid(String uuid, boolean attribute) {

		return createArcXri(":uuid:" + uuid, attribute);
	}

	public static XDI3SubSegment createArcXriFromRandom(boolean attribute) {

		String uuid = UUID.randomUUID().toString();

		return createArcXriFromUuid(uuid, attribute);
	}

	public static XDI3SubSegment createArcXriFromHash(String string, boolean attribute) {

		byte[] output;

		try {

			MessageDigest digest = MessageDigest.getInstance("SHA-512");
			digest.update(string.getBytes("UTF-8"));
			output = digest.digest();
		} catch (Exception ex) {

			throw new Xdi2RuntimeException(ex.getMessage(), ex);
		}

		String hex = new String(Hex.encodeHex(output));

		return createArcXri(":sha512:" + hex, attribute);
	}

	public static boolean isValidArcXri(XDI3SubSegment arcXri, boolean attribute) {

		if (arcXri == null) return false;

		if (arcXri.isClassXs()) return false;
		if (attribute && ! arcXri.isAttributeXs()) return false;
		if (! attribute && arcXri.isAttributeXs()) return false;
		if (arcXri.hasXRef()) return false;

		if (! XDIConstants.CS_BANG.equals(arcXri.getCs())) return false;

		if (! arcXri.hasLiteral()) return false;

		return true;
	}

	/*
	 * Helper classes
	 */

	public static class MappingContextNodeXdiMemberUnorderedIterator extends NotNullIterator<XdiMemberUnordered<?, ?, ?, ?, ?>> {

		public MappingContextNodeXdiMemberUnorderedIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, XdiMemberUnordered<?, ?, ?, ?, ?>> (contextNodes) {

				@Override
				public XdiMemberUnordered<?, ?, ?, ?, ?> map(ContextNode contextNode) {

					return XdiAbstractMemberUnordered.fromContextNode(contextNode);
				}
			});
		}
	}
}
