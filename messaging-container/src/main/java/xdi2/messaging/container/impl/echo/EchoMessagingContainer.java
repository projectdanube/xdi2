package xdi2.messaging.container.impl.echo;

import xdi2.core.Graph;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIStatement;
import xdi2.core.util.CopyUtil;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.container.AddressHandler;
import xdi2.messaging.container.StatementHandler;
import xdi2.messaging.container.exceptions.Xdi2MessagingException;
import xdi2.messaging.container.execution.ExecutionContext;
import xdi2.messaging.container.execution.ExecutionResult;
import xdi2.messaging.container.impl.AbstractMessagingContainer;
import xdi2.messaging.operations.Operation;

public class EchoMessagingContainer extends AbstractMessagingContainer {

	public EchoMessagingContainer() {

	}

	@Override
	public void execute(MessageEnvelope messageEnvelope, ExecutionContext executionContext, ExecutionResult executionResult) throws Xdi2MessagingException {

		for (Operation operation : messageEnvelope.getOperations()) {

			Graph operationResultGraph = executionResult.createOperationResultGraph(operation);

			CopyUtil.copyContextNode(operation.getMessage().getContextNode(), operationResultGraph, null);
		}

		executionResult.finish();
	}

	@Override
	public AddressHandler getAddressHandler(XDIAddress targetAddress) throws Xdi2MessagingException {

		return null;
	}

	@Override
	public StatementHandler getStatementHandler(XDIStatement targetStatement) throws Xdi2MessagingException {

		return null;
	}
}
