package xdi2.messaging.target.interceptor;

import xdi2.core.Graph;
import xdi2.messaging.operations.Operation;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.execution.ExecutionContext;

/**
 * Interceptor that is executed before and after an operation is executed.
 * 
 * @author markus
 */
public interface OperationInterceptor extends Interceptor<MessagingTarget> {

	/**
	 * Run before an operation is executed.
	 * @param operation The operation to process.
	 * @param operationResultGraph The operation's message result.
	 * @param executionContext The current execution context.
	 * @return Interceptor result that specifies how the operation should be further processed.
	 */
	public InterceptorResult before(Operation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException ;

	/**
	 * Run after an operation is executed.
	 * @param operation The operation to process.
	 * @param operationResultGraph The operation's message result.
	 * @param executionContext The current execution context.
	 * @return Interceptor result that specifies how the operation should be further processed.
	 */
	public InterceptorResult after(Operation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException;
}
