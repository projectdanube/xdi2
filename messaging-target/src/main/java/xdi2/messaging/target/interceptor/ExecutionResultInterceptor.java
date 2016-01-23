package xdi2.messaging.target.interceptor;

import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.execution.ExecutionContext;
import xdi2.messaging.target.execution.ExecutionResult;

/**
 * Interceptor that is executed on an execution result after a message envelope has been executed.
 * 
 * @author markus
 */
public interface ExecutionResultInterceptor extends Interceptor<MessagingTarget> {

	/**
	 * Run on the execution context and result after it has been finished.
	 * @param messagingTarget The current messaging target.
	 * @param executionContext The current execution context.
	 * @param executionResult The current execution result.
	 */
	public void finish(MessagingTarget messagingTarget, ExecutionContext executionContext, ExecutionResult executionResult) throws Xdi2MessagingException;
}
