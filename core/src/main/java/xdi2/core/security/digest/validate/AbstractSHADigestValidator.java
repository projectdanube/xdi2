package xdi2.core.security.digest.validate;

import xdi2.core.features.digests.SHADigest;

public abstract class AbstractSHADigestValidator extends AbstractDigestValidator<SHADigest> implements SHADigestValidator {

	protected AbstractSHADigestValidator() {

		super(SHADigest.class);
	}
}
