package xdi2.messaging.target.impl;

import xdi2.core.syntax.XDIAddress;
import xdi2.messaging.DelOperation;
import xdi2.messaging.DoOperation;
import xdi2.messaging.GetOperation;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import xdi2.messaging.SetOperation;
import xdi2.messaging.context.ExecutionContext;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.AddressHandler;

/**
 * Checks what kind of operation is being
 * executed ($add, $get, ...) and calls the appropriate executeXXX() method
 * @deprecated Use AbstractContextHandler instead
 * @author markus
 */
@Deprecated
public class AbstractAddressHandler implements AddressHandler {

	/*
	 * Operations on addresses
	 */

	@Override
	public final void executeOnAddress(XDIAddress targetXDIAddress, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (operation instanceof GetOperation)
			this.executeGetOnAddress(targetXDIAddress, (GetOperation) operation, messageResult, executionContext);
		else if (operation instanceof SetOperation)
			this.executeSetOnAddress(targetXDIAddress, (SetOperation) operation, messageResult, executionContext);
		else if (operation instanceof DelOperation)
			this.executeDelOnAddress(targetXDIAddress, (DelOperation) operation, messageResult, executionContext);
		else if (operation instanceof DoOperation)
			this.executeDoOnAddress(targetXDIAddress, (DoOperation) operation, messageResult, executionContext);
		else
			throw new Xdi2MessagingException("Unknown operation: " + operation.getOperationXDIAddress(), null, executionContext);
	}

	public void executeGetOnAddress(XDIAddress targetXDIAddress, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeSetOnAddress(XDIAddress targetXDIAddress, SetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeDelOnAddress(XDIAddress targetXDIAddress, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeDoOnAddress(XDIAddress targetXDIAddress, DoOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}
}
