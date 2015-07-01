package xdi2.messaging.target.interceptor.impl.authentication.signature;

import xdi2.messaging.target.MessagingTarget;

public abstract class AbstractSignatureAuthenticator implements SignatureAuthenticator {

	@Override
	public void init(MessagingTarget messagingTarget, AuthenticationSignatureInterceptor authenticationSignatureInterceptor) throws Exception {

	}

	@Override
	public void shutdown(MessagingTarget messagingTarget, AuthenticationSignatureInterceptor authenticationSignatureInterceptor) throws Exception {

	}
}
