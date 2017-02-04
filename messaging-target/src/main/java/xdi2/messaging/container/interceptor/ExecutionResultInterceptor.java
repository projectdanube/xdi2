package xdi2.messaging.container.interceptor;

import xdi2.messaging.container.MessagingContainer;
import xdi2.messaging.container.exceptions.Xdi2MessagingException;
import xdi2.messaging.container.execution.ExecutionContext;
import xdi2.messaging.container.execution.ExecutionResult;

/**
 * Interceptor that is executed on an execution result after a message envelope has been executed.
 * 
 * @author markus
 */
public interface ExecutionResultInterceptor extends Interceptor<MessagingContainer> {

	/**
	 * Run on the execution context and result after it has been finished.
	 * @param messagingContainer The current messaging target.
	 * @param executionContext The current execution context.
	 * @param executionResult The current execution result.
	 */
	public void finish(MessagingContainer messagingContainer, ExecutionContext executionContext, ExecutionResult executionResult) throws Xdi2MessagingException;
}
