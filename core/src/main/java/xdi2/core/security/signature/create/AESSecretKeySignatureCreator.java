package xdi2.core.security.signature.create;

import java.nio.charset.Charset;
import java.security.GeneralSecurityException;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.features.signatures.AESSignature;
import xdi2.core.features.signatures.Signatures;
import xdi2.core.syntax.XDIAddress;

/**
 * This is an AESSignatureCreator that create an XDI AESSignature using a secret key,
 * which can be obtained using the XDI address that identifies the signer.
 */
public abstract class AESSecretKeySignatureCreator extends AbstractAESSignatureCreator implements AESSignatureCreator {

	private static Logger log = LoggerFactory.getLogger(AESSecretKeySignatureCreator.class.getName());

	public AESSecretKeySignatureCreator(String digestAlgorithm, Integer digestLength) {

		super(digestAlgorithm, digestLength);
	}

	public AESSecretKeySignatureCreator() {

	}

	@Override
	public AESSignature create(byte[] normalizedSerialization, ContextNode contextNode, XDIAddress signerXDIAddress) throws GeneralSecurityException {

		// obtain secret key

		SecretKey secretKey = this.getSecretKey(signerXDIAddress);

		if (secretKey == null) {

			if (log.isDebugEnabled()) log.debug("No private key found for " + signerXDIAddress);
			return null;
		}

		if (log.isDebugEnabled()) log.debug("Secret key found for " + signerXDIAddress + ".");

		// create signature

		AESSignature signature;

		try {

			signature = (AESSignature) Signatures.createSignature(
					contextNode,
					this.getDigestAlgorithm(), 
					this.getDigestVersion(), 
					getSecretKeyAlgorithm(secretKey), 
					getSecretKeyLength(secretKey), 
					true);
		} catch (Exception ex) {

			throw new GeneralSecurityException("Cannot create signature: " + ex.getMessage(), ex);
		}

		// set signature value

		this.setValue(normalizedSerialization, signature, secretKey);

		// done

		return signature;
	}

	@Override
	public void setValue(byte[] normalizedSerialization, AESSignature signature, XDIAddress signerXDIAddress) throws GeneralSecurityException {

		// obtain secret key

		SecretKey secretKey = this.getSecretKey(signerXDIAddress);

		if (secretKey == null) {

			throw new GeneralSecurityException("No secret key found for " + signerXDIAddress);
		}

		if (log.isDebugEnabled()) log.debug("Secret key found for " + signerXDIAddress + ".");

		// set signature value

		this.setValue(normalizedSerialization, signature, secretKey);
	}

	public void setValue(byte[] normalizedSerialization, AESSignature signature, SecretKey secretKey) throws GeneralSecurityException {

		// set signature value

		String jceAlgorithm = signature.getJCEAlgorithm();

		if (log.isDebugEnabled()) log.debug("Setting value for " + signature.getClass().getSimpleName() + " with algorithm " + jceAlgorithm);

		Mac jceMac = Mac.getInstance(jceAlgorithm);
		jceMac.init(secretKey);
		jceMac.update(normalizedSerialization);

		byte[] signatureValue = jceMac.doFinal();

		signature.setSignatureValue(signatureValue);
	}

	protected abstract SecretKey getSecretKey(XDIAddress signerXDIAddress) throws GeneralSecurityException;

	/*
	 * Helper methods
	 */

	public static SecretKey aesSecretKeyFromSecretKeyString(String secretKeyString) throws GeneralSecurityException {

		if (secretKeyString == null) return null;

		byte[] secretKeyBytes = Base64.decodeBase64(secretKeyString.getBytes(Charset.forName("UTF-8")));

		return new SecretKeySpec(secretKeyBytes, 0, secretKeyBytes.length, "AES");
	}

	public static String getSecretKeyAlgorithm(SecretKey secretKey) {

		return secretKey.getAlgorithm().toLowerCase();
	}

	public static Integer getSecretKeyLength(SecretKey secretKey) {

		return Integer.valueOf(secretKey.getEncoded().length * 8);
	}
}
