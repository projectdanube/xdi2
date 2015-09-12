package xdi2.core.security.digest.validate;

import java.security.GeneralSecurityException;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.features.digests.SHADigest;

/**
 * This is a SHAValidator that validate an XDI SHADigest.
 */
public class SHABasicDigestValidator extends AbstractSHADigestValidator implements SHADigestValidator {

	private static Logger log = LoggerFactory.getLogger(SHABasicDigestValidator.class.getName());

	public SHABasicDigestValidator() {

	}

	@Override
	public boolean validate(byte[] normalizedSerialization, byte[] digestValue, SHADigest digest) throws GeneralSecurityException {

		// validate

		String jceAlgorithm = digest.getJCEAlgorithm();

		if (log.isDebugEnabled()) log.debug("Validating for " + digest.getClass().getSimpleName() + " with algorithm " + jceAlgorithm);

		java.security.MessageDigest jceMessageDigest = java.security.MessageDigest.getInstance(jceAlgorithm);
		jceMessageDigest.update(normalizedSerialization);

		return Arrays.equals(jceMessageDigest.digest(), digestValue);
	}
}
