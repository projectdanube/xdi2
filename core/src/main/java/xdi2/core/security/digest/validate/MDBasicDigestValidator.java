package xdi2.core.security.digest.validate;

import java.security.GeneralSecurityException;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.features.digests.MDDigest;

/**
 * This is a MDValidator that validate an XDI MDDigest.
 */
public class MDBasicDigestValidator extends AbstractMDDigestValidator implements MDDigestValidator {

	private static Logger log = LoggerFactory.getLogger(MDBasicDigestValidator.class.getName());

	public MDBasicDigestValidator() {

	}

	@Override
	public boolean validate(byte[] normalizedSerialization, byte[] digestValue, MDDigest digest) throws GeneralSecurityException {

		// validate

		String jceAlgorithm = digest.getJCEAlgorithm();

		if (log.isDebugEnabled()) log.debug("Validating for " + digest.getClass().getSimpleName() + " with algorithm " + jceAlgorithm);

		java.security.MessageDigest jceMessageDigest = java.security.MessageDigest.getInstance(jceAlgorithm);
		jceMessageDigest.update(normalizedSerialization);

		return Arrays.equals(jceMessageDigest.digest(), digestValue);
	}
}
