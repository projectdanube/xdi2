package xdi2.messaging.target.interceptor.impl.authentication.signature;

import java.security.PublicKey;
import java.util.Map;

import xdi2.core.xri3.XDI3Segment;
import xdi2.messaging.Message;

/**
 * A SignatureAuthenticator that can authenticate an XDI message using a
 * statically configured list of sender addresses and public keys.
 */
public class StaticSignatureAuthenticator extends PublicKeySignatureAuthenticator {

	private Map<XDI3Segment, PublicKey> publicKeys;

	public StaticSignatureAuthenticator(Map<XDI3Segment, PublicKey> publicKeys) {

		super();

		this.publicKeys = publicKeys;
	}

	public StaticSignatureAuthenticator() {

		super();
	}

	@Override
	protected PublicKey getPublicKey(Message message) {

		XDI3Segment senderXri = message.getSenderXri();
		if (senderXri == null) return null;

		// look for static public key

		PublicKey publicKey = this.getPublicKeys().get(senderXri);
		if (publicKey == null) return null;

		// done

		return publicKey;
	}

	public Map<XDI3Segment, PublicKey> getPublicKeys() {

		return this.publicKeys;
	}

	public void setPublicKeys(Map<XDI3Segment, PublicKey> publicKeys) {

		this.publicKeys = publicKeys;
	}
}