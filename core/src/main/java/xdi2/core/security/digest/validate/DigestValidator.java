package xdi2.core.security.digest.validate;

import java.security.GeneralSecurityException;

import xdi2.core.features.digests.Digest;

/**
 * A DigestValidator can validate an XDI Digest.
 */
public interface DigestValidator <DIGEST extends Digest> {

	public boolean canValidate(Class<? extends DIGEST> clazz);

	/**
	 * Validate a digest.
	 */
	public boolean validateDigest(DIGEST digest) throws GeneralSecurityException;
}
