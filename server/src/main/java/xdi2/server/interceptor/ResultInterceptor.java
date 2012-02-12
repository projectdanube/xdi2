package xdi2.server.interceptor;

import xdi2.Statement;
import xdi2.exceptions.Xdi2MessagingException;
import xdi2.server.ExecutionContext;

public interface ResultInterceptor {

	/**
	 * Run on all statements after a message result is complete.
	 * @param statement The statement to process.
	 * @param executionContext The current execution context.
	 * @return true, if the statement is to be excluded from the result.
	 */
	public boolean exclude(Statement statement, ExecutionContext executionContext) throws Xdi2MessagingException;
}
