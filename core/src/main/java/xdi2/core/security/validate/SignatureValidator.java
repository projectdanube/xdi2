package xdi2.core.security.validate;

import java.security.GeneralSecurityException;

import xdi2.core.features.signatures.Signature;
import xdi2.core.syntax.XDIAddress;

/**
 * A SignatureValidater can validate an XDI Signature using an XDI address
 * that identifies the signer.
 */
public interface SignatureValidator <SIGNATURE extends Signature> {

	/**
	 * Validate a signature.
	 */
	public boolean validateSignature(SIGNATURE signature, XDIAddress signerXDIAddress) throws GeneralSecurityException;

	/**
	 * Validate a signature.
	 */
	public boolean validateSignature(SIGNATURE signature) throws GeneralSecurityException;
}
