package xdi2.core.security.digest.create;

import xdi2.core.features.digests.SHADigest;

/**
 * This is a DigestCreator that can create an XDI SHADigest.
 */
public interface SHADigestCreator extends DigestCreator<SHADigest> {

	public String getDigestAlgorithm();
	public Integer getDigestVersion();
}
