package xdi2.core.features.keys;

import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

import xdi2.core.Literal;
import xdi2.core.constants.XDIAuthenticationConstants;
import xdi2.core.constants.XDIConstants;
import xdi2.core.features.nodetypes.XdiAttribute;
import xdi2.core.features.nodetypes.XdiContext;
import xdi2.core.features.nodetypes.XdiValue;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;

public class Keys {

	private Keys() { }

	/**
	 * Given a context, get the signature public key.
	 * @param graph The graph.
	 * @return The signature public key.
	 */
	public static PublicKey getSignaturePublicKey(XdiContext<?> xdiContext) throws GeneralSecurityException {

		return getPublicKey(xdiContext, XDIAuthenticationConstants.XDI_ADD_MSG_SIG_KEYPAIR_PUBLIC_KEY);
	}

	/**
	 * Given a context, get the encryption public key.
	 * @param graph The graph.
	 * @return The encryption public key.
	 */
	public static PublicKey getEncryptionPublicKey(XdiContext<?> xdiContext) throws GeneralSecurityException {

		return getPublicKey(xdiContext, XDIAuthenticationConstants.XDI_ADD_MSG_ENCRYPT_KEYPAIR_PUBLIC_KEY);
	}

	/**
	 * Given a context, get the signature private key.
	 * @param graph The graph.
	 * @return The signature private key.
	 */
	public static PrivateKey getSignaturePrivateKey(XdiContext<?> xdiContext) throws GeneralSecurityException {

		return getPrivateKey(xdiContext, XDIAuthenticationConstants.XDI_ADD_MSG_SIG_KEYPAIR_PRIVATE_KEY);
	}

	/**
	 * Given a context, get the encryption private key.
	 * @param graph The graph.
	 * @return The encryption private key.
	 */
	public static PrivateKey getEncryptionPrivateKey(XdiContext<?> xdiContext) throws GeneralSecurityException {

		return getPrivateKey(xdiContext, XDIAuthenticationConstants.XDI_ADD_MSG_ENCRYPT_KEYPAIR_PRIVATE_KEY);
	}

	/**
	 * Given a context, get the signature secret key.
	 * @param graph The graph.
	 * @return The signature secret key.
	 */
	public static SecretKey getSignatureSecretKey(XdiContext<?> xdiContext) throws GeneralSecurityException {

		return getSecretKey(xdiContext, XDIAuthenticationConstants.XDI_ADD_MSG_SIG_KEYPAIR_PUBLIC_KEY);
	}

	/**
	 * Given a context, get the encryption secret key.
	 * @param graph The graph.
	 * @return The encryption secret key.
	 */
	public static SecretKey getEncryptionSecretKey(XdiContext<?> xdiContext) throws GeneralSecurityException {

		return getSecretKey(xdiContext, XDIAuthenticationConstants.XDI_ADD_MSG_ENCRYPT_KEYPAIR_PUBLIC_KEY);
	}

	/**
	 * Given a context, get a public key.
	 * @param graph The graph.
	 * @return A public key.
	 */
	public static PublicKey getPublicKey(XdiContext<?> xdiContext, XDIAddress publicKeyRelativeAddress) throws GeneralSecurityException {

		// find public key

		XdiAttribute publicKeyXdiAttribute = xdiContext.getXdiAttribute(publicKeyRelativeAddress, false);
		publicKeyXdiAttribute = publicKeyXdiAttribute == null ? null : publicKeyXdiAttribute.dereference();

		XdiValue publicKeyXdiValue = publicKeyXdiAttribute == null ? null : publicKeyXdiAttribute.getXdiValue(false);
		publicKeyXdiValue = publicKeyXdiValue == null ? null : publicKeyXdiValue.dereference();

		Literal publicKeyLiteral = publicKeyXdiValue == null ? null : publicKeyXdiValue.getContextNode().getLiteral();
		PublicKey publicKey = publicKeyLiteral == null ? null : publicKeyFromPublicKeyString(publicKeyLiteral.getLiteralDataString());

		// done

		return publicKey;
	}

	/**
	 * Given a context, get a private key.
	 * @param graph The graph.
	 * @return A private key.
	 */
	public static PrivateKey getPrivateKey(XdiContext<?> xdiContext, XDIAddress privateKeyRelativeAddress) throws GeneralSecurityException {

		// find private key

		XdiAttribute privateKeyXdiAttribute = xdiContext.getXdiAttribute(privateKeyRelativeAddress, false);
		privateKeyXdiAttribute = privateKeyXdiAttribute == null ? null : privateKeyXdiAttribute.dereference();

		XdiValue privateKeyXdiValue = privateKeyXdiAttribute == null ? null : privateKeyXdiAttribute.getXdiValue(false);
		privateKeyXdiValue = privateKeyXdiValue == null ? null : privateKeyXdiValue.dereference();

		Literal privateKeyLiteral = privateKeyXdiValue == null ? null : privateKeyXdiValue.getContextNode().getLiteral();
		PrivateKey privateKey = privateKeyLiteral == null ? null : privateKeyFromPrivateKeyString(privateKeyLiteral.getLiteralDataString());

		// done

		return privateKey;
	}

	/**
	 * Given a context, get a secret key.
	 * @param graph The graph.
	 * @return A secret key.
	 */
	public static SecretKey getSecretKey(XdiContext<?> xdiContext, XDIAddress secretKeyRelativeAddress) throws GeneralSecurityException {

		// find secret key

		XdiAttribute secretKeyXdiAttribute = xdiContext.getXdiAttribute(secretKeyRelativeAddress, false);
		secretKeyXdiAttribute = secretKeyXdiAttribute == null ? null : secretKeyXdiAttribute.dereference();

		XdiValue secretKeyXdiValue = secretKeyXdiAttribute == null ? null : secretKeyXdiAttribute.getXdiValue(false);
		secretKeyXdiValue = secretKeyXdiValue == null ? null : secretKeyXdiValue.dereference();

		Literal secretKeyLiteral = secretKeyXdiValue == null ? null : secretKeyXdiValue.getContextNode().getLiteral();
		SecretKey secretKey = secretKeyLiteral == null ? null : secretKeyFromSecretKeyString(secretKeyLiteral.getLiteralDataString());

		// done

		return secretKey;
	}

	/*
	 * Helper methods
	 */

	public static PublicKey publicKeyFromPublicKeyString(String publicKeyString) throws GeneralSecurityException {

		if (publicKeyString == null) return null;

		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.decodeBase64(publicKeyString));
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");

		return keyFactory.generatePublic(keySpec);
	}

	public static PrivateKey privateKeyFromPrivateKeyString(String privateKeyString) throws GeneralSecurityException {

		if (privateKeyString == null) return null;

		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKeyString));
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");

		return keyFactory.generatePrivate(keySpec);
	}

	public static SecretKey secretKeyFromSecretKeyString(String secretKeyString) throws GeneralSecurityException {

		if (secretKeyString == null) return null;

		byte[] secretKeyBytes = Base64.decodeBase64(secretKeyString);

		return new SecretKeySpec(secretKeyBytes, 0, secretKeyBytes.length, "AES");
	}

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
