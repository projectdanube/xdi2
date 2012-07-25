package xdi2.messaging.target.impl;

import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.ExecutionContext;

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
	 * @param messageResult The message result to fill.
	 * @param executionContext An "execution context" object for the entire XDI message envelope.
	 * @return True, if the operation has been handled.
	 */
	public boolean executeOnAddress(XRI3Segment targetAddress, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException;
}
