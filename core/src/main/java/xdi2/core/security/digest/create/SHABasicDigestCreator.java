package xdi2.core.security.digest.create;

import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.interfaces.RSAKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.features.digests.Digests;
import xdi2.core.features.digests.SHADigest;

/**
 * This is a SHADigestCreator that can create a SHADigest in a standard way.
 */
public class SHABasicDigestCreator extends AbstractSHADigestCreator implements SHADigestCreator {

	private static Logger log = LoggerFactory.getLogger(SHABasicDigestCreator.class.getName());

	public SHABasicDigestCreator(String digestAlgorithm, Integer digestLength) {

		super(digestAlgorithm, digestLength);
	}

	public SHABasicDigestCreator() {

		super();
	}

	@Override
	public SHADigest create(byte[] normalizedSerialization, ContextNode contextNode) throws GeneralSecurityException {

		// create digest

		SHADigest digest;

		try {

			digest = (SHADigest) Digests.createDigest(
					contextNode,
					this.getDigestAlgorithm(), 
					this.getDigestVersion(), 
					true);
		} catch (Exception ex) {

			throw new GeneralSecurityException("Cannot create digest: " + ex.getMessage(), ex);
		}

		// set digest value

		this.setValue(normalizedSerialization, digest);

		// done

		return digest;
	}

	@Override
	public void setValue(byte[] normalizedSerialization, SHADigest digest) throws GeneralSecurityException {

		// set digest value

		String jceAlgorithm = digest.getJCEAlgorithm();

		if (log.isDebugEnabled()) log.debug("Setting value for " + digest.getClass().getSimpleName() + " with algorithm " + jceAlgorithm);

		java.security.MessageDigest jceMessageDigest = java.security.MessageDigest.getInstance(jceAlgorithm);
		jceMessageDigest.update(normalizedSerialization);

		byte[] digestValue = jceMessageDigest.digest();

		digest.setDigestValue(digestValue);
	}

	/*
	 * Helper methods
	 */

	public static String getPrivateKeyAlgorithm(PrivateKey privateKey) {

		return privateKey.getAlgorithm().toLowerCase();
	}

	public static Integer getPrivateKeyLength(PrivateKey privateKey) {

		if (privateKey instanceof RSAKey) {

			return Integer.valueOf(((RSAKey) privateKey).getModulus().bitLength());
		}

		throw new IllegalArgumentException("Cannot determine key length for private key.");
	}
}
