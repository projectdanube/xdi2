package xdi2.messaging.target.interceptor;

import xdi2.core.Statement;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.ExecutionContext;

/**
 * Interceptor that is executed before and after an operation is executed.
 * 
 * @author markus
 */
public interface OperationInterceptor extends Interceptor {

	/**
	 * Run before an operation is executed.
	 * @param operation The operation to process.
	 * @param messageResult The message result.
	 * @param executionContext The current execution context.
	 * @return True, if the operation has been fully handled and the server should stop processing it.
	 */
	public boolean before(Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException;

	/**
	 * Run after an operation is executed.
	 * @param operation The operation to process.
	 * @param messageResult The message result.
	 * @param executionContext The current execution context.
	 * @return True, if the operation has been fully handled and the server should stop processing it.
	 */
	public boolean after(Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException;

	/**
	 * This method provides a way to execute an additional message envelope in a "child" processing loop.
	 * It is run for every statement produced as a result of an operation.
	 * @param operation The operation that produces the result statement.
	 * @param statement A result statement.
	 * @param executionContext The current execution context.
	 * @return A message envelope that will be executed in a "child" processing loop. 
	 */
	public MessageEnvelope feedback(Operation operation, Statement statement, ExecutionContext executionContext) throws Xdi2MessagingException;
}
