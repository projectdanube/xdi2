package xdi2.core.security.signature.validate;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.PublicKey;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.features.signatures.RSASignature;
import xdi2.core.syntax.XDIAddress;

/**
 * This is an RSASignatureValidator that validate an XDI RSASignature using a public key,
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

		if (log.isDebugEnabled()) log.debug("Public key found for " + signerXDIAddress + ": " + new String(Base64.encodeBase64(publicKey.getEncoded()), StandardCharsets.UTF_8));

		// validate

		String jceAlgorithm = signature.getJCEAlgorithm();

		if (log.isDebugEnabled()) log.debug("Validating for " + signature.getClass().getSimpleName() + " with algorithm " + jceAlgorithm);

		java.security.Signature jceSignature = java.security.Signature.getInstance(jceAlgorithm);
		jceSignature.initVerify(publicKey);
		jceSignature.update(normalizedSerialization);

		return jceSignature.verify(signatureValue);
	}

	protected abstract PublicKey getPublicKey(XDIAddress signerXDIAddress) throws GeneralSecurityException;
}
