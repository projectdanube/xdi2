package xdi2.messaging.target.impl;

import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIStatement;
import xdi2.core.util.XDIAddressUtil;
import xdi2.messaging.DelOperation;
import xdi2.messaging.DoOperation;
import xdi2.messaging.GetOperation;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import xdi2.messaging.SetOperation;
import xdi2.messaging.context.ExecutionContext;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.ContextHandler;
import xdi2.messaging.target.impl.graph.GraphContextHandler;

public abstract class AbstractContextHandler implements ContextHandler {

	public AbstractContextHandler() {

	}

	/*
	 * Operations on addresses
	 */

	@Override
	public void executeOnAddress(XDIAddress targetXDIAddress, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// execute on address

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

	/*
	 * Operations on statements
	 */

	@Override
	public void executeOnStatement(XDIStatement targetStatement, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

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
			throw new Xdi2MessagingException("Unknown operation: " + operation.getOperationXDIAddress(), null, executionContext);
	}

	public void executeGetOnStatement(XDIStatement targetStatement, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (targetStatement.isContextNodeStatement())
			this.executeGetOnContextNodeStatement(targetStatement, operation, messageResult, executionContext);
		else if (targetStatement.isRelationStatement())
			this.executeGetOnRelationStatement(targetStatement, operation, messageResult, executionContext);
		else if (targetStatement.isLiteralStatement())
			this.executeGetOnLiteralStatement(targetStatement, operation, messageResult, executionContext);
		else
			throw new Xdi2MessagingException("Invalid statement: " + targetStatement, null, executionContext);
	}

	public void executeSetOnStatement(XDIStatement targetStatement, SetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (targetStatement.isContextNodeStatement())
			this.executeSetOnContextNodeStatement(targetStatement, operation, messageResult, executionContext);
		else if (targetStatement.isRelationStatement())
			this.executeSetOnRelationStatement(targetStatement, operation, messageResult, executionContext);
		else if (targetStatement.isLiteralStatement())
			this.executeSetOnLiteralStatement(targetStatement, operation, messageResult, executionContext);
		else
			throw new Xdi2MessagingException("Invalid statement: " + targetStatement, null, executionContext);
	}

	public void executeDelOnStatement(XDIStatement targetStatement, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (targetStatement.isContextNodeStatement())
			this.executeDelOnContextNodeStatement(targetStatement, operation, messageResult, executionContext);
		else if (targetStatement.isRelationStatement())
			this.executeDelOnRelationStatement(targetStatement, operation, messageResult, executionContext);
		else if (targetStatement.isLiteralStatement())
			this.executeDelOnLiteralStatement(targetStatement, operation, messageResult, executionContext);
		else
			throw new Xdi2MessagingException("Invalid statement: " + targetStatement, null, executionContext);
	}

	public void executeDoOnStatement(XDIStatement targetStatement, DoOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

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

	public void executeGetOnContextNodeStatement(XDIStatement targetStatement, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDIAddress targetXDIAddress = targetStatement.getContextNodeXDIAddress();

		MessageResult tempMessageResult = new MessageResult();

		this.executeGetOnAddress(targetXDIAddress, operation, tempMessageResult, executionContext);

		new GraphContextHandler(tempMessageResult.getGraph()).executeGetOnContextNodeStatement(targetStatement, operation, messageResult, executionContext);
	}

	public void executeSetOnContextNodeStatement(XDIStatement targetStatement, SetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDIAddress targetXDIAddress = XDIAddressUtil.concatXDIAddresses(targetStatement.getTargetXDIAddress());

		this.executeSetOnAddress(targetXDIAddress, operation, messageResult, executionContext);
	}

	public void executeDelOnContextNodeStatement(XDIStatement targetStatement, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDIAddress targetXDIAddress = XDIAddressUtil.concatXDIAddresses(targetStatement.getContextNodeXDIAddress(), targetStatement.getContextNodeXDIArc());

		this.executeDelOnAddress(targetXDIAddress, operation, messageResult, executionContext);
	}

	public void executeDoOnContextNodeStatement(XDIStatement targetStatement, DoOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	/*
	 * Operations on relation statements
	 */

	public void executeGetOnRelationStatement(XDIStatement targetStatement, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDIAddress targetXDIAddress = targetStatement.getContextNodeXDIAddress();

		MessageResult tempMessageResult = new MessageResult();

		this.executeGetOnAddress(targetXDIAddress, operation, tempMessageResult, executionContext);

		new GraphContextHandler(tempMessageResult.getGraph()).executeGetOnRelationStatement(targetStatement, operation, messageResult, executionContext);
	}

	public void executeSetOnRelationStatement(XDIStatement targetStatement, SetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeDelOnRelationStatement(XDIStatement targetStatement, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeDoOnRelationStatement(XDIStatement targetStatement, DoOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	/*
	 * Operations on literal statements
	 */

	public void executeGetOnLiteralStatement(XDIStatement targetStatement, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDIAddress targetXDIAddress = targetStatement.getContextNodeXDIAddress();

		MessageResult tempMessageResult = new MessageResult();

		this.executeGetOnAddress(targetXDIAddress, operation, tempMessageResult, executionContext);

		new GraphContextHandler(tempMessageResult.getGraph()).executeGetOnLiteralStatement(targetStatement, operation, messageResult, executionContext);
	}

	public void executeSetOnLiteralStatement(XDIStatement targetStatement, SetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeDelOnLiteralStatement(XDIStatement targetStatement, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeDoOnLiteralStatement(XDIStatement targetStatement, DoOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}
}
