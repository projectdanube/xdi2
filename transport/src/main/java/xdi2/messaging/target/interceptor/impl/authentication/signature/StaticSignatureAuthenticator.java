package xdi2.messaging.target.interceptor.impl.authentication.signature;

import java.security.PublicKey;
import java.util.Map;

import xdi2.core.syntax.XDIAddress;
import xdi2.messaging.Message;

/**
 * A SignatureAuthenticator that can authenticate an XDI message using a
 * statically configured list of sender addresses and public keys.
 */
public class StaticSignatureAuthenticator extends PublicKeySignatureAuthenticator {

	private Map<XDIAddress, PublicKey> publicKeys;

	public StaticSignatureAuthenticator(Map<XDIAddress, PublicKey> publicKeys) {

		super();

		this.publicKeys = publicKeys;
	}

	public StaticSignatureAuthenticator() {

		super();
	}

	@Override
	protected PublicKey getPublicKey(Message message) {

		XDIAddress senderXDIAddress = message.getSenderXDIAddress();
		if (senderXDIAddress == null) return null;

		// look for static public key

		PublicKey publicKey = this.getPublicKeys().get(senderXDIAddress);
		if (publicKey == null) return null;

		// done

		return publicKey;
	}

	public Map<XDIAddress, PublicKey> getPublicKeys() {

		return this.publicKeys;
	}

	public void setPublicKeys(Map<XDIAddress, PublicKey> publicKeys) {

		this.publicKeys = publicKeys;
	}
}