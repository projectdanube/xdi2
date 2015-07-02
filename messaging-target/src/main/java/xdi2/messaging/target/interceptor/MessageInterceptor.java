package xdi2.messaging.target.interceptor;

import xdi2.messaging.Message;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.execution.ExecutionContext;
import xdi2.messaging.target.execution.ExecutionResult;

/**
 * Interceptor that is executed before and after a message is executed.
 * 
 * @author markus
 */
public interface MessageInterceptor extends Interceptor<MessagingTarget> {

	/**
	 * Run before a message is executed.
	 * @param message The message to process.
	 * @param executionResult The execution result.
	 * @param executionContext The current execution context.
	 * @return True, if the message has been fully handled and the server should stop processing it.
	 */
	public InterceptorResult before(Message message, ExecutionResult executionResult, ExecutionContext executionContext) throws Xdi2MessagingException;

	/**
	 * Run after a message is executed.
	 * @param message The message to process.
	 * @param executionResult The execution result.
	 * @param executionContext The current execution context.
	 * @return True, if the message has been fully handled and the server should stop processing it.
	 */
	public InterceptorResult after(Message message, ExecutionResult executionResult, ExecutionContext executionContext) throws Xdi2MessagingException;
}
