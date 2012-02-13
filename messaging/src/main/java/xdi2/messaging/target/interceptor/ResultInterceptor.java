package xdi2.messaging.target.interceptor;

import xdi2.core.Statement;
import xdi2.core.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.ExecutionContext;

public interface ResultInterceptor {

	/**
	 * Run on all statements after a message result is complete.
	 * @param statement The statement to process.
	 * @param executionContext The current execution context.
	 * @return true, if the statement is to be excluded from the result.
	 */
	public boolean exclude(Statement statement, ExecutionContext executionContext) throws Xdi2MessagingException;
}
