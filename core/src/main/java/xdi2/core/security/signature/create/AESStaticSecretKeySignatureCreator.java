package xdi2.core.security.signature.create;

import java.util.Collections;
import java.util.Map;

import javax.crypto.SecretKey;

import xdi2.core.syntax.XDIAddress;

/**
 * This is an AESSecretKeySignatureCreator that create an XDI AESSignature by
 * obtaining secret keys from a statically configured list.
 */
public class AESStaticSecretKeySignatureCreator extends AESSecretKeySignatureCreator {

	private Map<XDIAddress, SecretKey> secretKeys;

	public AESStaticSecretKeySignatureCreator(String digestAlgorithm, Integer digestLength, Map<XDIAddress, SecretKey> secretKeys) {

		super(digestAlgorithm, digestLength);

		this.secretKeys = secretKeys;
	}

	public AESStaticSecretKeySignatureCreator(String digestAlgorithm, Integer digestLength, SecretKey secretKey) {

		super(digestAlgorithm, digestLength);

		this.secretKeys = Collections.singletonMap(null, secretKey);
	}

	public AESStaticSecretKeySignatureCreator(String digestAlgorithm, Integer digestLength) {

		super(digestAlgorithm, digestLength);

		this.secretKeys = Collections.emptyMap();
	}

	public AESStaticSecretKeySignatureCreator(Map<XDIAddress, SecretKey> secretKeys) {

		super();

		this.secretKeys = secretKeys;
	}

	public AESStaticSecretKeySignatureCreator(SecretKey secretKey) {

		super();

		this.secretKeys = Collections.singletonMap(null, secretKey);
	}

	public AESStaticSecretKeySignatureCreator() {

		super();

		this.secretKeys = Collections.emptyMap();
	}

	@Override
	protected SecretKey getSecretKey(XDIAddress signerXDIAddress) {

		// find private key

		SecretKey secretKey = this.getSecretKeys().get(signerXDIAddress);
		if (secretKey == null) return null;

		// done

		return secretKey;
	}

	/*
	 * Getters and setters
	 */

	public Map<XDIAddress, SecretKey> getSecretKeys() {

		return this.secretKeys;
	}

	public void setSecretKeys(Map<XDIAddress, SecretKey> secretKeys) {

		this.secretKeys = secretKeys;
	}

	public SecretKey getSecretKey() {

		return this.secretKeys.get(null);
	}

	public void setSecretKey(SecretKey secretKey) {

		this.secretKeys.put(null, secretKey);
	}
}