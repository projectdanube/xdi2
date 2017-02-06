package xdi2.core.security.signature.validate;

import java.nio.charset.Charset;
import java.security.GeneralSecurityException;

import xdi2.core.features.signatures.Signature;
import xdi2.core.io.Normalization;
import xdi2.core.io.Normalization.NormalizationCopyStrategy;
import xdi2.core.syntax.XDIAddress;

public abstract class AbstractSignatureValidator <SIGNATURE extends Signature> implements SignatureValidator<SIGNATURE> {

	private Class<SIGNATURE> clazz;

	protected AbstractSignatureValidator(Class<SIGNATURE> clazz) {

		this.clazz = clazz;
	}

	@Override
	public boolean canValidate(Class<? extends Signature> clazz) {

		return this.clazz.isAssignableFrom(clazz);
	}

	@Override
	@SuppressWarnings("unchecked")
	public final boolean validateSignature(Signature signature, XDIAddress signerXDIAddress) throws GeneralSecurityException {

		if (signature == null) throw new NullPointerException();

		// get normalized serialization

		byte[] normalizedSerialization = Normalization.serialize(signature.getBaseContextNode(), new NormalizationCopyStrategy()).getBytes(Charset.forName("UTF-8"));

		// get signature value

		byte[] signatureValue = signature.getSignatureValue();
		if (signatureValue == null) throw new GeneralSecurityException("No signature value.");

		// validate signature

		return this.validate(normalizedSerialization, signatureValue, (SIGNATURE) signature, signerXDIAddress);
	}

	@Override
	public final boolean validateSignature(Signature signature) throws GeneralSecurityException {

		return this.validateSignature(signature, null);
	}

	public abstract boolean validate(byte[] normalizedSerialization, byte[] signatureValue, SIGNATURE signature, XDIAddress signerXDIAddress) throws GeneralSecurityException;
}
