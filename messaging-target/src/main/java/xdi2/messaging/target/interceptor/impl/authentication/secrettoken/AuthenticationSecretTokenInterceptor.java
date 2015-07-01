package xdi2.messaging.target.interceptor.impl.authentication.secrettoken;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.LiteralNode;
import xdi2.core.constants.XDIAuthenticationConstants;
import xdi2.core.features.nodetypes.XdiAttribute;
import xdi2.core.features.nodetypes.XdiAttributeSingleton;
import xdi2.messaging.Message;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.Prototype;
import xdi2.messaging.target.exceptions.Xdi2AuthenticationException;
import xdi2.messaging.target.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.execution.ExecutionContext;
import xdi2.messaging.target.execution.ExecutionResult;
import xdi2.messaging.target.interceptor.AbstractInterceptor;
import xdi2.messaging.target.interceptor.InterceptorResult;
import xdi2.messaging.target.interceptor.MessageInterceptor;

/**
 * This interceptor looks for a secret token on an incoming XDI message,
 * and invokes an instance of SecretTokenAuthenticator to authenticate the message.
 */
public class AuthenticationSecretTokenInterceptor extends AbstractInterceptor<MessagingTarget> implements MessageInterceptor, Prototype<AuthenticationSecretTokenInterceptor> {

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

		interceptor.setSecretTokenAuthenticator(this.getSecretTokenAuthenticator());

		// done

		return interceptor;
	}

	/*
	 * Init and shutdown
	 */

	@Override
	public void init(MessagingTarget messagingTarget) throws Exception {

		super.init(messagingTarget);

		this.getSecretTokenAuthenticator().init(messagingTarget, this);
	}

	@Override
	public void shutdown(MessagingTarget messagingTarget) throws Exception {

		super.shutdown(messagingTarget);

		this.getSecretTokenAuthenticator().shutdown(messagingTarget, this);
	}

	/*
	 * MessageInterceptor
	 */

	@Override
	public InterceptorResult before(Message message, ExecutionResult executionResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// look for secret token on the message

		String secretToken = message.getSecretToken();
		if (secretToken == null) return InterceptorResult.DEFAULT;

		// authenticate

		if (log.isDebugEnabled()) log.debug("Authenticating via " + this.getSecretTokenAuthenticator().getClass().getSimpleName());

		boolean authenticated = this.getSecretTokenAuthenticator().authenticate(message, secretToken);
		if (! authenticated) throw new Xdi2AuthenticationException("Invalid secret token.", null, executionContext);

		XdiAttribute secretTokenValidXdiAttribute = XdiAttributeSingleton.fromContextNode(message.getContextNode().setDeepContextNode(XDIAuthenticationConstants.XDI_ADD_SECRET_TOKEN_VALID));
		LiteralNode secretTokenValidLiteral = secretTokenValidXdiAttribute.setLiteralDataBoolean(Boolean.valueOf(authenticated));

		if (log.isDebugEnabled()) log.debug("Valid: " + secretTokenValidLiteral.getStatement());

		// done

		return InterceptorResult.DEFAULT;
	}

	@Override
	public InterceptorResult after(Message message, ExecutionResult executionResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return InterceptorResult.DEFAULT;
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
