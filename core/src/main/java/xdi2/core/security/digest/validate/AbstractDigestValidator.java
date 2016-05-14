package xdi2.core.security.digest.validate;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;

import xdi2.core.features.digests.Digest;
import xdi2.core.io.Normalization;
import xdi2.core.io.Normalization.NormalizationCopyStrategy;

public abstract class AbstractDigestValidator <DIGEST extends Digest> implements DigestValidator<DIGEST> {

	private Class<DIGEST> clazz;

	protected AbstractDigestValidator(Class<DIGEST> clazz) {

		this.clazz = clazz;
	}

	@Override
	public boolean canValidate(Class<? extends DIGEST> clazz) {

		return this.clazz.isAssignableFrom(clazz);
	}

	@Override
	public final boolean validateDigest(DIGEST digest) throws GeneralSecurityException {

		if (digest == null) throw new NullPointerException();

		// get normalized serialization

		byte[] normalizedSerialization = Normalization.serialize(digest.getBaseContextNode(), new NormalizationCopyStrategy()).getBytes(StandardCharsets.UTF_8);

		// get digest value

		byte[] digestValue = digest.getDigestValue();
		if (digestValue == null) throw new GeneralSecurityException("No digest value.");

		// validate digest

		return this.validate(normalizedSerialization, digestValue, digest);
	}

	public abstract boolean validate(byte[] normalizedSerialization, byte[] digestValue, DIGEST digest) throws GeneralSecurityException;
}
