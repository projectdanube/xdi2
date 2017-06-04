package xdi2.core.security.signature.validate;

import java.security.interfaces.RSAPublicKey;
import java.util.Collections;
import java.util.Map;

import xdi2.core.syntax.XDIAddress;

/**
 * This is an RSAPublicKeySignatureValidator that can validate an XDI RSASignature by
 * obtaining public keys from a statically configured list.
 */
public class RSAStaticPublicKeySignatureValidator extends RSAPublicKeySignatureValidator {

	private Map<XDIAddress, RSAPublicKey> publicKeys;

	public RSAStaticPublicKeySignatureValidator(Map<XDIAddress, RSAPublicKey> publicKeys) {

		super();

		this.publicKeys = publicKeys;
	}

	public RSAStaticPublicKeySignatureValidator(RSAPublicKey publicKey) {

		super();

		this.publicKeys = Collections.singletonMap(null, publicKey);
	}

	public RSAStaticPublicKeySignatureValidator() {

		super();

		this.publicKeys = Collections.emptyMap();
	}

	@Override
	protected RSAPublicKey getPublicKey(XDIAddress signerXDIAddress) {

		// find public key

		RSAPublicKey publicKey = this.getPublicKeys().get(signerXDIAddress);
		if (publicKey == null) return null;

		// done

		return publicKey;
	}

	public Map<XDIAddress, RSAPublicKey> getPublicKeys() {

		return this.publicKeys;
	}

	public void setPublicKeys(Map<XDIAddress, RSAPublicKey> publicKeys) {

		this.publicKeys = publicKeys;
	}

	public RSAPublicKey getPublicKey() {

		return this.publicKeys.get(null);
	}

	public void setPublicKey(RSAPublicKey publicKey) {

		this.publicKeys.put(null, publicKey);
	}
}
