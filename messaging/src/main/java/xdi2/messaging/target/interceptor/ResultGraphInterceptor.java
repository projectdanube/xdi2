package xdi2.messaging.target.interceptor;

import xdi2.core.Graph;
import xdi2.messaging.context.ExecutionContext;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.MessagingTarget;

/**
 * Interceptor that is executed on a message result after a message envelope has been executed.
 * 
 * @author markus
 */
public interface ResultGraphInterceptor extends Interceptor<MessagingTarget> {

	/**
	 * Run on the message result after it is complete for final adjustments.
	 * @param messageResult The message result.
	 * @param executionContext The current execution context.
	 */
	public void finish(Graph resultGraph, ExecutionContext executionContext) throws Xdi2MessagingException;
}
