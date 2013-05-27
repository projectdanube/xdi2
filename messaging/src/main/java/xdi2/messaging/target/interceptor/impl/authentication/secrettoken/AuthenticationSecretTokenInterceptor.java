package xdi2.messaging.target.interceptor.impl.authentication.secrettoken;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.Literal;
import xdi2.core.constants.XDIAuthenticationConstants;
import xdi2.messaging.Message;
import xdi2.messaging.MessageResult;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.ExecutionContext;
import xdi2.messaging.target.Prototype;
import xdi2.messaging.target.interceptor.MessageInterceptor;

/**
 * This interceptor will look for a secret token on an incoming XDI message, and authenticate it using 
 * an instance of SecretTokenAuthenticator.
 */
public class AuthenticationSecretTokenInterceptor implements MessageInterceptor, Prototype<AuthenticationSecretTokenInterceptor> {

	private static Logger log = LoggerFactory.getLogger(AuthenticationSecretTokenInterceptor.class.getName());

	private SecretTokenAuthenticator secretTokenAuthenticator;

	/*
	 * Prototype
	 */

	@Override
	public AuthenticationSecretTokenInterceptor instanceFor(PrototypingContext prototypingContext) throws Xdi2MessagingException {

		// create new interceptor

		AuthenticationSecretTokenInterceptor interceptor = new AuthenticationSecretTokenInterceptor();

		// set the authenticator

		interceptor.setSecretTokenAuthenticator(this.getSecretTokenAuthenticator().instanceFor(prototypingContext));

		// done

		return interceptor;
	}

	/*
	 * MessageInterceptor
	 */

	@Override
	public boolean before(Message message, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// look for secret token on the message

		Literal secretTokenLiteral = message.getContextNode().getDeepLiteral(XDIAuthenticationConstants.XRI_S_SECRET_TOKEN);
		if (secretTokenLiteral == null) return false;

		String secretToken = secretTokenLiteral.getLiteralData();

		// authenticate

		boolean authenticated = this.getSecretTokenAuthenticator().authenticate(message, secretToken);

		ContextNode secretTokenContextNode = secretTokenLiteral.getContextNode().getContextNode();
		ContextNode validContextNode = secretTokenContextNode.setDeepContextNode(XDIAuthenticationConstants.XRI_S_VALID);
		Literal validLiteral = validContextNode.setLiteral(Boolean.toString(authenticated));

		if (log.isDebugEnabled()) log.debug(validLiteral.getStatement().toString());

		// done

		return false;
	}

	@Override
	public boolean after(Message message, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	/*
	 * Getters and setters
	 */

	public SecretTokenAuthenticator getSecretTokenAuthenticator() {

		return this.secretTokenAuthenticator;
	}

	public void setSecretTokenAuthenticator(SecretTokenAuthenticator secretTokenAuthenticator) {

		this.secretTokenAuthenticator = secretTokenAuthenticator;
	}
}
