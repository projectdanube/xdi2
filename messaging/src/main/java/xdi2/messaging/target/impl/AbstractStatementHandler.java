package xdi2.messaging.target.impl;

import xdi2.core.Statement;
import xdi2.core.Statement.ContextNodeStatement;
import xdi2.core.Statement.LiteralStatement;
import xdi2.core.Statement.RelationStatement;
import xdi2.messaging.AddOperation;
import xdi2.messaging.DelOperation;
import xdi2.messaging.GetOperation;
import xdi2.messaging.MessageResult;
import xdi2.messaging.ModOperation;
import xdi2.messaging.Operation;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.ExecutionContext;

/**
 * Checks what kind of statement and what kind of operation is being
 * executed ($add, $get, ...) and calls the appropriate executeXXX() method
 * 
 * @author markus
 */
public abstract class AbstractStatementHandler implements StatementHandler {

	@Override
	public boolean executeOnStatement(Statement targetStatement, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (operation instanceof GetOperation)
			return this.executeGetOnStatement(targetStatement, (GetOperation) operation, messageResult, executionContext);
		else if (operation instanceof AddOperation)
			return this.executeAddOnStatement(targetStatement, (AddOperation) operation, messageResult, executionContext);
		else if (operation instanceof ModOperation)
			return this.executeModOnStatement(targetStatement, (ModOperation) operation, messageResult, executionContext);
		else if (operation instanceof DelOperation)
			return this.executeDelOnStatement(targetStatement, (DelOperation) operation, messageResult, executionContext);
		else
			throw new Xdi2MessagingException("Unknown operation: " + operation.getOperationXri(), null, operation);
	}

	public boolean executeGetOnStatement(Statement targetStatement, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (targetStatement instanceof ContextNodeStatement)
			return this.executeGetOnContextNodeStatement((ContextNodeStatement) targetStatement, operation, messageResult, executionContext);
		else if (targetStatement instanceof RelationStatement)
			return this.executeGetOnRelationStatement((RelationStatement) targetStatement, operation, messageResult, executionContext);
		else if (targetStatement instanceof LiteralStatement)
			return this.executeGetOnLiteralStatement((LiteralStatement) targetStatement, operation, messageResult, executionContext);
		else
			throw new Xdi2MessagingException("Unknown statement type: " + targetStatement.getClass().getCanonicalName(), null, operation);
	}

	public boolean executeAddOnStatement(Statement targetStatement, AddOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (targetStatement instanceof ContextNodeStatement)
			return this.executeAddOnContextNodeStatement((ContextNodeStatement) targetStatement, operation, messageResult, executionContext);
		else if (targetStatement instanceof RelationStatement)
			return this.executeAddOnRelationStatement((RelationStatement) targetStatement, operation, messageResult, executionContext);
		else if (targetStatement instanceof LiteralStatement)
			return this.executeAddOnLiteralStatement((LiteralStatement) targetStatement, operation, messageResult, executionContext);
		else
			throw new Xdi2MessagingException("Unknown statement type: " + targetStatement.getClass().getCanonicalName(), null, operation);
	}

	public boolean executeModOnStatement(Statement targetStatement, ModOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (targetStatement instanceof ContextNodeStatement)
			return this.executeModOnContextNodeStatement((ContextNodeStatement) targetStatement, operation, messageResult, executionContext);
		else if (targetStatement instanceof RelationStatement)
			return this.executeModOnRelationStatement((RelationStatement) targetStatement, operation, messageResult, executionContext);
		else if (targetStatement instanceof LiteralStatement)
			return this.executeModOnLiteralStatement((LiteralStatement) targetStatement, operation, messageResult, executionContext);
		else
			throw new Xdi2MessagingException("Unknown statement type: " + targetStatement.getClass().getCanonicalName(), null, operation);
	}

	public boolean executeDelOnStatement(Statement targetStatement, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (targetStatement instanceof ContextNodeStatement)
			return this.executeDelOnContextNodeStatement((ContextNodeStatement) targetStatement, operation, messageResult, executionContext);
		else if (targetStatement instanceof RelationStatement)
			return this.executeDelOnRelationStatement((RelationStatement) targetStatement, operation, messageResult, executionContext);
		else if (targetStatement instanceof LiteralStatement)
			return this.executeDelOnLiteralStatement((LiteralStatement) targetStatement, operation, messageResult, executionContext);
		else
			throw new Xdi2MessagingException("Unknown statement type: " + targetStatement.getClass().getCanonicalName(), null, operation);
	}

	/*
	 * Operations on context nodes
	 */

	public boolean executeGetOnContextNodeStatement(ContextNodeStatement contextNodeStatement, GetOperation operation, MessageResult operationResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean executeAddOnContextNodeStatement(ContextNodeStatement contextNodeStatement, AddOperation operation, MessageResult operationResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean executeModOnContextNodeStatement(ContextNodeStatement contextNodeStatement, ModOperation operation, MessageResult operationResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean executeDelOnContextNodeStatement(ContextNodeStatement contextNodeStatement, DelOperation operation, MessageResult operationResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	/*
	 * Operations on relations
	 */

	public boolean executeGetOnRelationStatement(RelationStatement relationStatement, GetOperation operation, MessageResult operationResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean executeAddOnRelationStatement(RelationStatement relationStatement, AddOperation operation, MessageResult operationResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean executeModOnRelationStatement(RelationStatement relationStatement, ModOperation operation, MessageResult operationResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean executeDelOnRelationStatement(RelationStatement relationStatement, DelOperation operation, MessageResult operationResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	/*
	 * Operations on literals
	 */

	public boolean executeGetOnLiteralStatement(LiteralStatement literalStatement, GetOperation operation, MessageResult operationResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean executeAddOnLiteralStatement(LiteralStatement literalStatement, AddOperation operation, MessageResult operationResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean executeModOnLiteralStatement(LiteralStatement literalStatement, ModOperation operation, MessageResult operationResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean executeDelOnLiteralStatement(LiteralStatement literalStatement, DelOperation operation, MessageResult operationResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}
}
