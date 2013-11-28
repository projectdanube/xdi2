package xdi2.messaging.target.contributor.impl.xdi.manipulator.impl.signature;

import java.security.PrivateKey;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.features.signatures.KeyPairSignature;
import xdi2.core.features.signatures.Signature;
import xdi2.messaging.Message;

/**
 * A SignatureCreator that can authenticate a signature against
 * a public key.
 */
public abstract class PrivateKeySignatureCreator extends AbstractSignatureCreator implements SignatureCreator {

	private static Logger log = LoggerFactory.getLogger(PrivateKeySignatureCreator.class.getName());

	public PrivateKeySignatureCreator() {

	}

	@Override
	public void createSignature(Message message, Signature<?, ?> signature) {

		// obtain private key

		PrivateKey privateKey = this.getPrivateKey(message);

		if (privateKey == null) {

			if (log.isWarnEnabled()) log.debug("No private key found for sender " + message.getSenderXri());

			return;
		}

		if (log.isDebugEnabled()) log.debug("Public key found for sender " + message.getSenderXri() + ": " + Base64.encodeBase64String(privateKey.getEncoded()));

		// create signature

		try {

			((KeyPairSignature) signature).sign(privateKey);
		} catch (Exception ex) {

			if (log.isWarnEnabled()) log.warn("Cannot create signature: " + ex.getMessage(), ex);

			return;
		}
	}

	protected abstract PrivateKey getPrivateKey(Message message);
}
