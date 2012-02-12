package xdi2.server.interceptor.impl.authn;

import xdi2.ContextNode;
import xdi2.Literal;
import xdi2.exceptions.Xdi2MessagingException;
import xdi2.messaging.Message;
import xdi2.messaging.MessageResult;
import xdi2.server.ExecutionContext;
import xdi2.server.interceptor.AbstractMessageInterceptor;
import xdi2.server.interceptor.MessageInterceptor;
import xdi2.xri3.impl.XRI3Authority;
import xdi2.xri3.impl.XRI3SubSegment;

public class PasswordAuthenticationMessageInterceptor extends AbstractMessageInterceptor implements MessageInterceptor {

	private static final XRI3SubSegment XRI_PASSWORD = new XRI3SubSegment("$password");

	private PasswordValidator passwordValidator;

	@Override
	public boolean before(Message message, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XRI3Authority senderXri = message.getSender();

		ContextNode contextNode = message.getContextNode().getContextNode(XRI_PASSWORD);
		if (contextNode == null) return false;

		Literal literal = contextNode.getLiteral();
		if (literal == null) return false;

		String password = literal.getLiteralData();

		if (! this.passwordValidator.isValidPassword(senderXri, password)) return false;

		// store authentication and password in execution context

		AuthenticationExecutionContext.setMessageAuthenticated(executionContext);
		PasswordAuthenticationExecutionContext.setMessagePassword(executionContext, password);

		// done

		return false;
	}

	public PasswordValidator getPasswordValidator() {

		return this.passwordValidator;
	}

	public void setPasswordValidator(PasswordValidator passwordValidator) {

		this.passwordValidator = passwordValidator;
	}

	public interface PasswordValidator {

		public boolean isValidPassword(XRI3Authority senderXri, String password) throws Xdi2MessagingException;
	}
}
