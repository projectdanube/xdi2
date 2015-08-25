package xdi2.core.features.signatures;

import xdi2.core.constants.XDIAuthenticationConstants;
import xdi2.core.features.nodetypes.XdiAbstractContext;
import xdi2.core.features.nodetypes.XdiAttribute;
import xdi2.core.features.nodetypes.XdiAttributeInstance;
import xdi2.core.features.nodetypes.XdiAttributeSingleton;

/**
 * An XDI signature, represented as an XDI attribute.
 * 
 * @author markus
 */
public final class RSASignature extends Signature {

	private static final long serialVersionUID = 5144184647292934979L;

	public static final String KEY_ALGORITHM_RSA = "rsa";
	public static final String KEY_ALGORITHM_DSA = "dsa";

	public static final String DIGEST_ALGORITHM_SHA = "sha";

	protected RSASignature(XdiAttribute xdiAttribute) {

		super(xdiAttribute);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if an XDI attribute is a valid XDI signature.
	 * @param xdiAttribute The XDI attribute to check.
	 * @return True if the XDI attribute is a valid XDI signature.
	 */
	public static boolean isValid(XdiAttribute xdiAttribute) {

		if (xdiAttribute instanceof XdiAttributeSingleton) {

			if (! ((XdiAttributeSingleton) xdiAttribute).getBaseXDIArc().equals(XdiAbstractContext.getBaseXDIArc(XDIAuthenticationConstants.XDI_ARC_SIGNATURE))) return false;
		} else if (xdiAttribute instanceof XdiAttributeInstance) {

			if (! ((XdiAttributeInstance) xdiAttribute).getXdiCollection().getBaseXDIArc().equals(XdiAbstractContext.getBaseXDIArc(XDIAuthenticationConstants.XDI_ARC_SIGNATURE))) return false;
		} else {

			return false;
		}

		String keyAlgorithm = Signatures.getKeyAlgorithm(xdiAttribute);
		String digestAlgorithm = Signatures.getDigestAlgorithm(xdiAttribute);

		if (! KEY_ALGORITHM_RSA.equalsIgnoreCase(keyAlgorithm) && ! KEY_ALGORITHM_DSA.equalsIgnoreCase(keyAlgorithm)) return false;
		if (! DIGEST_ALGORITHM_SHA.equalsIgnoreCase(digestAlgorithm)) return false;

		return true;
	}

	/**
	 * Factory method that creates an XDI signature bound to a given XDI attribute.
	 * @param xdiAttribute The XDI attribute that is an XDI signature.
	 * @return The XDI signature.
	 */
	public static RSASignature fromXdiAttribute(XdiAttribute xdiAttribute) {

		if (! isValid(xdiAttribute)) return null;

		return new RSASignature(xdiAttribute);
	}

	/*
	 * Instance methods
	 */

	public String getAlgorithm() {

		StringBuilder builder = new StringBuilder();

		builder.append(this.getDigestAlgorithm().toUpperCase());
		builder.append(this.getDigestLength());
		builder.append("with");
		builder.append(this.getKeyAlgorithm().toUpperCase());

		return builder.toString();
	}
}
