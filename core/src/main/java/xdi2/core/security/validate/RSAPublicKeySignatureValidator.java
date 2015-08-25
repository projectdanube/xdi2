package xdi2.core.security.validate;

import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.PublicKey;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.features.signatures.RSASignature;
import xdi2.core.syntax.XDIAddress;

/**
 * This is an RSASignatureValidater that validate an XDI RSASignature using a public key,
 * which can be obtained using the XDI address that identifies the signer.
 */
public abstract class RSAPublicKeySignatureValidator extends AbstractRSASignatureValidator implements RSASignatureValidator {

	private static Logger log = LoggerFactory.getLogger(RSAPublicKeySignatureValidator.class.getName());

	public RSAPublicKeySignatureValidator() {

	}

	@Override
	public boolean validate(byte[] normalizedSerialization, byte[] signatureValue, RSASignature signature, XDIAddress signerXDIAddress) throws GeneralSecurityException {

		// obtain public key

		PublicKey publicKey = this.getPublicKey(signerXDIAddress);

		if (publicKey == null) {

			if (log.isDebugEnabled()) log.debug("No public key found for " + signerXDIAddress);
			return false;
		}

		if (log.isDebugEnabled()) log.debug("Public key found for " + signerXDIAddress + ": " + new String(Base64.encodeBase64(publicKey.getEncoded()), Charset.forName("UTF-8")));

		// validate

		String algorithm = signature.getAlgorithm();

		java.security.Signature javaSignature = java.security.Signature.getInstance(algorithm);
		javaSignature.initVerify(publicKey);
		javaSignature.update(normalizedSerialization);

		return javaSignature.verify(signatureValue);
	}

	protected abstract PublicKey getPublicKey(XDIAddress signerXDIAddress) throws GeneralSecurityException;
}
