package xdi2.core.security.sign;

import java.security.PrivateKey;
import java.util.Collections;
import java.util.Map;

import xdi2.core.syntax.XDIAddress;

/**
 * This is an RSAPrivateKeySignatureCreator that create an XDI RSASignature by
 * obtaining private keys from a statically configured list.
 */
public class RSAStaticPrivateKeySignatureCreator extends RSAPrivateKeySignatureCreator {

	private Map<XDIAddress, PrivateKey> privateKeys;

	public RSAStaticPrivateKeySignatureCreator(Map<XDIAddress, PrivateKey> privateKeys) {

		super();

		this.privateKeys = privateKeys;
	}

	public RSAStaticPrivateKeySignatureCreator(PrivateKey privateKey) {

		super();

		this.privateKeys = Collections.singletonMap(null, privateKey);
	}

	public RSAStaticPrivateKeySignatureCreator() {

		super();
	}

	@Override
	protected PrivateKey getPrivateKey(XDIAddress signerXDIAddress) {

		// find private key

		PrivateKey privateKey = this.getPrivateKeys().get(signerXDIAddress);
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

	public PrivateKey getPrivateKey() {

		return this.privateKeys.get(null);
	}

	public void setPrivateKey(PrivateKey privateKey) {

		this.privateKeys.put(null, privateKey);
	}
}