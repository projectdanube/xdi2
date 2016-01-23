package xdi2.core.security.signature.create;

import xdi2.core.features.signatures.AESSignature;
import xdi2.core.features.signatures.RSASignature;

public abstract class AbstractAESSignatureCreator extends AbstractSignatureCreator<AESSignature> implements AESSignatureCreator {

	public static final String DEFAULT_DIGEST_ALGORITHM = RSASignature.DIGEST_ALGORITHM_SHA;
	public static final Integer DEFAULT_DIGEST_VERSION = Integer.valueOf(256);

	private String digestAlgorithm;
	private Integer digestVersion;

	public AbstractAESSignatureCreator(String digestAlgorithm, Integer digestVersion) {

		super(AESSignature.class);

		this.digestAlgorithm = digestAlgorithm;
		this.digestVersion = digestVersion;
	}

	public AbstractAESSignatureCreator() {

		super(AESSignature.class);

		this.digestAlgorithm = DEFAULT_DIGEST_ALGORITHM;
		this.digestVersion = DEFAULT_DIGEST_VERSION;
	}

	/*
	 * Getters and setters
	 */

	@Override
	public String getDigestAlgorithm() {

		return this.digestAlgorithm;
	}

	public void setDigestAlgorithm(String digestAlgorithm) {

		this.digestAlgorithm = digestAlgorithm;
	}

	@Override
	public Integer getDigestVersion() {

		return this.digestVersion;
	}

	public void setDigestVersion(Integer digestVersion) {

		this.digestVersion = digestVersion;
	}
}
