package xdi2.messaging.target.contributor.impl.proxy.manipulator.impl.signing;

import java.security.PrivateKey;
import java.security.interfaces.RSAKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.features.signatures.KeyPairSignature;
import xdi2.core.features.signatures.Signature;
import xdi2.messaging.Message;

/**
 * A Signer that can authenticate a signature against
 * a public key.
 */
public abstract class PrivateKeySigner extends AbstractSigner implements Signer {

	private static Logger log = LoggerFactory.getLogger(PrivateKeySigner.class.getName());

	private String digestAlgorithm;
	private int digestLength;

	public PrivateKeySigner() {

	}

	@Override
	public Signature<?, ?> sign(Message message) {

		// obtain private key

		PrivateKey privateKey = this.getPrivateKey(message);

		if (privateKey == null) {

			if (log.isWarnEnabled()) log.warn("No private key found for sender " + message.getSenderXri());

			return null;
		}

		if (log.isDebugEnabled()) log.debug("Private key found for sender " + message.getSenderXri() + ".");

		// create signature

		Signature<?, ?> signature;

		try {

			signature = message.setSignature(this.getDigestAlgorithm(), this.getDigestLength(), privateKey.getAlgorithm(), 0);

			((KeyPairSignature) signature).sign(privateKey);
		} catch (Exception ex) {

			if (log.isWarnEnabled()) log.warn("Cannot create signature: " + ex.getMessage(), ex);

			return null;
		}

		// done

		return signature;
	}

	public String getPrivateKeyAlgorithm(PrivateKey privateKey) {
		
		return privateKey.getAlgorithm().toLowerCase();
	}
	
	public int getPrivateKeyLength(PrivateKey privateKey) {
		
		if (privateKey instanceof RSAKey) {
			
			return ((RSAKey) privateKey).getModulus().bitLength();
		}

		throw new RuntimeException("Cannot determine key length for private key.");
	}
	
	protected abstract PrivateKey getPrivateKey(Message message);

	/*
	 * Getters and setters
	 */

	public String getDigestAlgorithm() {

		return this.digestAlgorithm;
	}

	public void setDigestAlgorithm(String digestAlgorithm) {

		this.digestAlgorithm = digestAlgorithm;
	}

	public int getDigestLength() {

		return this.digestLength;
	}

	public void setDigestLength(int digestLength) {

		this.digestLength = digestLength;
	}
}
