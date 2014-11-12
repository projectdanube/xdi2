package xdi2.core.keys.source;

import java.security.PrivateKey;
import java.util.Map;

import xdi2.core.syntax.XDIAddress;

/**
 * A private key source using a
 * statically configured list of XDI addresses and private keys.
 */
public class StaticPrivateKeySource extends AbstractKeySource<PrivateKey> implements KeySource<PrivateKey> {

	private PrivateKey staticPrivateKey;
	private Map<XDIAddress, PrivateKey> staticPrivateKeys;

	public StaticPrivateKeySource(PrivateKey staticPrivateKey, Map<XDIAddress, PrivateKey> staticPrivateKeys) {

		super();

		this.staticPrivateKey = staticPrivateKey;
		this.staticPrivateKeys = staticPrivateKeys;
	}

	public StaticPrivateKeySource() {

		super();
	}

	@Override
	protected PrivateKey getKeyInternal() {

		return this.staticPrivateKey;
	}

	@Override
	protected PrivateKey getKeyInternal(XDIAddress XDIaddress) {

		// look for static private key

		PrivateKey privateKey = this.getStaticPrivateKeys().get(XDIaddress);
		if (privateKey == null) return null;

		// done

		return privateKey;
	}

	/*
	 * Getters and setters
	 */

	public PrivateKey getStaticPrivateKey() {

		return this.staticPrivateKey;
	}

	public void setStaticPrivateKey(PrivateKey staticPrivateKey) {

		this.staticPrivateKey = staticPrivateKey;
	}

	public Map<XDIAddress, PrivateKey> getStaticPrivateKeys() {

		return this.staticPrivateKeys;
	}

	public void setStaticPrivateKeys(Map<XDIAddress, PrivateKey> staticPrivateKeys) {

		this.staticPrivateKeys = staticPrivateKeys;
	}
}