package xdi2.core.security.signature.validate;

import xdi2.core.features.signatures.AESSignature;

public abstract class AbstractAESSignatureValidator extends AbstractSignatureValidator<AESSignature> implements AESSignatureValidator {

	protected AbstractAESSignatureValidator() {

		super(AESSignature.class);
	}
}
