package xdi2.core.features.keys;

import xdi2.core.LiteralNode;
import xdi2.core.constants.XDIConstants;
import xdi2.core.constants.XDISecurityConstants;
import xdi2.core.features.nodetypes.XdiAttribute;
import xdi2.core.features.nodetypes.XdiAttributeSingleton;
import xdi2.core.features.nodetypes.XdiContext;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;

public class Keys {

	private Keys() { }

	/*
	 * Gets keys
	 */

	/**
	 * Given a context, get the signature public key.
	 * @param graph The graph.
	 * @return The signature public key.
	 */
	public static String getSignaturePublicKey(XdiContext<?> xdiContext) {

		return getKey(xdiContext, XDISecurityConstants.XDI_ADD_MSG_SIG_KEYPAIR_PUBLIC_KEY);
	}

	/**
	 * Given a context, get the encryption public key.
	 * @param graph The graph.
	 * @return The encryption public key.
	 */
	public static String getEncryptionPublicKey(XdiContext<?> xdiContext) {

		return getKey(xdiContext, XDISecurityConstants.XDI_ADD_MSG_ENCRYPT_KEYPAIR_PUBLIC_KEY);
	}

	/**
	 * Given a context, get the signature private key.
	 * @param graph The graph.
	 * @return The signature private key.
	 */
	public static String getSignaturePrivateKey(XdiContext<?> xdiContext) {

		return getKey(xdiContext, XDISecurityConstants.XDI_ADD_MSG_SIG_KEYPAIR_PRIVATE_KEY);
	}

	/**
	 * Given a context, get the encryption private key.
	 * @param graph The graph.
	 * @return The encryption private key.
	 */
	public static String getEncryptionPrivateKey(XdiContext<?> xdiContext) {

		return getKey(xdiContext, XDISecurityConstants.XDI_ADD_MSG_ENCRYPT_KEYPAIR_PRIVATE_KEY);
	}

	/**
	 * Given a context, get the secret key.
	 * @param graph The graph.
	 * @return The signature secret key.
	 */
	public static String getSecretKey(XdiContext<?> xdiContext) {

		return getKey(xdiContext, XDISecurityConstants.XDI_ADD_SECRET_KEY);
	}

	/**
	 * Given a context, get a key.
	 * @param graph The graph.
	 * @return A key.
	 */
	public static String getKey(XdiContext<?> xdiContext, XDIAddress keyRelativeAddress) {

		// find public key

		XdiAttribute keyXdiAttribute = xdiContext.getXdiAttribute(keyRelativeAddress, false);
		keyXdiAttribute = keyXdiAttribute == null ? null : keyXdiAttribute.dereference();

		LiteralNode keyLiteral = keyXdiAttribute == null ? null : keyXdiAttribute.getLiteralNode();
		String key = keyLiteral == null ? null : keyLiteral.getLiteralDataString();

		// done

		return key;
	}

	/*
	 * Set keys
	 */

	/**
	 * Given a context, set the signature public key.
	 * @param graph The graph.
	 * @param The signature public key.
	 */
	public static void setSignaturePublicKey(XdiContext<?> xdiContext, String signaturePublicKey) {

		setKey(xdiContext, signaturePublicKey, XDISecurityConstants.XDI_ADD_MSG_SIG_KEYPAIR_PUBLIC_KEY);
	}

	/**
	 * Given a context, set the encryption public key.
	 * @param graph The graph.
	 * @param The signature public key.
	 */
	public static void setEncryptionPublicKey(XdiContext<?> xdiContext, String encryptionPublicKey) {

		setKey(xdiContext, encryptionPublicKey, XDISecurityConstants.XDI_ADD_MSG_ENCRYPT_KEYPAIR_PUBLIC_KEY);
	}

	/**
	 * Given a context, set the signature private key.
	 * @param graph The graph.
	 * @param The signature private key.
	 */
	public static void setSignaturePrivateKey(XdiContext<?> xdiContext, String signaturePrivateKey) {

		setKey(xdiContext, signaturePrivateKey, XDISecurityConstants.XDI_ADD_MSG_SIG_KEYPAIR_PRIVATE_KEY);
	}

	/**
	 * Given a context, set the encryption private key.
	 * @param graph The graph.
	 * @param The signature private key.
	 */
	public static void setEncryptionPrivateKey(XdiContext<?> xdiContext, String encryptionPrivateKey) {

		setKey(xdiContext, encryptionPrivateKey, XDISecurityConstants.XDI_ADD_MSG_ENCRYPT_KEYPAIR_PRIVATE_KEY);
	}

	/**
	 * Given a context, set the secret key.
	 * @param graph The graph.
	 * @param The secret key.
	 */
	public static void setSecretKey(XdiContext<?> xdiContext, String secretKey) {

		setKey(xdiContext, secretKey, XDISecurityConstants.XDI_ADD_SECRET_KEY);
	}

	/**
	 * Given a context, set a key.
	 * @param graph The graph.
	 * @param key A key.
	 */
	public static void setKey(XdiContext<?> xdiContext, String key, XDIAddress keyRelativeAddress) {

		// set public key

		XdiAttributeSingleton publicKeyXdiAttributeSingleton = xdiContext.getXdiAttributeSingleton(keyRelativeAddress, true);
		publicKeyXdiAttributeSingleton.setLiteralString(key);
	}

	/*
	 * Helper methods
	 */

	public static String getKeyAlgorithm(XDIAddress dataTypeXDIAddress) {

		XDIArc keyAlgorithmAddress = dataTypeXDIAddress.getNumXDIArcs() > 0 ? dataTypeXDIAddress.getXDIArc(0) : null;
		if (keyAlgorithmAddress == null) return null;

		if (! XDIConstants.CS_CLASS_RESERVED.equals(keyAlgorithmAddress.getCs())) return null;
		if (keyAlgorithmAddress.hasXRef()) return null;
		if (! keyAlgorithmAddress.hasLiteral()) return null;

		return keyAlgorithmAddress.getLiteral();
	}

	public static Integer getKeyLength(XDIAddress dataTypeXDIAddress) {

		XDIArc keyLengthAddress = dataTypeXDIAddress.getNumXDIArcs() > 1 ? dataTypeXDIAddress.getXDIArc(1) : null;
		if (keyLengthAddress == null) return null;

		if (! XDIConstants.CS_CLASS_RESERVED.equals(keyLengthAddress.getCs())) return null;
		if (keyLengthAddress.hasXRef()) return null;
		if (! keyLengthAddress.hasLiteral()) return null;

		return Integer.valueOf(keyLengthAddress.getLiteral());
	}
}
