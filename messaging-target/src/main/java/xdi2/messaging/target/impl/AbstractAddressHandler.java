package xdi2.messaging.target.impl;

import xdi2.core.Graph;
import xdi2.core.syntax.XDIAddress;
import xdi2.messaging.operations.DelOperation;
import xdi2.messaging.operations.DoOperation;
import xdi2.messaging.operations.GetOperation;
import xdi2.messaging.operations.Operation;
import xdi2.messaging.operations.SetOperation;
import xdi2.messaging.target.AddressHandler;
import xdi2.messaging.target.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.execution.ExecutionContext;

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
	public final void executeOnAddress(XDIAddress targetXDIAddress, Operation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (operation instanceof GetOperation)
			this.executeGetOnAddress(targetXDIAddress, (GetOperation) operation, operationResultGraph, executionContext);
		else if (operation instanceof SetOperation)
			this.executeSetOnAddress(targetXDIAddress, (SetOperation) operation, operationResultGraph, executionContext);
		else if (operation instanceof DelOperation)
			this.executeDelOnAddress(targetXDIAddress, (DelOperation) operation, operationResultGraph, executionContext);
		else if (operation instanceof DoOperation)
			this.executeDoOnAddress(targetXDIAddress, (DoOperation) operation, operationResultGraph, executionContext);
	}

	public void executeGetOnAddress(XDIAddress targetXDIAddress, GetOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeSetOnAddress(XDIAddress targetXDIAddress, SetOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeDelOnAddress(XDIAddress targetXDIAddress, DelOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeDoOnAddress(XDIAddress targetXDIAddress, DoOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

	}
}
