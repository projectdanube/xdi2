package xdi2.core.security.sign;

import xdi2.core.features.signatures.RSASignature;

public abstract class AbstractRSASignatureCreator extends AbstractSignatureCreator<RSASignature> implements RSASignatureCreator {

	public static final String DEFAULT_DIGEST_ALGORITHM = RSASignature.DIGEST_ALGORITHM_SHA;
	public static final Integer DEFAULT_DIGEST_LENGTH = Integer.valueOf(256);

	private String digestAlgorithm;
	private Integer digestLength;

	public AbstractRSASignatureCreator(String digestAlgorithm, Integer digestLength) {

		this.digestAlgorithm = digestAlgorithm;
		this.digestLength = digestLength;
	}

	public AbstractRSASignatureCreator() {

		this.digestLength = DEFAULT_DIGEST_LENGTH;
		this.digestAlgorithm = DEFAULT_DIGEST_ALGORITHM;
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
	public Integer getDigestLength() {

		return this.digestLength;
	}

	public void setDigestLength(Integer digestLength) {

		this.digestLength = digestLength;
	}
}
