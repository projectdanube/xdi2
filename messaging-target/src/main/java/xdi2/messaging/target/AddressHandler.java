package xdi2.messaging.target;

import xdi2.core.Graph;
import xdi2.core.syntax.XDIAddress;
import xdi2.messaging.operations.Operation;
import xdi2.messaging.target.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.exceptions.Xdi2PushRequiredException;
import xdi2.messaging.target.execution.ExecutionContext;

/**
 * An AddressHandler can execute an XDI operation against an address.
 * 
 * The AbstractMessagingTarget requests AddressHandler implementations for each
 * address given as part of an operation.
 * 
 * @author markus
 */
public interface AddressHandler {

	/**
	 * Executes an XDI operation on an address.
	 * @param targetAddress The target address.
	 * @param operation The operation that is being executed.
	 * @param operationResultGraph The result graph.
	 * @param executionContext An "execution context" object for the entire XDI message envelope.
	 */
	public void executeOnAddress(XDIAddress targetAddress, Operation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException, Xdi2PushRequiredException;
}
