package xdi2.core.security.signature.create;

import xdi2.core.features.signatures.RSASignature;

public abstract class AbstractRSASignatureCreator extends AbstractSignatureCreator<RSASignature> implements RSASignatureCreator {

	public static final String DEFAULT_DIGEST_ALGORITHM = RSASignature.DIGEST_ALGORITHM_SHA;
	public static final Integer DEFAULT_DIGEST_VERSION = Integer.valueOf(256);

	private String digestAlgorithm;
	private Integer digestVersion;

	public AbstractRSASignatureCreator(String digestAlgorithm, Integer digestVersion) {

		super(RSASignature.class);

		this.digestAlgorithm = digestAlgorithm;
		this.digestVersion = digestVersion;
	}

	public AbstractRSASignatureCreator() {

		super(RSASignature.class);

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
