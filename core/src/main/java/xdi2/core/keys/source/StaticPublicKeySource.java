package xdi2.core.keys.source;

import java.security.PublicKey;
import java.util.Map;

import xdi2.core.syntax.XDIAddress;

/**
 * A public key source using a
 * statically configured list of XDI addresses and public keys.
 */
public class StaticPublicKeySource extends AbstractKeySource<PublicKey> implements KeySource<PublicKey> {

	private PublicKey staticPublicKey;
	private Map<XDIAddress, PublicKey> staticPublicKeys;

	public StaticPublicKeySource(PublicKey staticPublicKey, Map<XDIAddress, PublicKey> staticPublicKeys) {

		super();

		this.staticPublicKey = staticPublicKey;
		this.staticPublicKeys = staticPublicKeys;
	}

	public StaticPublicKeySource() {

		super();
	}

	@Override
	protected PublicKey getKeyInternal() {
		
		return this.staticPublicKey;
	}

	@Override
	protected PublicKey getKeyInternal(XDIAddress XDIaddress) {

		// look for static public key

		PublicKey staticPublicKey = this.getStaticPublicKeys().get(XDIaddress);
		if (staticPublicKey == null) return null;

		// done

		return staticPublicKey;
	}

	/*
	 * Getters and setters
	 */

	public PublicKey getStaticPublicKey() {

		return this.staticPublicKey;
	}

	public void setStaticPublicKey(PublicKey publicKey) {

		this.staticPublicKey = publicKey;
	}

	public Map<XDIAddress, PublicKey> getStaticPublicKeys() {

		return this.staticPublicKeys;
	}

	public void setStaticPublicKeys(Map<XDIAddress, PublicKey> publicKeys) {

		this.staticPublicKeys = publicKeys;
	}
}