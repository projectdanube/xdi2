package xdi2.messaging.target.interceptor.impl.authentication.signature;

import xdi2.core.features.signatures.Signature;

public abstract class AbstractSignatureAuthenticator <SIG extends Signature<?, ?>> implements SignatureAuthenticator<SIG> {

	@Override
	public void init() throws Exception {

	}

	@Override
	public void shutdown() throws Exception {

	}
}
