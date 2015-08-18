package xdi2.core.security.sign;

import java.security.GeneralSecurityException;

import xdi2.core.ContextNode;
import xdi2.core.features.signatures.Signature;
import xdi2.core.syntax.XDIAddress;

/**
 * A SignatureValidater can create an XDI Signature using an XDI address
 * that identifies the signer.
 */
public interface SignatureCreator <SIGNATURE extends Signature> {

	/**
	 * Create a signature.
	 */
	public SIGNATURE createSignature(ContextNode contextNode, XDIAddress signerXDIAddress) throws GeneralSecurityException;

	/**
	 * Create a signature.
	 */
	public SIGNATURE createSignature(ContextNode contextNode) throws GeneralSecurityException;

	/**
	 * Set a signature value.
	 */
	public void setSignatureValue(SIGNATURE signature, XDIAddress signerXDIAddress) throws GeneralSecurityException;

	/**
	 * Set a signature value.
	 */
	public void setSignatureValue(SIGNATURE signature) throws GeneralSecurityException;
}
