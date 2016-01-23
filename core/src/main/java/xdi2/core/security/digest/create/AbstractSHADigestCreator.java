package xdi2.core.security.digest.create;

import xdi2.core.features.digests.SHADigest;

public abstract class AbstractSHADigestCreator extends AbstractDigestCreator<SHADigest> implements SHADigestCreator {

	public static final String DEFAULT_DIGEST_ALGORITHM = SHADigest.DIGEST_ALGORITHM_SHA;
	public static final Integer DEFAULT_DIGEST_VERSION = Integer.valueOf(256);

	private String digestAlgorithm;
	private Integer digestVersion;

	public AbstractSHADigestCreator(String digestAlgorithm, Integer digestVersion) {

		super(SHADigest.class);

		this.digestAlgorithm = digestAlgorithm;
		this.digestVersion = digestVersion;
	}

	public AbstractSHADigestCreator() {

		super(SHADigest.class);

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

	@Override
	public void setDigestAlgorithm(String digestAlgorithm) {

		this.digestAlgorithm = digestAlgorithm;
	}

	@Override
	public Integer getDigestVersion() {

		return this.digestVersion;
	}

	@Override
	public void setDigestVersion(Integer digestVersion) {

		this.digestVersion = digestVersion;
	}
}
