package xdi2.messaging.target.impl;

import xdi2.core.ContextNode;
import xdi2.core.Literal;
import xdi2.core.Relation;
import xdi2.core.exceptions.Xdi2MessagingException;
import xdi2.messaging.AddOperation;
import xdi2.messaging.DelOperation;
import xdi2.messaging.GetOperation;
import xdi2.messaging.MessageResult;
import xdi2.messaging.ModOperation;
import xdi2.messaging.Operation;
import xdi2.messaging.target.ExecutionContext;

/**
 * Relieves subclasses from the following tasks:
 * 
 * - remembers the operation and context node to which the ResourceHandler applies
 * - checks what kind of operation is executed ($add, $get, ...) and calls the
 * appropriate executeXXX() method
 * 
 * @author markus
 */
public abstract class AbstractContextNodeHandler implements ContextNodeHandler {

	protected Operation operation;
	protected ContextNode operationContextNode;

	public AbstractContextNodeHandler(Operation operation, ContextNode operationContextNode) {

		this.operation = operation;
		this.operationContextNode = operationContextNode;
	}

	/*
	 * Operations on context nodes
	 */

	public boolean executeContextNode(Operation operation, MessageResult operationResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (operation instanceof GetOperation)
			return this.executeGetContextNode(operation, operationResult, executionContext);
		else if (operation instanceof AddOperation)
			return this.executeAddContextNode(operation, operationResult, executionContext);
		else if (operation instanceof ModOperation)
			return this.executeModContextNode(operation, operationResult, executionContext);
		else if (operation instanceof DelOperation)
			return this.executeDelContextNode(operation, operationResult, executionContext);
		else
			throw new Xdi2MessagingException("Unknown operation: " + operation.getOperationXri());
	}

	public boolean executeGetContextNode(Operation operation, MessageResult operationResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean executeAddContextNode(Operation operation, MessageResult operationResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean executeModContextNode(Operation operation, MessageResult operationResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean executeDelContextNode(Operation operation, MessageResult operationResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	/*
	 * Operations on relations
	 */

	public boolean executeRelation(Relation operationRelation, Operation operation, MessageResult operationResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (operation instanceof GetOperation)
			return this.executeGetRelation(operationRelation, operation, operationResult, executionContext);
		else if (operation instanceof AddOperation)
			return this.executeAddRelation(operationRelation, operation, operationResult, executionContext);
		else if (operation instanceof ModOperation)
			return this.executeModRelation(operationRelation, operation, operationResult, executionContext);
		else if (operation instanceof DelOperation)
			return this.executeDelRelation(operationRelation, operation, operationResult, executionContext);
		else
			throw new Xdi2MessagingException("Unknown operation: " + operation.getOperationXri());
	}

	public boolean executeGetRelation(Relation operationRelation, Operation operation, MessageResult operationResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean executeAddRelation(Relation operationRelation, Operation operation, MessageResult operationResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean executeModRelation(Relation operationRelation, Operation operation, MessageResult operationResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean executeDelRelation(Relation operationRelation, Operation operation, MessageResult operationResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	/*
	 * Operations on literals
	 */

	public boolean executeLiteral(Literal operationLiteral, Operation operation, MessageResult operationResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (operation instanceof GetOperation)
			return this.executeGetLiteral(operationLiteral, operation, operationResult, executionContext);
		else if (operation instanceof AddOperation)
			return this.executeAddLiteral(operationLiteral, operation, operationResult, executionContext);
		else if (operation instanceof ModOperation)
			return this.executeModLiteral(operationLiteral, operation, operationResult, executionContext);
		else if (operation instanceof DelOperation)
			return this.executeDelLiteral(operationLiteral, operation, operationResult, executionContext);
		else
			throw new Xdi2MessagingException("Unknown operation: " + operation.getOperationXri());
	}

	public boolean executeGetLiteral(Literal operationLiteral, Operation operation, MessageResult operationResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean executeAddLiteral(Literal operationLiteral, Operation operation, MessageResult operationResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean executeModLiteral(Literal operationLiteral, Operation operation, MessageResult operationResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean executeDelLiteral(Literal operationLiteral, Operation operation, MessageResult operationResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public Operation getOperation() {

		return this.operation;
	}

	public ContextNode getOperationContextNode() {

		return this.operationContextNode;
	}
}
