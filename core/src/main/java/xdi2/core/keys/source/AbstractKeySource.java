package xdi2.core.keys.source;

import java.security.GeneralSecurityException;
import java.security.Key;

import xdi2.core.syntax.XDIAddress;

public abstract class AbstractKeySource <KEY extends Key> implements KeySource<KEY> {

	private boolean alwaysStatic;
	private boolean fallbackStatic;

	public AbstractKeySource() {

		this.alwaysStatic = false;
		this.fallbackStatic = false;
	}

	@Override
	public final KEY getKey() throws GeneralSecurityException {

		return this.getKeyInternal();
	}

	@Override
	public final KEY getKey(XDIAddress XDIaddress) throws GeneralSecurityException {

		if (this.isAlwaysStatic()) return this.getKeyInternal();

		KEY key = this.getKeyInternal(XDIaddress);
		if (key == null && this.isFallbackStatic()) return this.getKeyInternal();

		return key;
	}

	protected abstract KEY getKeyInternal() throws GeneralSecurityException;
	protected abstract KEY getKeyInternal(XDIAddress XDIaddress) throws GeneralSecurityException;

	/*
	 * Getters and setters
	 */

	public boolean isAlwaysStatic() {

		return this.alwaysStatic;
	}

	public void setAlwaysStatic(boolean alwaysStatic) {

		this.alwaysStatic = alwaysStatic;
	}

	public boolean isFallbackStatic() {

		return this.fallbackStatic;
	}

	public void setFallbackStatic(boolean fallbackStatic) {

		this.fallbackStatic = fallbackStatic;
	}
}
