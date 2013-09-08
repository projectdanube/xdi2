package xdi2.messaging.target;

import xdi2.core.util.XDI3Util;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;
import xdi2.messaging.DelOperation;
import xdi2.messaging.DoOperation;
import xdi2.messaging.GetOperation;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import xdi2.messaging.SetOperation;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.impl.graph.GraphContextHandler;

public abstract class AbstractContextHandler implements StatementHandler, AddressHandler {

	public AbstractContextHandler() {

	}

	/*
	 * Operations on addresses
	 */

	@Override
	public void executeOnAddress(XDI3Segment targetAddress, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// execute on address

		if (operation instanceof GetOperation)
			this.executeGetOnAddress(targetAddress, (GetOperation) operation, messageResult, executionContext);
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

	public void executeSetOnAddress(XDI3Segment targetAddress, SetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeDelOnAddress(XDI3Segment targetAddress, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeDoOnAddress(XDI3Segment targetAddress, DoOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	/*
	 * Operations on statements
	 */

	@Override
	public void executeOnStatement(XDI3Statement targetStatement, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// execute on statement

		if (operation instanceof GetOperation)
			this.executeGetOnStatement(targetStatement, (GetOperation) operation, messageResult, executionContext);
		else if (operation instanceof SetOperation)
			this.executeSetOnStatement(targetStatement, (SetOperation) operation, messageResult, executionContext);
		else if (operation instanceof DelOperation)
			this.executeDelOnStatement(targetStatement, (DelOperation) operation, messageResult, executionContext);
		else if (operation instanceof DoOperation)
			this.executeDoOnStatement(targetStatement, (DoOperation) operation, messageResult, executionContext);
		else
			throw new Xdi2MessagingException("Unknown operation: " + operation.getOperationXri(), null, executionContext);
	}

	public void executeGetOnStatement(XDI3Statement targetStatement, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (targetStatement.isContextNodeStatement())
			this.executeGetOnContextNodeStatement(targetStatement, operation, messageResult, executionContext);
		else if (targetStatement.isRelationStatement())
			this.executeGetOnRelationStatement(targetStatement, operation, messageResult, executionContext);
		else if (targetStatement.isLiteralStatement())
			this.executeGetOnLiteralStatement(targetStatement, operation, messageResult, executionContext);
		else
			throw new Xdi2MessagingException("Invalid statement: " + targetStatement, null, executionContext);
	}

	public void executeSetOnStatement(XDI3Statement targetStatement, SetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (targetStatement.isContextNodeStatement())
			this.executeSetOnContextNodeStatement(targetStatement, operation, messageResult, executionContext);
		else if (targetStatement.isRelationStatement())
			this.executeSetOnRelationStatement(targetStatement, operation, messageResult, executionContext);
		else if (targetStatement.isLiteralStatement())
			this.executeSetOnLiteralStatement(targetStatement, operation, messageResult, executionContext);
		else
			throw new Xdi2MessagingException("Invalid statement: " + targetStatement, null, executionContext);
	}

	public void executeDelOnStatement(XDI3Statement targetStatement, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (targetStatement.isContextNodeStatement())
			this.executeDelOnContextNodeStatement(targetStatement, operation, messageResult, executionContext);
		else if (targetStatement.isRelationStatement())
			this.executeDelOnRelationStatement(targetStatement, operation, messageResult, executionContext);
		else if (targetStatement.isLiteralStatement())
			this.executeDelOnLiteralStatement(targetStatement, operation, messageResult, executionContext);
		else
			throw new Xdi2MessagingException("Invalid statement: " + targetStatement, null, executionContext);
	}

	public void executeDoOnStatement(XDI3Statement targetStatement, DoOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (targetStatement.isContextNodeStatement())
			this.executeDoOnContextNodeStatement(targetStatement, operation, messageResult, executionContext);
		else if (targetStatement.isRelationStatement())
			this.executeDoOnRelationStatement(targetStatement, operation, messageResult, executionContext);
		else if (targetStatement.isLiteralStatement())
			this.executeDoOnLiteralStatement(targetStatement, operation, messageResult, executionContext);
		else
			throw new Xdi2MessagingException("Invalid statement: " + targetStatement, null, executionContext);
	}

	/*
	 * Operations on context node statements
	 */

	public void executeGetOnContextNodeStatement(XDI3Statement targetStatement, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment targetAddress = targetStatement.getContextNodeXri();

		MessageResult tempMessageResult = new MessageResult();

		this.executeGetOnAddress(targetAddress, operation, tempMessageResult, executionContext);

		new GraphContextHandler(tempMessageResult.getGraph()).executeGetOnContextNodeStatement(targetStatement, operation, messageResult, executionContext);
	}

	public void executeSetOnContextNodeStatement(XDI3Statement targetStatement, SetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment targetAddress = XDI3Util.concatXris(targetStatement.getContextNodeXri(), targetStatement.getContextNodeArcXri());

		this.executeSetOnAddress(targetAddress, operation, messageResult, executionContext);
	}

	public void executeDelOnContextNodeStatement(XDI3Statement targetStatement, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment targetAddress = XDI3Util.concatXris(targetStatement.getContextNodeXri(), targetStatement.getContextNodeArcXri());

		this.executeDelOnAddress(targetAddress, operation, messageResult, executionContext);
	}

	public void executeDoOnContextNodeStatement(XDI3Statement targetStatement, DoOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	/*
	 * Operations on relation statements
	 */

	public void executeGetOnRelationStatement(XDI3Statement targetStatement, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment targetAddress = targetStatement.getContextNodeXri();

		MessageResult tempMessageResult = new MessageResult();

		this.executeGetOnAddress(targetAddress, operation, tempMessageResult, executionContext);

		new GraphContextHandler(tempMessageResult.getGraph()).executeGetOnRelationStatement(targetStatement, operation, messageResult, executionContext);
	}

	public void executeSetOnRelationStatement(XDI3Statement targetStatement, SetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeDelOnRelationStatement(XDI3Statement targetStatement, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeDoOnRelationStatement(XDI3Statement targetStatement, DoOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	/*
	 * Operations on literal statements
	 */

	public void executeGetOnLiteralStatement(XDI3Statement targetStatement, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment targetAddress = targetStatement.getContextNodeXri();

		MessageResult tempMessageResult = new MessageResult();

		this.executeGetOnAddress(targetAddress, operation, tempMessageResult, executionContext);

		new GraphContextHandler(tempMessageResult.getGraph()).executeGetOnLiteralStatement(targetStatement, operation, messageResult, executionContext);
	}

	public void executeSetOnLiteralStatement(XDI3Statement targetStatement, SetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeDelOnLiteralStatement(XDI3Statement targetStatement, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeDoOnLiteralStatement(XDI3Statement targetStatement, DoOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}
}
