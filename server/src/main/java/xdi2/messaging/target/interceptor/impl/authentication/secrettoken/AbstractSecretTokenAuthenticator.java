package xdi2.messaging.target.interceptor.impl.authentication.secrettoken;

import xdi2.messaging.target.MessagingTarget;

public abstract class AbstractSecretTokenAuthenticator implements SecretTokenAuthenticator {

	@Override
	public void init(MessagingTarget messagingTarget, AuthenticationSecretTokenInterceptor authenticationSecretTokenInterceptor) throws Exception {

	}

	@Override
	public void shutdown(MessagingTarget messagingTarget, AuthenticationSecretTokenInterceptor authenticationSecretTokenInterceptor) throws Exception {

	}
}
