package xdi2.messaging.target.interceptor.impl.security.secrettoken;

import xdi2.messaging.target.MessagingTarget;

public abstract class AbstractSecretTokenValidator implements SecretTokenValidator {

	@Override
	public void init(MessagingTarget messagingTarget, SecretTokenInterceptor authenticationSecretTokenInterceptor) throws Exception {

	}

	@Override
	public void shutdown(MessagingTarget messagingTarget, SecretTokenInterceptor authenticationSecretTokenInterceptor) throws Exception {

	}
}
