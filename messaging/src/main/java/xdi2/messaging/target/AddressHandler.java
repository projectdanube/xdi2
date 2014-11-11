package xdi2.messaging.target;

import xdi2.core.Graph;
import xdi2.core.syntax.XDIAddress;
import xdi2.messaging.context.ExecutionContext;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.operations.Operation;

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
	 * @param resultGraph The result graph.
	 * @param executionContext An "execution context" object for the entire XDI message envelope.
	 */
	public void executeOnAddress(XDIAddress targetAddress, Operation operation, Graph resultGraph, ExecutionContext executionContext) throws Xdi2MessagingException;
}
