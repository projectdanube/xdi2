package xdi2.core.features.encryption;

import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

import org.apache.commons.codec.binary.Base64;

import xdi2.core.Graph;
import xdi2.core.LiteralNode;
import xdi2.core.constants.XDIAuthenticationConstants;
import xdi2.core.features.encryption.Encryptions.NoEncryptionsCopyStrategy;
import xdi2.core.features.nodetypes.XdiAbstractContext;
import xdi2.core.features.nodetypes.XdiAttribute;
import xdi2.core.features.nodetypes.XdiAttributeMember;
import xdi2.core.features.nodetypes.XdiAttributeSingleton;
import xdi2.core.io.Normalization;
import xdi2.core.util.CopyUtil;

/**
 * An XDI encryption, represented as an XDI attribute.
 * 
 * @author markus
 */
public final class SymmetricKeyEncryption extends Encryption<SecretKey, SecretKey> {

	private static final long serialVersionUID = 8245580198671482543L;

	public static final String KEY_ALGORITHM_AES = "aes";

	protected SymmetricKeyEncryption(XdiAttribute xdiAttribute) {

		super(xdiAttribute);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if an XDI attribute is a valid XDI encryption.
	 * @param xdiAttribute The XDI attribute to check.
	 * @return True if the XDI attribute is a valid XDI encryption.
	 */
	public static boolean isValid(XdiAttribute xdiAttribute) {

		if (xdiAttribute instanceof XdiAttributeSingleton) {

			if (! ((XdiAttributeSingleton) xdiAttribute).getBaseXDIArc().equals(XdiAbstractContext.getBaseXDIArc(XDIAuthenticationConstants.XDI_ARC_ENCRYPTION))) return false;
		} else if (xdiAttribute instanceof XdiAttributeMember) {

			if (! ((XdiAttributeMember) xdiAttribute).getXdiCollection().getBaseXDIArc().equals(XdiAbstractContext.getBaseXDIArc(XDIAuthenticationConstants.XDI_ARC_ENCRYPTION))) return false;
		} else {

			return false;
		}

		String keyAlgorithm = Encryptions.getKeyAlgorithm(xdiAttribute);

		if (! KEY_ALGORITHM_AES.equalsIgnoreCase(keyAlgorithm)) return false;

		return true;
	}

	/**
	 * Factory method that creates an XDI encryption bound to a given XDI attribute.
	 * @param xdiAttribute The XDI attribute that is an XDI encryption.
	 * @return The XDI encryption.
	 */
	public static SymmetricKeyEncryption fromXdiAttribute(XdiAttribute xdiAttribute) {

		if (! isValid(xdiAttribute)) return null;

		return new SymmetricKeyEncryption(xdiAttribute);
	}

	/*
	 * Instance methods
	 */

	@Override
	public String getTransformation() {

		StringBuilder builder = new StringBuilder();

		builder.append(this.getKeyAlgorithm().toUpperCase());

		return builder.toString();
	}

	@Override
	public void encrypt(SecretKey secretKey) throws GeneralSecurityException {

		byte[] normalizedSerialization;

		try {

			normalizedSerialization = Normalization.serialize(this.getBaseContextNode(), new NoEncryptionsCopyStrategy()).getBytes("UTF-8");
		} catch (Exception ex) {

			throw new RuntimeException(ex.getMessage(), ex);
		}

		String transformation = this.getTransformation();

		Cipher cipher = Cipher.getInstance(transformation);
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);

		byte[] bytes = cipher.doFinal(normalizedSerialization);

		this.getXdiAttribute().setLiteralDataString(Base64.encodeBase64String(bytes));
	}

	@Override
	public void decrypt(SecretKey secretKey) throws GeneralSecurityException {

		LiteralNode literalNode = this.getXdiAttribute().getLiteralNode();
		if (literalNode == null) throw new GeneralSecurityException("No encryption literal node.");

		String literalString = literalNode.getLiteralDataString();
		if (literalString == null) throw new GeneralSecurityException("No encryption literal string.");

		byte[] bytes = Base64.decodeBase64(literalString);

		String transformation = this.getTransformation();

		Cipher cipher = Cipher.getInstance(transformation);
		cipher.init(Cipher.DECRYPT_MODE, secretKey);

		byte[] normalizedSerialization = cipher.doFinal(bytes);

		Graph tempGraph;

		try {

			tempGraph = Normalization.deserialize(new String(normalizedSerialization, "UTF-8"));
		} catch (Exception ex) {

			throw new RuntimeException(ex.getMessage(), ex);
		}

		CopyUtil.copyGraph(tempGraph, this.getBaseContextNode().getGraph(), null);
	}
}
