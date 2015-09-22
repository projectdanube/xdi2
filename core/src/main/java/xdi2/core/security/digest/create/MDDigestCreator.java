package xdi2.core.security.digest.create;

import xdi2.core.features.digests.MDDigest;

/**
 * This is a DigestCreator that can create an XDI MDDigest.
 */
public interface MDDigestCreator extends DigestCreator<MDDigest> {

	public String getDigestAlgorithm();
	public void setDigestAlgorithm(String digestAlgorithm);

	public Integer getDigestVersion();
	public void setDigestVersion(Integer digestVersion);
}
