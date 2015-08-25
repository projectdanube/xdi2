package xdi2.core.security.sign;

import xdi2.core.features.signatures.AESSignature;
import xdi2.core.features.signatures.RSASignature;

public abstract class AbstractAESSignatureCreator extends AbstractSignatureCreator<AESSignature> implements AESSignatureCreator {

	public static final String DEFAULT_DIGEST_ALGORITHM = RSASignature.DIGEST_ALGORITHM_SHA;
	public static final Integer DEFAULT_DIGEST_LENGTH = Integer.valueOf(256);

	private String digestAlgorithm;
	private Integer digestLength;

	public AbstractAESSignatureCreator(String digestAlgorithm, Integer digestLength) {

		super(AESSignature.class);

		this.digestAlgorithm = digestAlgorithm;
		this.digestLength = digestLength;
	}

	public AbstractAESSignatureCreator() {

		super(AESSignature.class);

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
