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
 * - remembers the operation and statement details to which the ResourceHandler applies
 * - checks what kind of operation is executed ($add, $get, ...) and calls the
 * appropriate executeXXX() method
 * 
 * @author markus
 */
public abstract class AbstractResourceHandler implements ResourceHandler {

	protected Operation operation;
	protected ContextNode operationContextNode;
	protected Relation operationRelation;
	protected Literal operationLiteral;

	public AbstractResourceHandler(Operation operation, ContextNode operationContextNode) {

		this.operation = operation;
		this.operationContextNode = operationContextNode;
		this.operationRelation = null;
		this.operationLiteral = null;
	}

	public AbstractResourceHandler(Operation operation, Relation operationRelation) {

		this.operation = operation;
		this.operationContextNode = null;
		this.operationRelation = operationRelation;
		this.operationLiteral = null;
	}

	public AbstractResourceHandler(Operation operation, Literal operationLiteral) {

		this.operation = operation;
		this.operationContextNode = null;
		this.operationRelation = null;
		this.operationLiteral = operationLiteral;
	}

	public boolean execute(Operation operation, MessageResult operationResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (operation instanceof GetOperation)
			return this.executeGet(operation, operationResult, executionContext);
		else if (operation instanceof AddOperation)
			return this.executeAdd(operation, operationResult, executionContext);
		else if (operation instanceof ModOperation)
			return this.executeMod(operation, operationResult, executionContext);
		else if (operation instanceof DelOperation)
			return this.executeDel(operation, operationResult, executionContext);
		else
			throw new Xdi2MessagingException("Unknown operation: " + operation.getOperationXri());
	}

	public boolean executeGet(Operation operation, MessageResult operationResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean executeAdd(Operation operation, MessageResult operationResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean executeMod(Operation operation, MessageResult operationResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean executeDel(Operation operation, MessageResult operationResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public Operation getOperation() {

		return this.operation;
	}

	public ContextNode getOperationContextNode() {

		return this.operationContextNode;
	}

	public Relation getOperationRelation() {

		return this.operationRelation;
	}

	public Literal getOperationLiteral() {

		return this.operationLiteral;
	}
}
