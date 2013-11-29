package xdi2.messaging.target.contributor.impl.proxy.manipulator.impl.signing;

import java.security.PrivateKey;
import java.util.Map;

import xdi2.core.xri3.XDI3Segment;
import xdi2.messaging.Message;

/**
 * A Signer that can create signatures on an XDI message using a
 * statically configured list of sender addresses and private keys.
 */
public class StaticSigner extends PrivateKeySigner {

	private Map<XDI3Segment, PrivateKey> privateKeys;

	public StaticSigner(Map<XDI3Segment, PrivateKey> privateKeys) {

		super();

		this.privateKeys = privateKeys;
	}

	public StaticSigner() {

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

	/*
	 * Getters and setters
	 */
	
	public Map<XDI3Segment, PrivateKey> getPrivateKeys() {

		return this.privateKeys;
	}

	public void setPrivateKeys(Map<XDI3Segment, PrivateKey> privateKeys) {

		this.privateKeys = privateKeys;
	}
}