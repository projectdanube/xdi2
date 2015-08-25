package xdi2.core.security.validate;

import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.util.Arrays;

import javax.crypto.Mac;
import javax.crypto.SecretKey;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.features.signatures.AESSignature;
import xdi2.core.syntax.XDIAddress;

/**
 * This is an AESSignatureValidater that validate an XDI AESSignature using a secret key,
 * which can be obtained using the XDI address that identifies the signer.
 */
public abstract class AESSecretKeySignatureValidator extends AbstractAESSignatureValidator {

	private static Logger log = LoggerFactory.getLogger(AESSecretKeySignatureValidator.class.getName());

	public AESSecretKeySignatureValidator() {

	}

	@Override
	public boolean validate(byte[] normalizedSerialization, byte[] signatureValue, AESSignature signature, XDIAddress signerXDIAddress) throws GeneralSecurityException {

		// obtain secret key

		SecretKey secretKey = this.getSecretKey(signerXDIAddress);

		if (secretKey == null) {

			if (log.isDebugEnabled()) log.debug("No secret key found for " + signerXDIAddress);
			return false;
		}

		if (log.isDebugEnabled()) log.debug("Secret key found for " + signerXDIAddress + ": " + new String(Base64.encodeBase64(secretKey.getEncoded()), Charset.forName("UTF-8")));

		// validate

		String algorithm = signature.getAlgorithm();

		Mac mac = Mac.getInstance(algorithm);
		mac.init(secretKey);
		mac.update(normalizedSerialization);

		return Arrays.equals(signatureValue, mac.doFinal());
	}

	protected abstract SecretKey getSecretKey(XDIAddress signerXDIAddress) throws GeneralSecurityException;
}
