package xdi2.messaging.target.impl;

import xdi2.core.Graph;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIStatement;
import xdi2.core.util.XDIAddressUtil;
import xdi2.messaging.operations.DelOperation;
import xdi2.messaging.operations.DoOperation;
import xdi2.messaging.operations.GetOperation;
import xdi2.messaging.operations.Operation;
import xdi2.messaging.operations.SetOperation;
import xdi2.messaging.target.ContextHandler;
import xdi2.messaging.target.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.execution.ExecutionContext;
import xdi2.messaging.target.impl.graph.GraphContextHandler;

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
	}

	public void executeGetOnAddress(XDIAddress targetXDIAddress, GetOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeSetOnAddress(XDIAddress targetXDIAddress, SetOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeDelOnAddress(XDIAddress targetXDIAddress, DelOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeDoOnAddress(XDIAddress targetXDIAddress, DoOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

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
}
