package xdi2.messaging.target;

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

/**
 * Checks what kind of statement and what kind of operation is being
 * executed ($add, $get, ...) and calls the appropriate executeXXX() method
 * 
 * @author markus
 */
public abstract class AbstractStatementHandler implements StatementHandler {

	/*
	 * Operations on statements
	 */

	@Override
	public final void executeOnStatement(Statement targetStatement, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (operation instanceof GetOperation)
			this.executeGetOnStatement(targetStatement, (GetOperation) operation, messageResult, executionContext);
		else if (operation instanceof AddOperation)
			this.executeAddOnStatement(targetStatement, (AddOperation) operation, messageResult, executionContext);
		else if (operation instanceof ModOperation)
			this.executeModOnStatement(targetStatement, (ModOperation) operation, messageResult, executionContext);
		else if (operation instanceof DelOperation)
			this.executeDelOnStatement(targetStatement, (DelOperation) operation, messageResult, executionContext);
		else 
			throw new Xdi2MessagingException("Unknown operation: " + operation.getOperationXri(), null, executionContext);
	}

	public void executeGetOnStatement(Statement targetStatement, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (targetStatement instanceof ContextNodeStatement)
			this.executeGetOnContextNodeStatement((ContextNodeStatement) targetStatement, operation, messageResult, executionContext);
		else if (targetStatement instanceof RelationStatement)
			this.executeGetOnRelationStatement((RelationStatement) targetStatement, operation, messageResult, executionContext);
		else if (targetStatement instanceof LiteralStatement)
			this.executeGetOnLiteralStatement((LiteralStatement) targetStatement, operation, messageResult, executionContext);
		else
			throw new Xdi2MessagingException("Unknown statement type: " + targetStatement.getClass().getCanonicalName(), null, executionContext);
	}

	public void executeAddOnStatement(Statement targetStatement, AddOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (targetStatement instanceof ContextNodeStatement)
			this.executeAddOnContextNodeStatement((ContextNodeStatement) targetStatement, operation, messageResult, executionContext);
		else if (targetStatement instanceof RelationStatement)
			this.executeAddOnRelationStatement((RelationStatement) targetStatement, operation, messageResult, executionContext);
		else if (targetStatement instanceof LiteralStatement)
			this.executeAddOnLiteralStatement((LiteralStatement) targetStatement, operation, messageResult, executionContext);
		else
			throw new Xdi2MessagingException("Unknown statement type: " + targetStatement.getClass().getCanonicalName(), null, executionContext);
	}

	public void executeModOnStatement(Statement targetStatement, ModOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (targetStatement instanceof ContextNodeStatement)
			this.executeModOnContextNodeStatement((ContextNodeStatement) targetStatement, operation, messageResult, executionContext);
		else if (targetStatement instanceof RelationStatement)
			this.executeModOnRelationStatement((RelationStatement) targetStatement, operation, messageResult, executionContext);
		else if (targetStatement instanceof LiteralStatement)
			this.executeModOnLiteralStatement((LiteralStatement) targetStatement, operation, messageResult, executionContext);
		else
			throw new Xdi2MessagingException("Unknown statement type: " + targetStatement.getClass().getCanonicalName(), null, executionContext);
	}

	public void executeDelOnStatement(Statement targetStatement, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (targetStatement instanceof ContextNodeStatement)
			this.executeDelOnContextNodeStatement((ContextNodeStatement) targetStatement, operation, messageResult, executionContext);
		else if (targetStatement instanceof RelationStatement)
			this.executeDelOnRelationStatement((RelationStatement) targetStatement, operation, messageResult, executionContext);
		else if (targetStatement instanceof LiteralStatement)
			this.executeDelOnLiteralStatement((LiteralStatement) targetStatement, operation, messageResult, executionContext);
		else
			throw new Xdi2MessagingException("Unknown statement type: " + targetStatement.getClass().getCanonicalName(), null, executionContext);
	}

	/*
	 * Operations on context node statements
	 */

	public void executeGetOnContextNodeStatement(ContextNodeStatement contextNodeStatement, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeAddOnContextNodeStatement(ContextNodeStatement contextNodeStatement, AddOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeModOnContextNodeStatement(ContextNodeStatement contextNodeStatement, ModOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeDelOnContextNodeStatement(ContextNodeStatement contextNodeStatement, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	/*
	 * Operations on relation statements
	 */

	public void executeGetOnRelationStatement(RelationStatement relationStatement, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeAddOnRelationStatement(RelationStatement relationStatement, AddOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeModOnRelationStatement(RelationStatement relationStatement, ModOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeDelOnRelationStatement(RelationStatement relationStatement, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	/*
	 * Operations on literal statements
	 */

	public void executeGetOnLiteralStatement(LiteralStatement literalStatement, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeAddOnLiteralStatement(LiteralStatement literalStatement, AddOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeModOnLiteralStatement(LiteralStatement literalStatement, ModOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeDelOnLiteralStatement(LiteralStatement literalStatement, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}
}
