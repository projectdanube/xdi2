package xdi2.messaging.target.impl;

import xdi2.core.syntax.XDIStatement;
import xdi2.messaging.AddOperation;
import xdi2.messaging.DelOperation;
import xdi2.messaging.DoOperation;
import xdi2.messaging.GetOperation;
import xdi2.messaging.MessageResult;
import xdi2.messaging.ModOperation;
import xdi2.messaging.Operation;
import xdi2.messaging.SetOperation;
import xdi2.messaging.context.ExecutionContext;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.StatementHandler;

/**
 * Checks what kind of statement and what kind of operation is being
 * executed ($add, $get, ...) and calls the appropriate executeXXX() method
 * @deprecated Use AbstractContextHandler instead
 * @author markus
 */
@Deprecated
public abstract class AbstractStatementHandler implements StatementHandler {

	/*
	 * Operations on statements
	 */

	@Override
	public final void executeOnStatement(XDIStatement targetStatement, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (operation instanceof GetOperation)
			this.executeGetOnStatement(targetStatement, (GetOperation) operation, messageResult, executionContext);
		else if (operation instanceof SetOperation)
			this.executeSetOnStatement(targetStatement, (SetOperation) operation, messageResult, executionContext);
		else if (operation instanceof AddOperation)
			this.executeAddOnStatement(targetStatement, (AddOperation) operation, messageResult, executionContext);
		else if (operation instanceof ModOperation)
			this.executeModOnStatement(targetStatement, (ModOperation) operation, messageResult, executionContext);
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
			throw new Xdi2MessagingException("Unknown statement type: " + targetStatement.getClass().getCanonicalName(), null, executionContext);
	}

	public void executeSetOnStatement(XDIStatement targetStatement, SetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (targetStatement.isContextNodeStatement())
			this.executeSetOnContextNodeStatement(targetStatement, operation, messageResult, executionContext);
		else if (targetStatement.isRelationStatement())
			this.executeSetOnRelationStatement(targetStatement, operation, messageResult, executionContext);
		else if (targetStatement.isLiteralStatement())
			this.executeSetOnLiteralStatement(targetStatement, operation, messageResult, executionContext);
		else
			throw new Xdi2MessagingException("Unknown statement type: " + targetStatement.getClass().getCanonicalName(), null, executionContext);
	}

	public void executeAddOnStatement(XDIStatement targetStatement, AddOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (targetStatement.isContextNodeStatement())
			this.executeAddOnContextNodeStatement(targetStatement, operation, messageResult, executionContext);
		else if (targetStatement.isRelationStatement())
			this.executeAddOnRelationStatement(targetStatement, operation, messageResult, executionContext);
		else if (targetStatement.isLiteralStatement())
			this.executeAddOnLiteralStatement(targetStatement, operation, messageResult, executionContext);
		else
			throw new Xdi2MessagingException("Unknown statement type: " + targetStatement.getClass().getCanonicalName(), null, executionContext);
	}

	public void executeModOnStatement(XDIStatement targetStatement, ModOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (targetStatement.isContextNodeStatement())
			this.executeModOnContextNodeStatement(targetStatement, operation, messageResult, executionContext);
		else if (targetStatement.isRelationStatement())
			this.executeModOnRelationStatement(targetStatement, operation, messageResult, executionContext);
		else if (targetStatement.isLiteralStatement())
			this.executeModOnLiteralStatement(targetStatement, operation, messageResult, executionContext);
		else
			throw new Xdi2MessagingException("Unknown statement type: " + targetStatement.getClass().getCanonicalName(), null, executionContext);
	}

	public void executeDelOnStatement(XDIStatement targetStatement, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (targetStatement.isContextNodeStatement())
			this.executeDelOnContextNodeStatement(targetStatement, operation, messageResult, executionContext);
		else if (targetStatement.isRelationStatement())
			this.executeDelOnRelationStatement(targetStatement, operation, messageResult, executionContext);
		else if (targetStatement.isLiteralStatement())
			this.executeDelOnLiteralStatement(targetStatement, operation, messageResult, executionContext);
		else
			throw new Xdi2MessagingException("Unknown statement type: " + targetStatement.getClass().getCanonicalName(), null, executionContext);
	}

	public void executeDoOnStatement(XDIStatement targetStatement, DoOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (targetStatement.isContextNodeStatement())
			this.executeDoOnContextNodeStatement(targetStatement, operation, messageResult, executionContext);
		else if (targetStatement.isRelationStatement())
			this.executeDoOnRelationStatement(targetStatement, operation, messageResult, executionContext);
		else if (targetStatement.isLiteralStatement())
			this.executeDoOnLiteralStatement(targetStatement, operation, messageResult, executionContext);
		else
			throw new Xdi2MessagingException("Unknown statement type: " + targetStatement.getClass().getCanonicalName(), null, executionContext);
	}

	/*
	 * Operations on context node statements
	 */

	public void executeGetOnContextNodeStatement(XDIStatement contextNodeStatement, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeSetOnContextNodeStatement(XDIStatement contextNodeStatement, SetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeAddOnContextNodeStatement(XDIStatement contextNodeStatement, AddOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeModOnContextNodeStatement(XDIStatement contextNodeStatement, ModOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeDelOnContextNodeStatement(XDIStatement contextNodeStatement, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeDoOnContextNodeStatement(XDIStatement contextNodeStatement, DoOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	/*
	 * Operations on relation statements
	 */

	public void executeGetOnRelationStatement(XDIStatement relationStatement, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeSetOnRelationStatement(XDIStatement relationStatement, SetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeAddOnRelationStatement(XDIStatement relationStatement, AddOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeModOnRelationStatement(XDIStatement relationStatement, ModOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeDelOnRelationStatement(XDIStatement relationStatement, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeDoOnRelationStatement(XDIStatement relationStatement, DoOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	/*
	 * Operations on literal statements
	 */

	public void executeGetOnLiteralStatement(XDIStatement literalStatement, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeSetOnLiteralStatement(XDIStatement literalStatement, SetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeAddOnLiteralStatement(XDIStatement literalStatement, AddOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeModOnLiteralStatement(XDIStatement literalStatement, ModOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeDelOnLiteralStatement(XDIStatement literalStatement, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeDoOnLiteralStatement(XDIStatement literalStatement, DoOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}
}
