package xdi2.core.features.signatures;

import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.util.Arrays;

import javax.crypto.Mac;
import javax.crypto.SecretKey;

import org.apache.commons.codec.binary.Base64;

import xdi2.core.LiteralNode;
import xdi2.core.constants.XDIAuthenticationConstants;
import xdi2.core.features.nodetypes.XdiAbstractContext;
import xdi2.core.features.nodetypes.XdiAttribute;
import xdi2.core.features.nodetypes.XdiAttributeInstance;
import xdi2.core.features.nodetypes.XdiAttributeSingleton;
import xdi2.core.features.signatures.Signatures.NoSignaturesCopyStrategy;
import xdi2.core.io.Normalization;

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

			if (! ((XdiAttributeSingleton) xdiAttribute).getBaseXDIArc().equals(XdiAbstractContext.getBaseXDIArc(XDIAuthenticationConstants.XDI_ARC_SIGNATURE))) return false;
		} else if (xdiAttribute instanceof XdiAttributeInstance) {

			if (! ((XdiAttributeInstance) xdiAttribute).getXdiCollection().getBaseXDIArc().equals(XdiAbstractContext.getBaseXDIArc(XDIAuthenticationConstants.XDI_ARC_SIGNATURE))) return false;
		} else {

			return false;
		}

		String keyAlgorithm = Signatures.getKeyAlgorithm(xdiAttribute);
		String digestAlgorithm = Signatures.getDigestAlgorithm(xdiAttribute);

		if (! KEY_ALGORITHM_AES.equalsIgnoreCase(keyAlgorithm)) return false;
		if (! DIGEST_ALGORITHM_SHA.equalsIgnoreCase(digestAlgorithm)) return false;

		return true;
	}

	/**
	 * Factory method that creates an XDI signature bound to a given XDI attribute.
	 * @param xdiAttribute The XDI attribute that is an XDI signature.
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

			normalizedSerialization = Normalization.serialize(this.getBaseContextNode(), new NoSignaturesCopyStrategy()).getBytes(Charset.forName("UTF-8"));
		} catch (Exception ex) {

			throw new RuntimeException(ex.getMessage(), ex);
		}

		String algorithm = this.getAlgorithm();

		Mac mac = Mac.getInstance(algorithm);
		mac.init(secretKey);
		mac.update(normalizedSerialization);

		byte[] bytes = mac.doFinal();

		this.getXdiAttribute().setLiteralDataString(new String(Base64.encodeBase64(bytes), Charset.forName("UTF-8")));
	}

	@Override
	public boolean validate(SecretKey secretKey) throws GeneralSecurityException {

		LiteralNode literalNode = this.getXdiAttribute().getLiteralNode();
		if (literalNode == null) throw new GeneralSecurityException("No signature literal node.");

		String literalString = literalNode.getLiteralDataString();
		if (literalString == null) throw new GeneralSecurityException("No signature literal string.");

		byte[] bytes = Base64.decodeBase64(literalString.getBytes(Charset.forName("UTF-8")));

		byte[] normalizedSerialization;

		try {

			normalizedSerialization = Normalization.serialize(this.getBaseContextNode(), new NoSignaturesCopyStrategy()).getBytes(Charset.forName("UTF-8"));
		} catch (Exception ex) {

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
