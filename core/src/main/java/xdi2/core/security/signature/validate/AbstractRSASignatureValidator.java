package xdi2.core.security.signature.validate;

import xdi2.core.features.signatures.RSASignature;

public abstract class AbstractRSASignatureValidator extends AbstractSignatureValidator<RSASignature> implements RSASignatureValidator {

	protected AbstractRSASignatureValidator() {

		super(RSASignature.class);
	}
}
