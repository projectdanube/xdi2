package xdi2.messaging.target.impl;

import xdi2.core.Graph;
import xdi2.core.syntax.XDIStatement;
import xdi2.messaging.operations.DelOperation;
import xdi2.messaging.operations.DoOperation;
import xdi2.messaging.operations.GetOperation;
import xdi2.messaging.operations.Operation;
import xdi2.messaging.operations.SetOperation;
import xdi2.messaging.target.StatementHandler;
import xdi2.messaging.target.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.execution.ExecutionContext;

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
	public final void executeOnStatement(XDIStatement targetStatement, Operation operation, Graph resultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

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
			throw new Xdi2MessagingException("Unknown statement type: " + targetStatement.getClass().getCanonicalName(), null, executionContext);
	}

	public void executeSetOnStatement(XDIStatement targetStatement, SetOperation operation, Graph resultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (targetStatement.isContextNodeStatement())
			this.executeSetOnContextNodeStatement(targetStatement, operation, resultGraph, executionContext);
		else if (targetStatement.isRelationStatement())
			this.executeSetOnRelationStatement(targetStatement, operation, resultGraph, executionContext);
		else if (targetStatement.isLiteralStatement())
			this.executeSetOnLiteralStatement(targetStatement, operation, resultGraph, executionContext);
		else
			throw new Xdi2MessagingException("Unknown statement type: " + targetStatement.getClass().getCanonicalName(), null, executionContext);
	}

	public void executeDelOnStatement(XDIStatement targetStatement, DelOperation operation, Graph resultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (targetStatement.isContextNodeStatement())
			this.executeDelOnContextNodeStatement(targetStatement, operation, resultGraph, executionContext);
		else if (targetStatement.isRelationStatement())
			this.executeDelOnRelationStatement(targetStatement, operation, resultGraph, executionContext);
		else if (targetStatement.isLiteralStatement())
			this.executeDelOnLiteralStatement(targetStatement, operation, resultGraph, executionContext);
		else
			throw new Xdi2MessagingException("Unknown statement type: " + targetStatement.getClass().getCanonicalName(), null, executionContext);
	}

	public void executeDoOnStatement(XDIStatement targetStatement, DoOperation operation, Graph resultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (targetStatement.isContextNodeStatement())
			this.executeDoOnContextNodeStatement(targetStatement, operation, resultGraph, executionContext);
		else if (targetStatement.isRelationStatement())
			this.executeDoOnRelationStatement(targetStatement, operation, resultGraph, executionContext);
		else if (targetStatement.isLiteralStatement())
			this.executeDoOnLiteralStatement(targetStatement, operation, resultGraph, executionContext);
		else
			throw new Xdi2MessagingException("Unknown statement type: " + targetStatement.getClass().getCanonicalName(), null, executionContext);
	}

	/*
	 * Operations on context node statements
	 */

	public void executeGetOnContextNodeStatement(XDIStatement contextNodeStatement, GetOperation operation, Graph resultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeSetOnContextNodeStatement(XDIStatement contextNodeStatement, SetOperation operation, Graph resultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeDelOnContextNodeStatement(XDIStatement contextNodeStatement, DelOperation operation, Graph resultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeDoOnContextNodeStatement(XDIStatement contextNodeStatement, DoOperation operation, Graph resultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	/*
	 * Operations on relation statements
	 */

	public void executeGetOnRelationStatement(XDIStatement relationStatement, GetOperation operation, Graph resultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeSetOnRelationStatement(XDIStatement relationStatement, SetOperation operation, Graph resultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeDelOnRelationStatement(XDIStatement relationStatement, DelOperation operation, Graph resultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeDoOnRelationStatement(XDIStatement relationStatement, DoOperation operation, Graph resultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	/*
	 * Operations on literal statements
	 */

	public void executeGetOnLiteralStatement(XDIStatement literalStatement, GetOperation operation, Graph resultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeSetOnLiteralStatement(XDIStatement literalStatement, SetOperation operation, Graph resultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeDelOnLiteralStatement(XDIStatement literalStatement, DelOperation operation, Graph resultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeDoOnLiteralStatement(XDIStatement literalStatement, DoOperation operation, Graph resultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

	}
}
