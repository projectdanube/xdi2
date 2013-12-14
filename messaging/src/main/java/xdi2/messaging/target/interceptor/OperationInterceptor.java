package xdi2.messaging.target.interceptor;

import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import xdi2.messaging.context.ExecutionContext;
import xdi2.messaging.exceptions.Xdi2MessagingException;

/**
 * Interceptor that is executed before and after an operation is executed.
 * 
 * @author markus
 */
public interface OperationInterceptor extends Interceptor {

	/**
	 * Run before an operation is executed.
	 * @param operation The operation to process.
	 * @param operationMessageResult The operation's message result.
	 * @param executionContext The current execution context.
	 * @return True, if the operation has been fully handled and the server should stop processing it.
	 */
	public InterceptorResult before(Operation operation, MessageResult operationMessageResult, ExecutionContext executionContext) throws Xdi2MessagingException;

	/**
	 * Run after an operation is executed.
	 * @param operation The operation to process.
	 * @param operationMessageResult The operation's message result.
	 * @param executionContext The current execution context.
	 * @return True, if the operation has been fully handled and the server should stop processing it.
	 */
	public InterceptorResult after(Operation operation, MessageResult operationMessageResult, ExecutionContext executionContext) throws Xdi2MessagingException;
}
