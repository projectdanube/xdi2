package xdi2.core.security.signature.create;

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

	public RSAStaticPrivateKeySignatureCreator(String digestAlgorithm, Integer digestLength, Map<XDIAddress, PrivateKey> privateKeys) {

		super(digestAlgorithm, digestLength);

		this.privateKeys = privateKeys;
	}

	public RSAStaticPrivateKeySignatureCreator(String digestAlgorithm, Integer digestLength, PrivateKey privateKey) {

		super(digestAlgorithm, digestLength);

		this.privateKeys = Collections.singletonMap(null, privateKey);
	}

	public RSAStaticPrivateKeySignatureCreator(String digestAlgorithm, Integer digestLength) {

		super(digestAlgorithm, digestLength);

		this.privateKeys = Collections.emptyMap();
	}

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

		this.privateKeys = Collections.emptyMap();
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