package xdi2.messaging.container.interceptor.impl.security.secrettoken;

import xdi2.messaging.container.MessagingContainer;

public abstract class AbstractSecretTokenValidator implements SecretTokenValidator {

	@Override
	public void init(MessagingContainer messagingContainer, SecretTokenInterceptor authenticationSecretTokenInterceptor) throws Exception {

	}

	@Override
	public void shutdown(MessagingContainer messagingContainer, SecretTokenInterceptor authenticationSecretTokenInterceptor) throws Exception {

	}
}
