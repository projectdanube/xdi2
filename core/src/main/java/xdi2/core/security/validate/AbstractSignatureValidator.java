package xdi2.core.security.validate;

import java.nio.charset.Charset;
import java.security.GeneralSecurityException;

import xdi2.core.features.signatures.Signature;
import xdi2.core.features.signatures.Signatures.NoSignaturesCopyStrategy;
import xdi2.core.io.Normalization;
import xdi2.core.syntax.XDIAddress;

public abstract class AbstractSignatureValidator <SIGNATURE extends Signature> implements SignatureValidator<SIGNATURE> {

	@Override
	public final boolean validateSignature(SIGNATURE signature, XDIAddress signerXDIAddress) throws GeneralSecurityException {

		// get normalized serialization

		byte[] normalizedSerialization = Normalization.serialize(signature.getBaseContextNode(), new NoSignaturesCopyStrategy()).getBytes(Charset.forName("UTF-8"));

		// get signature value

		byte[] signatureValue = signature.getSignatureValue();
		if (signatureValue == null) throw new GeneralSecurityException("No signature value.");

		// validate signature

		return this.validate(normalizedSerialization, signatureValue, signature, signerXDIAddress);
	}

	@Override
	public final boolean validateSignature(SIGNATURE signature) throws GeneralSecurityException {

		return this.validateSignature(signature, null);
	}

	public abstract boolean validate(byte[] normalizedSerialization, byte[] signatureValue, SIGNATURE signature, XDIAddress signerXDIAddress) throws GeneralSecurityException;
}
