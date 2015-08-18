package xdi2.core.security.sign;

import java.nio.charset.Charset;
import java.security.GeneralSecurityException;

import xdi2.core.ContextNode;
import xdi2.core.features.signatures.Signature;
import xdi2.core.features.signatures.Signatures.NoSignaturesCopyStrategy;
import xdi2.core.io.Normalization;
import xdi2.core.syntax.XDIAddress;

public abstract class AbstractSignatureCreator <SIGNATURE extends Signature> implements SignatureCreator<SIGNATURE> {

	@Override
	public final SIGNATURE createSignature(ContextNode contextNode, XDIAddress signerXDIAddress) throws GeneralSecurityException {

		// get normalized serialization

		byte[] normalizedSerialization = Normalization.serialize(contextNode, new NoSignaturesCopyStrategy()).getBytes(Charset.forName("UTF-8"));

		// create signature

		return this.create(normalizedSerialization, contextNode, signerXDIAddress);
	}

	@Override
	public final SIGNATURE createSignature(ContextNode contextNode) throws GeneralSecurityException {

		return this.createSignature(contextNode, null);
	}

	@Override
	public final void setSignatureValue(SIGNATURE signature, XDIAddress signerXDIAddress) throws GeneralSecurityException {

		// get normalized serialization

		byte[] normalizedSerialization = Normalization.serialize(signature.getBaseContextNode(), new NoSignaturesCopyStrategy()).getBytes(Charset.forName("UTF-8"));

		// set signature value

		this.setValue(normalizedSerialization, signature, signerXDIAddress);
	}

	@Override
	public final void setSignatureValue(SIGNATURE signature) throws GeneralSecurityException {

		this.setSignatureValue(signature, null);
	}

	public abstract SIGNATURE create(byte[] normalizedSerialization, ContextNode contextNode, XDIAddress signerXDIAddress) throws GeneralSecurityException;

	public abstract void setValue(byte[] normalizedSerialization, SIGNATURE signature, XDIAddress signerXDIAddress) throws GeneralSecurityException;
}
