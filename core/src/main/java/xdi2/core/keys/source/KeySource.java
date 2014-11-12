package xdi2.core.keys.source;

import java.security.GeneralSecurityException;
import java.security.Key;

import xdi2.core.syntax.XDIAddress;

public interface KeySource <KEY extends Key> {

	public KEY getKey() throws GeneralSecurityException;
	public KEY getKey(XDIAddress XDIaddress) throws GeneralSecurityException;
}
