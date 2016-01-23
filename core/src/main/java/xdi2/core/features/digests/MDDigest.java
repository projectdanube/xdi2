package xdi2.core.features.digests;

import xdi2.core.constants.XDISecurityConstants;
import xdi2.core.features.nodetypes.XdiAbstractContext;
import xdi2.core.features.nodetypes.XdiAttribute;
import xdi2.core.features.nodetypes.XdiAttributeInstance;
import xdi2.core.features.nodetypes.XdiAttributeSingleton;

/**
 * An XDI digest, represented as an XDI attribute.
 * 
 * @author markus
 */
public final class MDDigest extends Digest {

	private static final long serialVersionUID = 1161926167103700841L;

	public static final String DIGEST_ALGORITHM_MD = "md";

	protected MDDigest(XdiAttribute xdiAttribute) {

		super(xdiAttribute);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if an XDI attribute is a valid XDI digest.
	 * @param xdiAttribute The XDI attribute to check.
	 * @return True if the XDI attribute is a valid XDI digest.
	 */
	public static boolean isValid(XdiAttribute xdiAttribute) {

		if (xdiAttribute instanceof XdiAttributeSingleton) {

			if (! ((XdiAttributeSingleton) xdiAttribute).getBaseXDIArc().equals(XdiAbstractContext.getBaseXDIArc(XDISecurityConstants.XDI_ARC_DIGEST))) return false;
		} else if (xdiAttribute instanceof XdiAttributeInstance) {

			if (! ((XdiAttributeInstance) xdiAttribute).getXdiCollection().getBaseXDIArc().equals(XdiAbstractContext.getBaseXDIArc(XDISecurityConstants.XDI_ARC_DIGEST))) return false;
		} else {

			return false;
		}

		String digestAlgorithm = Digests.getDigestAlgorithm(xdiAttribute);

		if (! DIGEST_ALGORITHM_MD.equalsIgnoreCase(digestAlgorithm)) return false;

		return true;
	}

	/**
	 * Factory method that creates an XDI digest bound to a given XDI attribute.
	 * @param xdiAttribute The XDI attribute that is an XDI digest.
	 * @return The XDI digest.
	 */
	public static MDDigest fromXdiAttribute(XdiAttribute xdiAttribute) {

		if (! isValid(xdiAttribute)) return null;

		return new MDDigest(xdiAttribute);
	}

	/*
	 * Instance methods
	 */

	public String getJCEAlgorithm() {

		StringBuilder builder = new StringBuilder();

		builder.append(this.getDigestAlgorithm().toUpperCase());
		builder.append(this.getDigestVersion());

		return builder.toString();
	}
}
