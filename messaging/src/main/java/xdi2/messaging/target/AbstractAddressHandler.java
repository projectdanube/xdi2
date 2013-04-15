package xdi2.messaging.target;

import xdi2.core.xri3.XDI3Segment;
import xdi2.messaging.AddOperation;
import xdi2.messaging.DelOperation;
import xdi2.messaging.DoOperation;
import xdi2.messaging.GetOperation;
import xdi2.messaging.MessageResult;
import xdi2.messaging.ModOperation;
import xdi2.messaging.Operation;
import xdi2.messaging.SetOperation;
import xdi2.messaging.exceptions.Xdi2MessagingException;

/**
 * Checks what kind of operation is being
 * executed ($add, $get, ...) and calls the appropriate executeXXX() method
 * 
 * @author markus
 */
@Deprecated
public class AbstractAddressHandler implements AddressHandler {

	/*
	 * Operations on addresses
	 */

	@Override
	public final void executeOnAddress(XDI3Segment targetAddress, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (operation instanceof GetOperation)
			this.executeGetOnAddress(targetAddress, (GetOperation) operation, messageResult, executionContext);
		else if (operation instanceof AddOperation)
			this.executeAddOnAddress(targetAddress, (AddOperation) operation, messageResult, executionContext);
		else if (operation instanceof ModOperation)
			this.executeModOnAddress(targetAddress, (ModOperation) operation, messageResult, executionContext);
		else if (operation instanceof SetOperation)
			this.executeSetOnAddress(targetAddress, (SetOperation) operation, messageResult, executionContext);
		else if (operation instanceof DelOperation)
			this.executeDelOnAddress(targetAddress, (DelOperation) operation, messageResult, executionContext);
		else if (operation instanceof DoOperation)
			this.executeDoOnAddress(targetAddress, (DoOperation) operation, messageResult, executionContext);
		else
			throw new Xdi2MessagingException("Unknown operation: " + operation.getOperationXri(), null, executionContext);
	}

	public void executeGetOnAddress(XDI3Segment targetAddress, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeAddOnAddress(XDI3Segment targetAddress, AddOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeModOnAddress(XDI3Segment targetAddress, ModOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeSetOnAddress(XDI3Segment targetAddress, SetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeDelOnAddress(XDI3Segment targetAddress, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeDoOnAddress(XDI3Segment targetAddress, DoOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}
}
