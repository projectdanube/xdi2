package xdi2.messaging.target.contributor.impl.proxy.manipulator.impl.signing;

import java.security.PrivateKey;
import java.util.Map;

import xdi2.core.syntax.XDIAddress;
import xdi2.messaging.Message;

/**
 * A Signer that can create signatures on an XDI message using a
 * statically configured list of sender addresses and private keys.
 */
public class StaticSigner extends PrivateKeySigner {

	private Map<XDIAddress, PrivateKey> privateKeys;

	public StaticSigner(Map<XDIAddress, PrivateKey> privateKeys) {

		super();

		this.privateKeys = privateKeys;
	}

	public StaticSigner() {

		super();
	}

	@Override
	protected PrivateKey getPrivateKey(Message message) {

		XDIAddress senderAddress = message.getSenderAddress();
		if (senderAddress == null) return null;

		// look for static private key

		PrivateKey privateKey = this.getPrivateKeys().get(senderAddress);
		if (privateKey == null) return null;

		// done

		return privateKey;
	}

	/*
	 * Getters and setters
	 */
	
	public Map<XDIAddress, PrivateKey> getPrivateKeys() {

		return this.privateKeys;
	}

	public void setPrivateKeys(Map<XDIAddress, PrivateKey> privateKeys) {

		this.privateKeys = privateKeys;
	}
}