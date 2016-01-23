package xdi2.messaging.target.interceptor.impl.security.secrettoken;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.LiteralNode;
import xdi2.core.constants.XDISecurityConstants;
import xdi2.core.features.nodetypes.XdiAttribute;
import xdi2.core.features.nodetypes.XdiAttributeSingleton;
import xdi2.core.syntax.XDIAddress;
import xdi2.messaging.Message;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.Prototype;
import xdi2.messaging.target.exceptions.Xdi2SecurityException;
import xdi2.messaging.target.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.execution.ExecutionContext;
import xdi2.messaging.target.execution.ExecutionResult;
import xdi2.messaging.target.interceptor.InterceptorResult;
import xdi2.messaging.target.interceptor.MessageInterceptor;
import xdi2.messaging.target.interceptor.impl.AbstractInterceptor;

/**
 * This interceptor looks for a secret token on an incoming XDI message,
 * and invokes an instance of SecretTokenValidator to validate it.
 */
public class SecretTokenInterceptor extends AbstractInterceptor<MessagingTarget> implements MessageInterceptor, Prototype<SecretTokenInterceptor> {

	private static Logger log = LoggerFactory.getLogger(SecretTokenInterceptor.class.getName());

	private SecretTokenValidator secretTokenValidator;

	/*
	 * Prototype
	 */

	@Override
	public SecretTokenInterceptor instanceFor(PrototypingContext prototypingContext) throws Xdi2MessagingException {

		// create new interceptor

		SecretTokenInterceptor interceptor = new SecretTokenInterceptor();

		// set the authenticator

		interceptor.setSecretTokenValidator(this.getSecretTokenValidator());

		// done

		return interceptor;
	}

	/*
	 * Init and shutdown
	 */

	@Override
	public void init(MessagingTarget messagingTarget) throws Exception {

		super.init(messagingTarget);

		this.getSecretTokenValidator().init(messagingTarget, this);
	}

	@Override
	public void shutdown(MessagingTarget messagingTarget) throws Exception {

		super.shutdown(messagingTarget);

		this.getSecretTokenValidator().shutdown(messagingTarget, this);
	}

	/*
	 * MessageInterceptor
	 */

	@Override
	public InterceptorResult before(Message message, ExecutionContext executionContext, ExecutionResult executionResult) throws Xdi2MessagingException {

		// look for secret token on the message

		String secretToken = message.getSecretToken();
		if (secretToken == null) return InterceptorResult.DEFAULT;

		// validate secret token

		XDIAddress senderXDIAddress = message.getSenderXDIAddress();

		SecretTokenValidator secretTokenAuthenticator = this.getSecretTokenValidator();

		if (log.isDebugEnabled()) log.debug("Validating for " + senderXDIAddress + " via " + secretTokenAuthenticator.getClass().getSimpleName());

		boolean validated = true;

		validated &= secretTokenAuthenticator.authenticate(secretToken, senderXDIAddress);

		// secret token is valid?

		XdiAttribute secretTokenValidXdiAttribute = XdiAttributeSingleton.fromContextNode(message.getContextNode().setDeepContextNode(XDISecurityConstants.XDI_ADD_SECRET_TOKEN_VALID));
		LiteralNode secretTokenValidLiteral = secretTokenValidXdiAttribute.setLiteralBoolean(Boolean.valueOf(validated));

		if (log.isDebugEnabled()) log.debug("Valid for " + senderXDIAddress + ": " + secretTokenValidLiteral.getStatement());

		if (! validated) throw new Xdi2SecurityException("Invalid secret token.", null, executionContext);

		// done

		return InterceptorResult.DEFAULT;
	}

	@Override
	public InterceptorResult after(Message message, ExecutionContext executionContext, ExecutionResult executionResult) throws Xdi2MessagingException {

		return InterceptorResult.DEFAULT;
	}

	/*
	 * Getters and setters
	 */

	public SecretTokenValidator getSecretTokenValidator() {

		return this.secretTokenValidator;
	}

	public void setSecretTokenValidator(SecretTokenValidator secretTokenValidator) {

		this.secretTokenValidator = secretTokenValidator;
	}
}
