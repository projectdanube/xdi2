package xdi2.core.security.digest.create;

import java.nio.charset.Charset;
import java.security.GeneralSecurityException;

import xdi2.core.ContextNode;
import xdi2.core.features.digests.Digest;
import xdi2.core.features.digests.Digests.NoDigestsCopyStrategy;
import xdi2.core.io.Normalization;
import xdi2.core.io.Normalization.NormalizationCopyStrategy;

public abstract class AbstractDigestCreator <DIGEST extends Digest> implements DigestCreator<DIGEST> {

	private Class<DIGEST> clazz;

	protected AbstractDigestCreator(Class<DIGEST> clazz) {

		this.clazz = clazz;
	}

	@Override
	public boolean canCreate(Class<? extends DIGEST> clazz) {

		return this.clazz.isAssignableFrom(clazz);
	}

	@Override
	public final DIGEST createDigest(ContextNode contextNode) throws GeneralSecurityException {

		// get normalized serialization

		byte[] normalizedSerialization = Normalization.serialize(contextNode, new NoDigestsCopyStrategy()).getBytes(Charset.forName("UTF-8"));

		// create digest

		return this.create(normalizedSerialization, contextNode);
	}

	@Override
	public final void createDigest(DIGEST digest) throws GeneralSecurityException {

		if (digest == null) throw new NullPointerException();

		// get normalized serialization

		byte[] normalizedSerialization = Normalization.serialize(digest.getBaseContextNode(), new NormalizationCopyStrategy()).getBytes(Charset.forName("UTF-8"));

		// set digest value

		this.setValue(normalizedSerialization, digest);
	}

	public abstract DIGEST create(byte[] normalizedSerialization, ContextNode contextNode) throws GeneralSecurityException;

	public abstract void setValue(byte[] normalizedSerialization, DIGEST signature) throws GeneralSecurityException;
}
