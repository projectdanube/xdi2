package xdi2.messaging.target.interceptor;

import xdi2.core.Graph;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIStatement;
import xdi2.messaging.operations.Operation;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.exceptions.Xdi2PushRequiredException;
import xdi2.messaging.target.execution.ExecutionContext;

/**
 * Interceptor that is executed when an operation is executed on an XDI statement or an XDI address.
 * 
 * @author markus
 */
public interface TargetInterceptor extends Interceptor<MessagingTarget> {

	/**
	 * Replaces or skips the target address before it is executed.
	 * @param targetXDIAddress The target address.
	 * @param operation The operation to process.
	 * @param messageResult The message result.
	 * @param executionContext The current execution context.
	 * @return The new target address, or the original target address, or null to skip.
	 */
	public XDIAddress targetAddress(XDIAddress targetXDIAddress, Operation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException, Xdi2PushRequiredException;

	/**
	 * Replaces or skips the target statement before it is executed.
	 * @param targetXDIStatement The target statement.
	 * @param operation The operation to process.
	 * @param messageResult The message result.
	 * @param executionContext The current execution context.
	 * @return The new target statement, or the original target statement, or null to skip.
	 */
	public XDIStatement targetStatement(XDIStatement targetXDIStatement, Operation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException, Xdi2PushRequiredException;
}
