package xdi2.messaging.container.impl;

import xdi2.core.Graph;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIStatement;
import xdi2.core.util.XDIAddressUtil;
import xdi2.messaging.container.ContextHandler;
import xdi2.messaging.container.exceptions.Xdi2MessagingException;
import xdi2.messaging.container.execution.ExecutionContext;
import xdi2.messaging.container.impl.graph.GraphContextHandler;
import xdi2.messaging.operations.ConnectOperation;
import xdi2.messaging.operations.DelOperation;
import xdi2.messaging.operations.DoOperation;
import xdi2.messaging.operations.GetOperation;
import xdi2.messaging.operations.Operation;
import xdi2.messaging.operations.PushOperation;
import xdi2.messaging.operations.SendOperation;
import xdi2.messaging.operations.SetOperation;

public abstract class AbstractContextHandler implements ContextHandler {

	public AbstractContextHandler() {

	}

	/*
	 * Operations on addresses
	 */

	@Override
	public final void executeOnAddress(XDIAddress targetXDIAddress, Operation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		// execute on address

		if (operation instanceof GetOperation)
			this.executeGetOnAddress(targetXDIAddress, (GetOperation) operation, operationResultGraph, executionContext);
		else if (operation instanceof SetOperation)
			this.executeSetOnAddress(targetXDIAddress, (SetOperation) operation, operationResultGraph, executionContext);
		else if (operation instanceof DelOperation)
			this.executeDelOnAddress(targetXDIAddress, (DelOperation) operation, operationResultGraph, executionContext);
		else if (operation instanceof DoOperation)
			this.executeDoOnAddress(targetXDIAddress, (DoOperation) operation, operationResultGraph, executionContext);
		else if (operation instanceof ConnectOperation)
			this.executeConnectOnAddress(targetXDIAddress, (ConnectOperation) operation, operationResultGraph, executionContext);
		else if (operation instanceof SendOperation)
			this.executeSendOnAddress(targetXDIAddress, (SendOperation) operation, operationResultGraph, executionContext);
		else if (operation instanceof PushOperation)
			this.executePushOnAddress(targetXDIAddress, (PushOperation) operation, operationResultGraph, executionContext);
		else
			throw new Xdi2MessagingException("Unknown operation: " + operation.getOperationXDIAddress(), null, executionContext);
	}

	public void executeGetOnAddress(XDIAddress targetXDIAddress, GetOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeSetOnAddress(XDIAddress targetXDIAddress, SetOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeDelOnAddress(XDIAddress targetXDIAddress, DelOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeDoOnAddress(XDIAddress targetXDIAddress, DoOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeConnectOnAddress(XDIAddress targetXDIAddress, ConnectOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeSendOnAddress(XDIAddress targetXDIAddress, SendOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executePushOnAddress(XDIAddress targetXDIAddress, PushOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	/*
	 * Operations on statements
	 */

	@Override
	public void executeOnStatement(XDIStatement targetStatement, Operation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		// execute on statement

		if (operation instanceof GetOperation)
			this.executeGetOnStatement(targetStatement, (GetOperation) operation, operationResultGraph, executionContext);
		else if (operation instanceof SetOperation)
			this.executeSetOnStatement(targetStatement, (SetOperation) operation, operationResultGraph, executionContext);
		else if (operation instanceof DelOperation)
			this.executeDelOnStatement(targetStatement, (DelOperation) operation, operationResultGraph, executionContext);
		else if (operation instanceof DoOperation)
			this.executeDoOnStatement(targetStatement, (DoOperation) operation, operationResultGraph, executionContext);
		else if (operation instanceof ConnectOperation)
			this.executeConnectOnStatement(targetStatement, (ConnectOperation) operation, operationResultGraph, executionContext);
		else if (operation instanceof SendOperation)
			this.executeSendOnStatement(targetStatement, (SendOperation) operation, operationResultGraph, executionContext);
		else if (operation instanceof PushOperation)
			this.executePushOnStatement(targetStatement, (PushOperation) operation, operationResultGraph, executionContext);
		else
			throw new Xdi2MessagingException("Unknown operation: " + operation.getOperationXDIAddress(), null, executionContext);
	}

	public void executeGetOnStatement(XDIStatement targetStatement, GetOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (targetStatement.isContextNodeStatement())
			this.executeGetOnContextNodeStatement(targetStatement, operation, operationResultGraph, executionContext);
		else if (targetStatement.isRelationStatement())
			this.executeGetOnRelationStatement(targetStatement, operation, operationResultGraph, executionContext);
		else if (targetStatement.isLiteralStatement())
			this.executeGetOnLiteralStatement(targetStatement, operation, operationResultGraph, executionContext);
		else
			throw new Xdi2MessagingException("Invalid statement: " + targetStatement, null, executionContext);
	}

	public void executeSetOnStatement(XDIStatement targetStatement, SetOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (targetStatement.isContextNodeStatement())
			this.executeSetOnContextNodeStatement(targetStatement, operation, operationResultGraph, executionContext);
		else if (targetStatement.isRelationStatement())
			this.executeSetOnRelationStatement(targetStatement, operation, operationResultGraph, executionContext);
		else if (targetStatement.isLiteralStatement())
			this.executeSetOnLiteralStatement(targetStatement, operation, operationResultGraph, executionContext);
		else
			throw new Xdi2MessagingException("Invalid statement: " + targetStatement, null, executionContext);
	}

	public void executeDelOnStatement(XDIStatement targetStatement, DelOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (targetStatement.isContextNodeStatement())
			this.executeDelOnContextNodeStatement(targetStatement, operation, operationResultGraph, executionContext);
		else if (targetStatement.isRelationStatement())
			this.executeDelOnRelationStatement(targetStatement, operation, operationResultGraph, executionContext);
		else if (targetStatement.isLiteralStatement())
			this.executeDelOnLiteralStatement(targetStatement, operation, operationResultGraph, executionContext);
		else
			throw new Xdi2MessagingException("Invalid statement: " + targetStatement, null, executionContext);
	}

	public void executeDoOnStatement(XDIStatement targetStatement, DoOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (targetStatement.isContextNodeStatement())
			this.executeDoOnContextNodeStatement(targetStatement, operation, operationResultGraph, executionContext);
		else if (targetStatement.isRelationStatement())
			this.executeDoOnRelationStatement(targetStatement, operation, operationResultGraph, executionContext);
		else if (targetStatement.isLiteralStatement())
			this.executeDoOnLiteralStatement(targetStatement, operation, operationResultGraph, executionContext);
		else
			throw new Xdi2MessagingException("Invalid statement: " + targetStatement, null, executionContext);
	}

	public void executeConnectOnStatement(XDIStatement targetStatement, ConnectOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (targetStatement.isContextNodeStatement())
			this.executeConnectOnContextNodeStatement(targetStatement, operation, operationResultGraph, executionContext);
		else if (targetStatement.isRelationStatement())
			this.executeConnectOnRelationStatement(targetStatement, operation, operationResultGraph, executionContext);
		else if (targetStatement.isLiteralStatement())
			this.executeConnectOnLiteralStatement(targetStatement, operation, operationResultGraph, executionContext);
		else
			throw new Xdi2MessagingException("Invalid statement: " + targetStatement, null, executionContext);
	}

	public void executeSendOnStatement(XDIStatement targetStatement, SendOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (targetStatement.isContextNodeStatement())
			this.executeSendOnContextNodeStatement(targetStatement, operation, operationResultGraph, executionContext);
		else if (targetStatement.isRelationStatement())
			this.executeSendOnRelationStatement(targetStatement, operation, operationResultGraph, executionContext);
		else if (targetStatement.isLiteralStatement())
			this.executeSendOnLiteralStatement(targetStatement, operation, operationResultGraph, executionContext);
		else
			throw new Xdi2MessagingException("Invalid statement: " + targetStatement, null, executionContext);
	}

	public void executePushOnStatement(XDIStatement targetStatement, PushOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (targetStatement.isContextNodeStatement())
			this.executePushOnContextNodeStatement(targetStatement, operation, operationResultGraph, executionContext);
		else if (targetStatement.isRelationStatement())
			this.executePushOnRelationStatement(targetStatement, operation, operationResultGraph, executionContext);
		else if (targetStatement.isLiteralStatement())
			this.executePushOnLiteralStatement(targetStatement, operation, operationResultGraph, executionContext);
		else
			throw new Xdi2MessagingException("Invalid statement: " + targetStatement, null, executionContext);
	}

	/*
	 * Operations on context node statements
	 */

	public void executeGetOnContextNodeStatement(XDIStatement targetStatement, GetOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDIAddress targetXDIAddress = targetStatement.getContextNodeXDIAddress();

		Graph tempOperationResultGraph = MemoryGraphFactory.getInstance().openGraph();

		this.executeGetOnAddress(targetXDIAddress, operation, tempOperationResultGraph, executionContext);

		new GraphContextHandler(tempOperationResultGraph).executeGetOnContextNodeStatement(targetStatement, operation, operationResultGraph, executionContext);
	}

	public void executeSetOnContextNodeStatement(XDIStatement targetStatement, SetOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDIAddress targetXDIAddress = XDIAddressUtil.concatXDIAddresses(targetStatement.getTargetXDIAddress());

		this.executeSetOnAddress(targetXDIAddress, operation, operationResultGraph, executionContext);
	}

	public void executeDelOnContextNodeStatement(XDIStatement targetStatement, DelOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDIAddress targetXDIAddress = XDIAddressUtil.concatXDIAddresses(targetStatement.getContextNodeXDIAddress(), targetStatement.getContextNodeXDIArc());

		this.executeDelOnAddress(targetXDIAddress, operation, operationResultGraph, executionContext);
	}

	public void executeDoOnContextNodeStatement(XDIStatement targetStatement, DoOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeConnectOnContextNodeStatement(XDIStatement targetStatement, ConnectOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeSendOnContextNodeStatement(XDIStatement targetStatement, SendOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executePushOnContextNodeStatement(XDIStatement targetStatement, PushOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	/*
	 * Operations on relation statements
	 */

	public void executeGetOnRelationStatement(XDIStatement targetStatement, GetOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDIAddress targetXDIAddress = targetStatement.getContextNodeXDIAddress();

		Graph tempOperationResultGraph = MemoryGraphFactory.getInstance().openGraph();

		this.executeGetOnAddress(targetXDIAddress, operation, tempOperationResultGraph, executionContext);

		new GraphContextHandler(tempOperationResultGraph).executeGetOnRelationStatement(targetStatement, operation, operationResultGraph, executionContext);
	}

	public void executeSetOnRelationStatement(XDIStatement targetStatement, SetOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeDelOnRelationStatement(XDIStatement targetStatement, DelOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeDoOnRelationStatement(XDIStatement targetStatement, DoOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeConnectOnRelationStatement(XDIStatement targetStatement, ConnectOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeSendOnRelationStatement(XDIStatement targetStatement, SendOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executePushOnRelationStatement(XDIStatement targetStatement, PushOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	/*
	 * Operations on literal statements
	 */

	public void executeGetOnLiteralStatement(XDIStatement targetStatement, GetOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDIAddress targetXDIAddress = targetStatement.getContextNodeXDIAddress();

		Graph tempOperationResultGraph = MemoryGraphFactory.getInstance().openGraph();

		this.executeGetOnAddress(targetXDIAddress, operation, tempOperationResultGraph, executionContext);

		new GraphContextHandler(tempOperationResultGraph).executeGetOnLiteralStatement(targetStatement, operation, operationResultGraph, executionContext);
	}

	public void executeSetOnLiteralStatement(XDIStatement targetStatement, SetOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeDelOnLiteralStatement(XDIStatement targetStatement, DelOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeDoOnLiteralStatement(XDIStatement targetStatement, DoOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeConnectOnLiteralStatement(XDIStatement targetStatement, ConnectOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeSendOnLiteralStatement(XDIStatement targetStatement, SendOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executePushOnLiteralStatement(XDIStatement targetStatement, PushOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

	}
}
