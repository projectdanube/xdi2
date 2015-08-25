package xdi2.core.security.sign;

import xdi2.core.features.signatures.AESSignature;

/**
 * This is a SignatureCreator that create an XDI AESSignature.
 */
public interface AESSignatureCreator extends SignatureCreator<AESSignature> {

	public String getDigestAlgorithm();
	public Integer getDigestLength();
}
