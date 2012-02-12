package xdi2.server.interceptor.impl.authn;

import java.util.Map;

import xdi2.server.ExecutionContext;

/**
 * Methods for storing state related to authentication in an execution context.
 */
public class AuthenticationExecutionContext {

	private static final String EXECUTIONCONTEXT_KEY_AUTHENTICATED = AuthenticationExecutionContext.class.getCanonicalName() + "#authenticated";

	private AuthenticationExecutionContext() { }

	public static boolean isMessageAuthenticated(ExecutionContext executionContext) {

		Map<String, Object> messageAttributes = executionContext.getMessageAttributes();

		return Boolean.TRUE.equals(messageAttributes.get(EXECUTIONCONTEXT_KEY_AUTHENTICATED));
	}

	public static void setMessageAuthenticated(ExecutionContext executionContext) {

		Map<String, Object> messageAttributes = executionContext.getMessageAttributes();

		messageAttributes.put(EXECUTIONCONTEXT_KEY_AUTHENTICATED, Boolean.TRUE);
	}	
}
