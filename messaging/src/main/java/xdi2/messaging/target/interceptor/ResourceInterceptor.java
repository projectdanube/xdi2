package xdi2.messaging.target.interceptor;

import xdi2.core.Statement;
import xdi2.core.exceptions.Xdi2MessagingException;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import xdi2.messaging.target.ExecutionContext;

public interface ResourceInterceptor {

	/**
	 * Run before an operation is executed on a statement.
	 * @return true, if the resource has been fully handled.
	 */
	public boolean before(Statement statement, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException;

	/**
	 * Run after an operation is executed on a statement.
	 * @return true, if the resource has been fully handled.
	 */
	public boolean after(Statement statement, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException;
}
