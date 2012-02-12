package xdi2.server.interceptor.impl.authn;

import java.util.Map;

import xdi2.server.ExecutionContext;

public class PasswordAuthenticationExecutionContext {

	private static final String EXECUTIONCONTEXT_KEY_PASSWORD = PasswordAuthenticationExecutionContext.class.getCanonicalName() + "#password";

	private PasswordAuthenticationExecutionContext() { }

	public static String getMessagePassword(ExecutionContext executionContext) {

		Map<String, Object> messageAttributes = executionContext.getMessageAttributes();

		return (String) messageAttributes.get(EXECUTIONCONTEXT_KEY_PASSWORD);
	}

	public static void setMessagePassword(ExecutionContext executionContext, String password) {

		Map<String, Object> messageAttributes = executionContext.getMessageAttributes();

		messageAttributes.put(EXECUTIONCONTEXT_KEY_PASSWORD, password);
	}	
}
