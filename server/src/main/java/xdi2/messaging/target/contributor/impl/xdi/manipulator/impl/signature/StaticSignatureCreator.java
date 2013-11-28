package xdi2.messaging.target.contributor.impl.xdi.manipulator.impl.signature;

import java.security.PrivateKey;
import java.util.Map;

import xdi2.core.xri3.XDI3Segment;
import xdi2.messaging.Message;

/**
 * A SignatureCreator that can create signatures on an XDI message using a
 * statically configured list of sender addresses and private keys.
 */
public class StaticSignatureCreator extends PrivateKeySignatureCreator {

	private Map<XDI3Segment, PrivateKey> privateKeys;

	public StaticSignatureCreator(Map<XDI3Segment, PrivateKey> privateKeys) {

		super();

		this.privateKeys = privateKeys;
	}

	public StaticSignatureCreator() {

		super();
	}

	@Override
	protected PrivateKey getPrivateKey(Message message) {

		XDI3Segment senderXri = message.getSenderXri();
		if (senderXri == null) return null;

		// look for static private key

		PrivateKey privateKey = this.getPrivateKeys().get(senderXri);
		if (privateKey == null) return null;

		// done

		return privateKey;
	}

	public Map<XDI3Segment, PrivateKey> getPrivateKeys() {

		return this.privateKeys;
	}

	public void setPrivateKeys(Map<XDI3Segment, PrivateKey> privateKeys) {

		this.privateKeys = privateKeys;
	}
}