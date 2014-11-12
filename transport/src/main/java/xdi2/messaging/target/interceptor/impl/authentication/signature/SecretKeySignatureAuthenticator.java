package xdi2.messaging.target.interceptor.impl.authentication.signature;

import javax.crypto.SecretKey;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.features.signatures.Signature;
import xdi2.core.features.signatures.SymmetricKeySignature;
import xdi2.messaging.Message;

/**
 * A SignatureAuthenticator that can authenticate a signature against
 * a secret key.
 */
public abstract class SecretKeySignatureAuthenticator extends AbstractSignatureAuthenticator implements SignatureAuthenticator {

	private static Logger log = LoggerFactory.getLogger(SecretKeySignatureAuthenticator.class.getName());

	public SecretKeySignatureAuthenticator() {

	}

	@Override
	public boolean authenticate(Message message, Signature<?, ?> signature) {

		// check signature type

		if (! (signature instanceof SymmetricKeySignature)) return false;

		// obtain secret key

		SecretKey secretKey = this.getSecretKey(message);

		if (secretKey == null) {

			if (log.isDebugEnabled()) log.debug("No secret key found for sender " + message.getSenderXDIAddress());

			return false;
		}

		if (log.isDebugEnabled()) log.debug("Secret key found for sender " + message.getSenderXDIAddress() + ": " + Base64.encodeBase64String(secretKey.getEncoded()));

		// authenticate

		boolean authenticated;

		try {

			authenticated = ((SymmetricKeySignature) signature).validate(secretKey);
		} catch (Exception ex) {

			if (log.isWarnEnabled()) log.warn("Cannot validate signature: " + ex.getMessage(), ex);

			return false;
		}

		return authenticated;
	}

	protected abstract SecretKey getSecretKey(Message message);
}
