package xdi2.messaging.target.interceptor.impl.authentication.signature;

import java.security.PublicKey;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.features.signatures.Signature;
import xdi2.messaging.Message;

/**
 * A SignatureAuthenticator that can authenticate a signature against
 * a public key.
 */
public abstract class PublicKeySignatureAuthenticator extends AbstractSignatureAuthenticator implements SignatureAuthenticator {

	private static Logger log = LoggerFactory.getLogger(PublicKeySignatureAuthenticator.class.getName());

	public PublicKeySignatureAuthenticator() {

	}

	@Override
	public boolean authenticate(Message message, Signature signature) {

		PublicKey publicKey = this.getPublicKey(message);

		if (publicKey == null) {
			
			if (log.isDebugEnabled()) log.debug("No public key found for sender " + message.getSenderXri());

			return false;
		}

		if (log.isDebugEnabled()) log.debug("Public key found for sender " + message.getSenderXri() + ": " + Base64.encodeBase64String(publicKey.getEncoded()));

		// authenticate
		
		boolean authenticated;
		
		try {

			authenticated = signature.validateSignature(publicKey);
		} catch (Exception ex) {

			if (log.isWarnEnabled()) log.warn("Cannot validate signature: " + ex.getMessage(), ex);

			return false;
		}

		return authenticated;
	}

	protected abstract PublicKey getPublicKey(Message message);
}
