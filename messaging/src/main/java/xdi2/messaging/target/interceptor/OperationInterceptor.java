package xdi2.messaging.target.interceptor;

import xdi2.core.exceptions.Xdi2MessagingException;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import xdi2.messaging.target.ExecutionContext;

public interface OperationInterceptor {

	/**
	 * Run before an operation is executed.
	 * @param operation The operation to process.
	 * @param messageResult The message result.
	 * @param executionContext The current execution context.
	 * @return true, if the operation has been fully handled.
	 */
	public boolean before(Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException;

	/**
	 * Run after an operation is executed.
	 * @param operation The operation to process.
	 * @param messageResult The message result.
	 * @param executionContext The current execution context.
	 * @return true, if the operation has been fully handled.
	 */
	public boolean after(Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException;
}
