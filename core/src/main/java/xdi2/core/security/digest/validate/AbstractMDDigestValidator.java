package xdi2.core.security.digest.validate;

import xdi2.core.features.digests.MDDigest;

public abstract class AbstractMDDigestValidator extends AbstractDigestValidator<MDDigest> implements MDDigestValidator {

	protected AbstractMDDigestValidator() {

		super(MDDigest.class);
	}
}
