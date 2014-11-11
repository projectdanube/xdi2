package xdi2.messaging.target.impl;

import xdi2.core.Graph;
import xdi2.core.syntax.XDIAddress;
import xdi2.messaging.context.ExecutionContext;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.operations.DelOperation;
import xdi2.messaging.operations.DoOperation;
import xdi2.messaging.operations.GetOperation;
import xdi2.messaging.operations.Operation;
import xdi2.messaging.operations.SetOperation;
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
	public final void executeOnAddress(XDIAddress targetAddress, Operation operation, Graph resultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (operation instanceof GetOperation)
			this.executeGetOnAddress(targetAddress, (GetOperation) operation, resultGraph, executionContext);
		else if (operation instanceof SetOperation)
			this.executeSetOnAddress(targetAddress, (SetOperation) operation, resultGraph, executionContext);
		else if (operation instanceof DelOperation)
			this.executeDelOnAddress(targetAddress, (DelOperation) operation, resultGraph, executionContext);
		else if (operation instanceof DoOperation)
			this.executeDoOnAddress(targetAddress, (DoOperation) operation, resultGraph, executionContext);
		else
			throw new Xdi2MessagingException("Unknown operation: " + operation.getOperationXDIAddress(), null, executionContext);
	}

	public void executeGetOnAddress(XDIAddress targetAddress, GetOperation operation, Graph resultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeSetOnAddress(XDIAddress targetAddress, SetOperation operation, Graph resultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeDelOnAddress(XDIAddress targetAddress, DelOperation operation, Graph resultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeDoOnAddress(XDIAddress targetAddress, DoOperation operation, Graph resultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

	}
}
