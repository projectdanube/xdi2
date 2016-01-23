package xdi2.core.security.signature.create;

import xdi2.core.features.signatures.RSASignature;

/**
 * This is a SignatureCreator that create an XDI RSASignature.
 */
public interface RSASignatureCreator extends SignatureCreator<RSASignature> {

	public String getDigestAlgorithm();
	public Integer getDigestVersion();
}
