package xdi2.core.security.signature.create;

import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.interfaces.RSAKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.features.signatures.RSASignature;
import xdi2.core.features.signatures.Signatures;
import xdi2.core.syntax.XDIAddress;

/**
 * This is an RSASignatureCreator that create an XDI RSASignature using a private key,
 * which can be obtained using the XDI address that identifies the signer.
 */
public abstract class RSAPrivateKeySignatureCreator extends AbstractRSASignatureCreator implements RSASignatureCreator {

	private static Logger log = LoggerFactory.getLogger(RSAPrivateKeySignatureCreator.class.getName());

	public RSAPrivateKeySignatureCreator(String digestAlgorithm, Integer digestLength) {

		super(digestAlgorithm, digestLength);
	}

	public RSAPrivateKeySignatureCreator() {

		super();
	}

	@Override
	public RSASignature create(byte[] normalizedSerialization, ContextNode contextNode, XDIAddress signerXDIAddress) throws GeneralSecurityException {

		// obtain private key

		PrivateKey privateKey = this.getPrivateKey(signerXDIAddress);

		if (privateKey == null) {

			if (log.isDebugEnabled()) log.debug("No private key found for " + signerXDIAddress);
			return null;
		}

		if (log.isDebugEnabled()) log.debug("Private key found for " + signerXDIAddress + ".");

		// create signature

		RSASignature signature;

		try {

			signature = (RSASignature) Signatures.createSignature(
					contextNode,
					this.getDigestAlgorithm(), 
					this.getDigestVersion(), 
					getPrivateKeyAlgorithm(privateKey), 
					getPrivateKeyLength(privateKey), 
					true);
		} catch (Exception ex) {

			throw new GeneralSecurityException("Cannot create signature: " + ex.getMessage(), ex);
		}

		// set signature value

		this.setValue(normalizedSerialization, signature, privateKey);

		// done

		return signature;
	}

	@Override
	public void setValue(byte[] normalizedSerialization, RSASignature signature, XDIAddress signerXDIAddress) throws GeneralSecurityException {

		// obtain private key

		PrivateKey privateKey = this.getPrivateKey(signerXDIAddress);

		if (privateKey == null) {

			throw new GeneralSecurityException("No private key found for " + signerXDIAddress);
		}

		if (log.isDebugEnabled()) log.debug("Private key found for " + signerXDIAddress + ".");

		// set signature value

		this.setValue(normalizedSerialization, signature, privateKey);
	}

	public void setValue(byte[] normalizedSerialization, RSASignature signature, PrivateKey privateKey) throws GeneralSecurityException {

		// set signature value

		String jceAlgorithm = signature.getJCEAlgorithm();

		if (log.isDebugEnabled()) log.debug("Setting value for " + signature.getClass().getSimpleName() + " with algorithm " + jceAlgorithm);

		java.security.Signature jceSignature = java.security.Signature.getInstance(jceAlgorithm);
		jceSignature.initSign(privateKey);
		jceSignature.update(normalizedSerialization);

		byte[] signatureValue = jceSignature.sign();

		signature.setSignatureValue(signatureValue);
	}

	protected abstract PrivateKey getPrivateKey(XDIAddress signerXDIAddress) throws GeneralSecurityException;

	/*
	 * Helper methods
	 */

	public static String getPrivateKeyAlgorithm(PrivateKey privateKey) {

		return privateKey.getAlgorithm().toLowerCase();
	}

	public static Integer getPrivateKeyLength(PrivateKey privateKey) {

		if (privateKey instanceof RSAKey) {

			return Integer.valueOf(((RSAKey) privateKey).getModulus().bitLength());
		}

		throw new IllegalArgumentException("Cannot determine key length for private key.");
	}
}
