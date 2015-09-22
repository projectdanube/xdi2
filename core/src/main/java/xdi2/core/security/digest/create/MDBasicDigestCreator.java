package xdi2.core.security.digest.create;

import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.interfaces.RSAKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.features.digests.Digests;
import xdi2.core.features.digests.MDDigest;

/**
 * This is a MDDigestCreator that can create a MDDigest in a standard way.
 */
public class MDBasicDigestCreator extends AbstractMDDigestCreator implements MDDigestCreator {

	private static Logger log = LoggerFactory.getLogger(MDBasicDigestCreator.class.getName());

	public MDBasicDigestCreator(String digestAlgorithm, Integer digestVersion) {

		super(digestAlgorithm, digestVersion);
	}

	public MDBasicDigestCreator() {

		super();
	}

	@Override
	public MDDigest create(byte[] normalizedSerialization, ContextNode contextNode) throws GeneralSecurityException {

		// create digest

		MDDigest digest;

		try {

			digest = (MDDigest) Digests.createDigest(
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
	public void setValue(byte[] normalizedSerialization, MDDigest digest) throws GeneralSecurityException {

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
