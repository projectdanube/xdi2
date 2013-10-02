package xdi2.messaging.target.interceptor.impl.authentication.signature;

import xdi2.core.features.signatures.Signature;
import xdi2.messaging.Message;
import xdi2.messaging.target.Prototype;

/**
 * The purpose of this interface is to authenticate an incoming XDI message
 * using a signature. This is used by the AuthenticationSignatureInterceptor.
 */
public interface SignatureAuthenticator extends Prototype<SignatureAuthenticator> {

	public void init() throws Exception;
	public void shutdown() throws Exception;

	/**
	 * Authenticates an XDI message given a signature.
	 */
	public boolean authenticate(Message message, Signature signature);
}
