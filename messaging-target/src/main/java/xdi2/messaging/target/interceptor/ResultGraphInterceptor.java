package xdi2.messaging.target.interceptor;

import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.execution.ExecutionContext;
import xdi2.messaging.target.execution.ExecutionResult;

/**
 * Interceptor that is executed on a message result after a message envelope has been executed.
 * 
 * @author markus
 */
public interface ResultGraphInterceptor extends Interceptor<MessagingTarget> {

	/**
	 * Run on the message result after it is complete for final adjustments.
	 * @param executionResult The executionResult.
	 * @param executionContext The current execution context.
	 */
	public void finish(ExecutionResult executionResult, ExecutionContext executionContext) throws Xdi2MessagingException;
}
