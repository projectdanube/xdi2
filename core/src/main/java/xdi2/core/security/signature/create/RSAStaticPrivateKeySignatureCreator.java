package xdi2.core.security.signature.create;

import java.security.interfaces.RSAPrivateKey;
import java.util.Collections;
import java.util.Map;

import xdi2.core.syntax.XDIAddress;

/**
 * This is an RSAPrivateKeySignatureCreator that create an XDI RSASignature by
 * obtaining private keys from a statically configured list.
 */
public class RSAStaticPrivateKeySignatureCreator extends RSAPrivateKeySignatureCreator {

	private Map<XDIAddress, RSAPrivateKey> privateKeys;

	public RSAStaticPrivateKeySignatureCreator(String digestAlgorithm, Integer digestLength, Map<XDIAddress, RSAPrivateKey> privateKeys) {

		super(digestAlgorithm, digestLength);

		this.privateKeys = privateKeys;
	}

	public RSAStaticPrivateKeySignatureCreator(String digestAlgorithm, Integer digestLength, RSAPrivateKey privateKey) {

		super(digestAlgorithm, digestLength);

		this.privateKeys = Collections.singletonMap(null, privateKey);
	}

	public RSAStaticPrivateKeySignatureCreator(String digestAlgorithm, Integer digestLength) {

		super(digestAlgorithm, digestLength);

		this.privateKeys = Collections.emptyMap();
	}

	public RSAStaticPrivateKeySignatureCreator(Map<XDIAddress, RSAPrivateKey> privateKeys) {

		super();

		this.privateKeys = privateKeys;
	}

	public RSAStaticPrivateKeySignatureCreator(RSAPrivateKey privateKey) {

		super();

		this.privateKeys = Collections.singletonMap(null, privateKey);
	}

	public RSAStaticPrivateKeySignatureCreator() {

		super();

		this.privateKeys = Collections.emptyMap();
	}

	@Override
	protected RSAPrivateKey getPrivateKey(XDIAddress signerXDIAddress) {

		// find private key

		RSAPrivateKey privateKey = this.getPrivateKeys().get(signerXDIAddress);
		if (privateKey == null) return null;

		// done

		return privateKey;
	}

	/*
	 * Getters and setters
	 */

	public Map<XDIAddress, RSAPrivateKey> getPrivateKeys() {

		return this.privateKeys;
	}

	public void setPrivateKeys(Map<XDIAddress, RSAPrivateKey> privateKeys) {

		this.privateKeys = privateKeys;
	}

	public RSAPrivateKey getPrivateKey() {

		return this.privateKeys.get(null);
	}

	public void setPrivateKey(RSAPrivateKey privateKey) {

		this.privateKeys.put(null, privateKey);
	}
}