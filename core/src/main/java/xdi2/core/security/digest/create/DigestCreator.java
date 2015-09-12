package xdi2.core.security.digest.create;

import java.security.GeneralSecurityException;

import xdi2.core.ContextNode;
import xdi2.core.features.digests.Digest;

/**
 * A DigestCreator can create an XDI Digest.
 */
public interface DigestCreator <DIGEST extends Digest> {

	public boolean canCreate(Class<? extends DIGEST> clazz);

	/**
	 * Create a digest.
	 */
	public DIGEST createDigest(ContextNode contextNode) throws GeneralSecurityException;

	/**
	 * Create a digest.
	 */
	public void createDigest(DIGEST digest) throws GeneralSecurityException;
}
