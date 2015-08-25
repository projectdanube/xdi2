package xdi2.core.security.validate;

import java.security.PublicKey;
import java.util.Collections;
import java.util.Map;

import xdi2.core.syntax.XDIAddress;

/**
 * This is an RSAPublicKeySignatureValidater that validate an XDI RSASignature by
 * obtaining public keys from a statically configured list.
 */
public class RSAStaticPublicKeySignatureValidator extends RSAPublicKeySignatureValidator {

	private Map<XDIAddress, PublicKey> publicKeys;

	public RSAStaticPublicKeySignatureValidator(Map<XDIAddress, PublicKey> publicKeys) {

		super();

		this.publicKeys = publicKeys;
	}

	public RSAStaticPublicKeySignatureValidator(PublicKey publicKey) {

		super();

		this.publicKeys = Collections.singletonMap(null, publicKey);
	}

	public RSAStaticPublicKeySignatureValidator() {

		super();

		this.publicKeys = Collections.emptyMap();
	}

	@Override
	protected PublicKey getPublicKey(XDIAddress signerXDIAddress) {

		// find public key

		PublicKey publicKey = this.getPublicKeys().get(signerXDIAddress);
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

	public PublicKey getPublicKey() {

		return this.publicKeys.get(null);
	}

	public void setPublicKey(PublicKey publicKey) {

		this.publicKeys.put(null, publicKey);
	}
}
