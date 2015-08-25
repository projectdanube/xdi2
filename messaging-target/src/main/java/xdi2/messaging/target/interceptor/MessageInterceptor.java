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
	 * @return Interceptor result that specifies how the operation should be further processed.
	 */
	public InterceptorResult before(Message message, ExecutionContext executionContext, ExecutionResult executionResult) throws Xdi2MessagingException;

	/**
	 * Run after a message is executed.
	 * @param message The message to process.
	 * @param executionResult The execution result.
	 * @param executionContext The current execution context.
	 * @return Interceptor result that specifies how the operation should be further processed.
	 */
	public InterceptorResult after(Message message, ExecutionContext executionContext, ExecutionResult executionResult) throws Xdi2MessagingException;
}
