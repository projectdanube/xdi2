package xdi2.messaging.target.interceptor.impl.authentication.secrettoken;

import xdi2.core.syntax.XDIAddress;
import xdi2.messaging.target.MessagingTarget;

/**
 * The purpose of this interface is to authenticate an incoming XDI message
 * using a secret token. This is used by the AuthenticationSecretTokenInterceptor.
 */
public interface SecretTokenAuthenticator {

	/*
	 * Init and shutdown
	 */

	public void init(MessagingTarget messagingTarget, AuthenticationSecretTokenInterceptor authenticationSecretTokenInterceptor) throws Exception;
	public void shutdown(MessagingTarget messagingTarget, AuthenticationSecretTokenInterceptor authenticationSecretTokenInterceptor) throws Exception;

	/**
	 * Authenticates an XDI message given a secret token.
	 */
	public boolean authenticate(String secretToken, XDIAddress senderXDIAddress);
}
