package xdi2.core.features.signatures;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.Arrays;

import javax.crypto.Mac;
import javax.crypto.SecretKey;

import org.apache.commons.codec.binary.Base64;

import xdi2.core.Literal;
import xdi2.core.constants.XDIAuthenticationConstants;
import xdi2.core.features.nodetypes.XdiAbstractContext;
import xdi2.core.features.nodetypes.XdiAttribute;
import xdi2.core.features.nodetypes.XdiAttributeMember;
import xdi2.core.features.nodetypes.XdiAttributeSingleton;
import xdi2.core.features.nodetypes.XdiValue;

/**
 * An XDI signature, represented as an XDI attribute.
 * 
 * @author markus
 */
public final class SymmetricKeySignature extends Signature<SecretKey, SecretKey> {

	private static final long serialVersionUID = 421543866460513859L;

	public static final String KEY_ALGORITHM_AES = "aes";

	public static final String DIGEST_ALGORITHM_SHA = "sha";

	protected SymmetricKeySignature(XdiAttribute xdiAttribute) {

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

			if (! ((XdiAttributeSingleton) xdiAttribute).getBaseArcXri().equals(XdiAbstractContext.getBaseArcXri(XDIAuthenticationConstants.XRI_SS_SIGNATURE))) return false;
		} else if (xdiAttribute instanceof XdiAttributeMember) {

			if (! ((XdiAttributeMember) xdiAttribute).getXdiCollection().getBaseArcXri().equals(XdiAbstractContext.getBaseArcXri(XDIAuthenticationConstants.XRI_SS_SIGNATURE))) return false;
		} else {

			return false;
		}

		if (! KEY_ALGORITHM_AES.equalsIgnoreCase(getKeyAlgorithm(xdiAttribute))) return false;

		if (! DIGEST_ALGORITHM_SHA.equalsIgnoreCase(getDigestAlgorithm(xdiAttribute))) return false;

		return true;
	}

	/**
	 * Factory method that creates an XDI signature bound to a given XDI attribute.
	 * @param xdiAttribute The XDI signature that is an XDI signature.
	 * @return The XDI signature.
	 */
	public static SymmetricKeySignature fromXdiAttribute(XdiAttribute xdiAttribute) {

		if (! isValid(xdiAttribute)) return null;

		return new SymmetricKeySignature(xdiAttribute);
	}

	/*
	 * Instance methods
	 */

	@Override
	public String getAlgorithm() {

		StringBuilder builder = new StringBuilder();

		builder.append("Hmac");
		builder.append(this.getDigestAlgorithm().toUpperCase());
		builder.append(this.getDigestLength());

		return builder.toString();
	}

	@Override
	public void sign(SecretKey secretKey) throws GeneralSecurityException {

		byte[] normalizedSerialization;

		try {

			normalizedSerialization = getNormalizedSerialization(this.getBaseContextNode()).getBytes("UTF-8");
		} catch (UnsupportedEncodingException ex) {

			throw new RuntimeException(ex.getMessage(), ex);
		}

		String algorithm = this.getAlgorithm();

		Mac mac = Mac.getInstance(algorithm);
		mac.init(secretKey);
		mac.update(normalizedSerialization);

		byte[] bytes = mac.doFinal();

		this.getXdiAttribute().getXdiValue(true).getContextNode().setLiteralString(Base64.encodeBase64String(bytes));
	}

	@Override
	public boolean validate(SecretKey secretKey) throws GeneralSecurityException {

		XdiValue xdiValue = this.getXdiAttribute().getXdiValue(false);
		if (xdiValue == null) return false;

		Literal literal = xdiValue.getContextNode().getLiteral();
		if (literal == null) return false;

		String literalString = literal.getLiteralDataString();
		if (literalString == null) return false;

		byte[] bytes = Base64.decodeBase64(literalString);

		byte[] normalizedSerialization;

		try {

			normalizedSerialization = getNormalizedSerialization(this.getBaseContextNode()).getBytes("UTF-8");
		} catch (UnsupportedEncodingException ex) {

			throw new RuntimeException(ex.getMessage(), ex);
		}

		String algorithm = this.getAlgorithm();

		Mac mac = Mac.getInstance(algorithm);
		mac.init(secretKey);
		mac.update(normalizedSerialization);

		boolean verify = Arrays.equals(bytes, mac.doFinal());

		return verify;
	}
}
