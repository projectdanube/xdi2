package xdi2.messaging.target.interceptor;

import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.execution.ExecutionContext;
import xdi2.messaging.target.execution.ExecutionResult;

/**
 * Interceptor that is executed before and after a message envelope is executed,
 * as well as when an exception occurs while executing the message envelope.
 * 
 * @author markus
 */
public interface MessageEnvelopeInterceptor extends Interceptor<MessagingTarget> {

	/**
	 * Run before a message envelope is executed.
	 * @param messageEnvelope The message envelope to process.
	 * @param executionResult The executionResult.
	 * @param executionContext The current execution context.
	 * @return Interceptor result that specifies how the operation should be further processed.
	 */
	public InterceptorResult before(MessageEnvelope messageEnvelope, ExecutionResult executionResult, ExecutionContext executionContext) throws Xdi2MessagingException;

	/**
	 * Run after a message envelope is executed.
	 * @param messageEnvelope The message envelope to process.
	 * @param executionResult The executionResult.
	 * @param executionContext The current execution context.
	 * @return Interceptor result that specifies how the operation should be further processed.
	 */
	public InterceptorResult after(MessageEnvelope messageEnvelope, ExecutionResult executionResult, ExecutionContext executionContext) throws Xdi2MessagingException;

	/**
	 * Run if an exception occurs while a message envelope is executed.
	 * @param executionResult The executionResult.
	 * @param executionContext The current execution context.
	 * @param ex The exception that occurred.
	 */
	public void exception(MessageEnvelope messageEnvelope, ExecutionResult executionResult, ExecutionContext executionContext, Exception ex);
}
