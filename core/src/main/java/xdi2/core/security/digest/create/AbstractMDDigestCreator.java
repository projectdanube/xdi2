package xdi2.core.security.digest.create;

import xdi2.core.features.digests.MDDigest;

public abstract class AbstractMDDigestCreator extends AbstractDigestCreator<MDDigest> implements MDDigestCreator {

	public static final String DEFAULT_DIGEST_ALGORITHM = MDDigest.DIGEST_ALGORITHM_MD;
	public static final Integer DEFAULT_DIGEST_VERSION = Integer.valueOf(5);

	private String digestAlgorithm;
	private Integer digestVersion;

	public AbstractMDDigestCreator(String digestAlgorithm, Integer digestVersion) {

		super(MDDigest.class);

		this.digestAlgorithm = digestAlgorithm;
		this.digestVersion = digestVersion;
	}

	public AbstractMDDigestCreator() {

		super(MDDigest.class);

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
