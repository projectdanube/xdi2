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
	public void executeOnAddress(XDIAddress targetAddress, Operation operation, Graph resultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		// execute on address

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

	/*
	 * Operations on statements
	 */

	@Override
	public void executeOnStatement(XDIStatement targetStatement, Operation operation, Graph resultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		// execute on statement

		if (operation instanceof GetOperation)
			this.executeGetOnStatement(targetStatement, (GetOperation) operation, resultGraph, executionContext);
		else if (operation instanceof SetOperation)
			this.executeSetOnStatement(targetStatement, (SetOperation) operation, resultGraph, executionContext);
		else if (operation instanceof DelOperation)
			this.executeDelOnStatement(targetStatement, (DelOperation) operation, resultGraph, executionContext);
		else if (operation instanceof DoOperation)
			this.executeDoOnStatement(targetStatement, (DoOperation) operation, resultGraph, executionContext);
		else
			throw new Xdi2MessagingException("Unknown operation: " + operation.getOperationXDIAddress(), null, executionContext);
	}

	public void executeGetOnStatement(XDIStatement targetStatement, GetOperation operation, Graph resultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (targetStatement.isContextNodeStatement())
			this.executeGetOnContextNodeStatement(targetStatement, operation, resultGraph, executionContext);
		else if (targetStatement.isRelationStatement())
			this.executeGetOnRelationStatement(targetStatement, operation, resultGraph, executionContext);
		else if (targetStatement.isLiteralStatement())
			this.executeGetOnLiteralStatement(targetStatement, operation, resultGraph, executionContext);
		else
			throw new Xdi2MessagingException("Invalid statement: " + targetStatement, null, executionContext);
	}

	public void executeSetOnStatement(XDIStatement targetStatement, SetOperation operation, Graph resultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (targetStatement.isContextNodeStatement())
			this.executeSetOnContextNodeStatement(targetStatement, operation, resultGraph, executionContext);
		else if (targetStatement.isRelationStatement())
			this.executeSetOnRelationStatement(targetStatement, operation, resultGraph, executionContext);
		else if (targetStatement.isLiteralStatement())
			this.executeSetOnLiteralStatement(targetStatement, operation, resultGraph, executionContext);
		else
			throw new Xdi2MessagingException("Invalid statement: " + targetStatement, null, executionContext);
	}

	public void executeDelOnStatement(XDIStatement targetStatement, DelOperation operation, Graph resultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (targetStatement.isContextNodeStatement())
			this.executeDelOnContextNodeStatement(targetStatement, operation, resultGraph, executionContext);
		else if (targetStatement.isRelationStatement())
			this.executeDelOnRelationStatement(targetStatement, operation, resultGraph, executionContext);
		else if (targetStatement.isLiteralStatement())
			this.executeDelOnLiteralStatement(targetStatement, operation, resultGraph, executionContext);
		else
			throw new Xdi2MessagingException("Invalid statement: " + targetStatement, null, executionContext);
	}

	public void executeDoOnStatement(XDIStatement targetStatement, DoOperation operation, Graph resultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (targetStatement.isContextNodeStatement())
			this.executeDoOnContextNodeStatement(targetStatement, operation, resultGraph, executionContext);
		else if (targetStatement.isRelationStatement())
			this.executeDoOnRelationStatement(targetStatement, operation, resultGraph, executionContext);
		else if (targetStatement.isLiteralStatement())
			this.executeDoOnLiteralStatement(targetStatement, operation, resultGraph, executionContext);
		else
			throw new Xdi2MessagingException("Invalid statement: " + targetStatement, null, executionContext);
	}

	/*
	 * Operations on context node statements
	 */

	public void executeGetOnContextNodeStatement(XDIStatement targetStatement, GetOperation operation, Graph resultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDIAddress targetAddress = targetStatement.getContextNodeXDIAddress();

		Graph tempResultGraph = MemoryGraphFactory.getInstance().openGraph();

		this.executeGetOnAddress(targetAddress, operation, tempResultGraph, executionContext);

		new GraphContextHandler(tempResultGraph).executeGetOnContextNodeStatement(targetStatement, operation, resultGraph, executionContext);
	}

	public void executeSetOnContextNodeStatement(XDIStatement targetStatement, SetOperation operation, Graph resultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDIAddress targetAddress = XDIAddressUtil.concatXDIAddresses(targetStatement.getContextNodeXDIAddress(), targetStatement.getContextNodeXDIArc());

		this.executeSetOnAddress(targetAddress, operation, resultGraph, executionContext);
	}

	public void executeDelOnContextNodeStatement(XDIStatement targetStatement, DelOperation operation, Graph resultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDIAddress targetAddress = XDIAddressUtil.concatXDIAddresses(targetStatement.getContextNodeXDIAddress(), targetStatement.getContextNodeXDIArc());

		this.executeDelOnAddress(targetAddress, operation, resultGraph, executionContext);
	}

	public void executeDoOnContextNodeStatement(XDIStatement targetStatement, DoOperation operation, Graph resultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	/*
	 * Operations on relation statements
	 */

	public void executeGetOnRelationStatement(XDIStatement targetStatement, GetOperation operation, Graph resultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDIAddress targetAddress = targetStatement.getContextNodeXDIAddress();

		Graph tempResultGraph = MemoryGraphFactory.getInstance().openGraph();

		this.executeGetOnAddress(targetAddress, operation, tempResultGraph, executionContext);

		new GraphContextHandler(tempResultGraph).executeGetOnRelationStatement(targetStatement, operation, resultGraph, executionContext);
	}

	public void executeSetOnRelationStatement(XDIStatement targetStatement, SetOperation operation, Graph resultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeDelOnRelationStatement(XDIStatement targetStatement, DelOperation operation, Graph resultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeDoOnRelationStatement(XDIStatement targetStatement, DoOperation operation, Graph resultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	/*
	 * Operations on literal statements
	 */

	public void executeGetOnLiteralStatement(XDIStatement targetStatement, GetOperation operation, Graph resultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDIAddress targetAddress = targetStatement.getContextNodeXDIAddress();

		Graph tempResultGraph = MemoryGraphFactory.getInstance().openGraph();

		this.executeGetOnAddress(targetAddress, operation, tempResultGraph, executionContext);

		new GraphContextHandler(tempResultGraph).executeGetOnLiteralStatement(targetStatement, operation, resultGraph, executionContext);
	}

	public void executeSetOnLiteralStatement(XDIStatement targetStatement, SetOperation operation, Graph resultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeDelOnLiteralStatement(XDIStatement targetStatement, DelOperation operation, Graph resultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeDoOnLiteralStatement(XDIStatement targetStatement, DoOperation operation, Graph resultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

	}
}
