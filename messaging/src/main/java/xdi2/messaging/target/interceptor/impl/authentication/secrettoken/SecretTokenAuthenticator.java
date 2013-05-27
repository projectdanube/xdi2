package xdi2.messaging.target.interceptor.impl.authentication.secrettoken;

import xdi2.messaging.Message;
import xdi2.messaging.target.Prototype;

/**
 * The purpose of this interface is to authenticate an incoming XDI message
 * using a secret token. This is used by the AuthenticationSecretTokenInterceptor.
 */
public interface SecretTokenAuthenticator extends Prototype<SecretTokenAuthenticator> {

	public boolean authenticate(Message message, String secretToken);
}