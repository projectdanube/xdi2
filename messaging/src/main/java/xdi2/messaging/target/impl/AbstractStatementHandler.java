package xdi2.messaging.target.impl;

import xdi2.core.Statement;
import xdi2.core.Statement.ContextNodeStatement;
import xdi2.core.Statement.LiteralStatement;
import xdi2.core.Statement.RelationStatement;
import xdi2.core.exceptions.Xdi2MessagingException;
import xdi2.messaging.AddOperation;
import xdi2.messaging.DelOperation;
import xdi2.messaging.GetOperation;
import xdi2.messaging.MessageResult;
import xdi2.messaging.ModOperation;
import xdi2.messaging.Operation;
import xdi2.messaging.target.ExecutionContext;

/**
 * Checks what kind of statement and what kind of operation is being
 * executed ($add, $get, ...) and calls the appropriate executeXXX() method
 * 
 * @author markus
 */
public abstract class AbstractStatementHandler implements StatementHandler {

	@Override
	public boolean executeOnStatement(Statement statement, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (operation instanceof GetOperation)
			return this.executeGetOnStatement(statement, (GetOperation) operation, messageResult, executionContext);
		else if (operation instanceof AddOperation)
			return this.executeAddOnStatement(statement, (AddOperation) operation, messageResult, executionContext);
		else if (operation instanceof ModOperation)
			return this.executeModOnStatement(statement, (ModOperation) operation, messageResult, executionContext);
		else if (operation instanceof DelOperation)
			return this.executeDelOnStatement(statement, (DelOperation) operation, messageResult, executionContext);
		else
			throw new Xdi2MessagingException("Unknown operation: " + operation.getOperationXri());
	}

	public boolean executeGetOnStatement(Statement statement, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (statement instanceof ContextNodeStatement)
			return this.executeGetOnContextNodeStatement((ContextNodeStatement) statement, operation, messageResult, executionContext);
		else if (statement instanceof RelationStatement)
			return this.executeGetOnRelationStatement((RelationStatement) statement, operation, messageResult, executionContext);
		else if (statement instanceof LiteralStatement)
			return this.executeGetOnLiteralStatement((LiteralStatement) statement, operation, messageResult, executionContext);
		else
			throw new Xdi2MessagingException("Unknown statement type: " + statement.getClass().getCanonicalName());
	}

	public boolean executeAddOnStatement(Statement statement, AddOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (statement instanceof ContextNodeStatement)
			return this.executeAddOnContextNodeStatement((ContextNodeStatement) statement, operation, messageResult, executionContext);
		else if (statement instanceof RelationStatement)
			return this.executeAddOnRelationStatement((RelationStatement) statement, operation, messageResult, executionContext);
		else if (statement instanceof LiteralStatement)
			return this.executeAddOnLiteralStatement((LiteralStatement) statement, operation, messageResult, executionContext);
		else
			throw new Xdi2MessagingException("Unknown statement type: " + statement.getClass().getCanonicalName());
	}

	public boolean executeModOnStatement(Statement statement, ModOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (statement instanceof ContextNodeStatement)
			return this.executeModOnContextNodeStatement((ContextNodeStatement) statement, operation, messageResult, executionContext);
		else if (statement instanceof RelationStatement)
			return this.executeModOnRelationStatement((RelationStatement) statement, operation, messageResult, executionContext);
		else if (statement instanceof LiteralStatement)
			return this.executeModOnLiteralStatement((LiteralStatement) statement, operation, messageResult, executionContext);
		else
			throw new Xdi2MessagingException("Unknown statement type: " + statement.getClass().getCanonicalName());
	}

	public boolean executeDelOnStatement(Statement statement, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (statement instanceof ContextNodeStatement)
			return this.executeDelOnContextNodeStatement((ContextNodeStatement) statement, operation, messageResult, executionContext);
		else if (statement instanceof RelationStatement)
			return this.executeDelOnRelationStatement((RelationStatement) statement, operation, messageResult, executionContext);
		else if (statement instanceof LiteralStatement)
			return this.executeDelOnLiteralStatement((LiteralStatement) statement, operation, messageResult, executionContext);
		else
			throw new Xdi2MessagingException("Unknown statement type: " + statement.getClass().getCanonicalName());
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
