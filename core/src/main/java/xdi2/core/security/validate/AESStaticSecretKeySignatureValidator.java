package xdi2.core.security.validate;

import java.util.Collections;
import java.util.Map;

import javax.crypto.SecretKey;

import xdi2.core.syntax.XDIAddress;

/**
 * This is an AESPublicKeySignatureValidater that validate an XDI AESSignature by
 * obtaining secret keys from a statically configured list.
 */
public class AESStaticSecretKeySignatureValidator extends AESSecretKeySignatureValidator {

	private Map<XDIAddress, SecretKey> secretKeys;

	public AESStaticSecretKeySignatureValidator(Map<XDIAddress, SecretKey> secretKeys) {

		super();

		this.secretKeys = secretKeys;
	}

	public AESStaticSecretKeySignatureValidator(SecretKey secretKey) {

		super();

		this.secretKeys = Collections.singletonMap(null, secretKey);
	}

	public AESStaticSecretKeySignatureValidator() {

		super();

		this.secretKeys = Collections.emptyMap();
	}

	@Override
	protected SecretKey getSecretKey(XDIAddress signerXDIAddress) {

		// find secret key

		SecretKey secretKey = this.getSecretKeys().get(signerXDIAddress);
		if (secretKey == null) return null;

		// done

		return secretKey;
	}

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
