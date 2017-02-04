package xdi2.messaging.container.interceptor;

import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.container.MessagingContainer;
import xdi2.messaging.container.exceptions.Xdi2MessagingException;
import xdi2.messaging.container.execution.ExecutionContext;
import xdi2.messaging.container.execution.ExecutionResult;

/**
 * Interceptor that is executed before and after a message envelope is executed,
 * as well as when an exception occurs while executing the message envelope.
 * 
 * @author markus
 */
public interface MessageEnvelopeInterceptor extends Interceptor<MessagingContainer> {

	/**
	 * Run before a message envelope is executed.
	 * @param messageEnvelope The message envelope to process.
	 * @param executionResult The executionResult.
	 * @param executionContext The current execution context.
	 * @return Interceptor result that specifies how the operation should be further processed.
	 */
	public InterceptorResult before(MessageEnvelope messageEnvelope, ExecutionContext executionContext, ExecutionResult executionResult) throws Xdi2MessagingException;

	/**
	 * Run after a message envelope is executed.
	 * @param messageEnvelope The message envelope to process.
	 * @param executionResult The executionResult.
	 * @param executionContext The current execution context.
	 * @return Interceptor result that specifies how the operation should be further processed.
	 */
	public InterceptorResult after(MessageEnvelope messageEnvelope, ExecutionContext executionContext, ExecutionResult executionResult) throws Xdi2MessagingException;

	/**
	 * Run if an exception occurs while a message envelope is executed.
	 * @param executionResult The executionResult.
	 * @param executionContext The current execution context.
	 * @param ex The exception that occurred.
	 */
	public void exception(MessageEnvelope messageEnvelope, ExecutionContext executionContext, ExecutionResult executionResult, Exception ex);
}
