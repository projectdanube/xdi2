package xdi2.core.security.signature.create;

import java.nio.charset.Charset;
import java.security.GeneralSecurityException;

import xdi2.core.ContextNode;
import xdi2.core.features.signatures.Signature;
import xdi2.core.io.Normalization;
import xdi2.core.io.Normalization.NormalizationCopyStrategy;
import xdi2.core.syntax.XDIAddress;

public abstract class AbstractSignatureCreator <SIGNATURE extends Signature> implements SignatureCreator<SIGNATURE> {

	private Class<SIGNATURE> clazz;

	protected AbstractSignatureCreator(Class<SIGNATURE> clazz) {

		this.clazz = clazz;
	}

	@Override
	public boolean canCreate(Class<? extends SIGNATURE> clazz) {

		return this.clazz.isAssignableFrom(clazz);
	}

	@Override
	public final SIGNATURE createSignature(ContextNode contextNode, XDIAddress signerXDIAddress) throws GeneralSecurityException {

		if (contextNode == null) throw new NullPointerException();

		// get normalized serialization

		byte[] normalizedSerialization = Normalization.serialize(contextNode, new NormalizationCopyStrategy()).getBytes(Charset.forName("UTF-8"));

		// create signature

		return this.create(normalizedSerialization, contextNode, signerXDIAddress);
	}

	@Override
	public final SIGNATURE createSignature(ContextNode contextNode) throws GeneralSecurityException {

		return this.createSignature(contextNode, null);
	}

	@Override
	public final void createSignature(SIGNATURE signature, XDIAddress signerXDIAddress) throws GeneralSecurityException {

		if (signature == null) throw new NullPointerException();

		// get normalized serialization

		byte[] normalizedSerialization = Normalization.serialize(signature.getBaseContextNode(), new NormalizationCopyStrategy()).getBytes(Charset.forName("UTF-8"));

		// set signature value

		this.setValue(normalizedSerialization, signature, signerXDIAddress);
	}

	@Override
	public final void createSignature(SIGNATURE signature) throws GeneralSecurityException {

		this.createSignature(signature, null);
	}

	public abstract SIGNATURE create(byte[] normalizedSerialization, ContextNode contextNode, XDIAddress signerXDIAddress) throws GeneralSecurityException;

	public abstract void setValue(byte[] normalizedSerialization, SIGNATURE signature, XDIAddress signerXDIAddress) throws GeneralSecurityException;
}
