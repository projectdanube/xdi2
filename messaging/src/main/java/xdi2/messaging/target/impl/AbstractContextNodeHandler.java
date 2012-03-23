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
 * - remembers the operation and context node to which the ContextNodeHandler applies
 * - checks what kind of operation is executed ($add, $get, ...) and calls the
 * appropriate executeXXX() method
 * 
 * @author markus
 */
public abstract class AbstractContextNodeHandler implements ContextNodeHandler {

	/*
	 * Operations on context nodes
	 */

	@Override
	public boolean executeOnContextNode(ContextNode operationContextNode, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (operation instanceof GetOperation)
			return this.executeGetOnContextNode(operationContextNode, operation, messageResult, executionContext);
		else if (operation instanceof AddOperation)
			return this.executeAddOnContextNode(operationContextNode, operation, messageResult, executionContext);
		else if (operation instanceof ModOperation)
			return this.executeModOnContextNode(operationContextNode, operation, messageResult, executionContext);
		else if (operation instanceof DelOperation)
			return this.executeDelOnContextNode(operationContextNode, operation, messageResult, executionContext);
		else
			throw new Xdi2MessagingException("Unknown operation: " + operation.getOperationXri());
	}

	public boolean executeGetOnContextNode(ContextNode operationContextNode, Operation operation, MessageResult operationResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean executeAddOnContextNode(ContextNode operationContextNode, Operation operation, MessageResult operationResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean executeModOnContextNode(ContextNode operationContextNode, Operation operation, MessageResult operationResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean executeDelOnContextNode(ContextNode operationContextNode, Operation operation, MessageResult operationResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	/*
	 * Operations on relations
	 */

	@Override
	public boolean executeOnRelation(ContextNode operationContextNode, Relation operationRelation, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (operation instanceof GetOperation)
			return this.executeGetOnRelation(operationContextNode, operationRelation, operation, messageResult, executionContext);
		else if (operation instanceof AddOperation)
			return this.executeAddOnRelation(operationContextNode, operationRelation, operation, messageResult, executionContext);
		else if (operation instanceof ModOperation)
			return this.executeModOnRelation(operationContextNode, operationRelation, operation, messageResult, executionContext);
		else if (operation instanceof DelOperation)
			return this.executeDelOnRelation(operationContextNode, operationRelation, operation, messageResult, executionContext);
		else
			throw new Xdi2MessagingException("Unknown operation: " + operation.getOperationXri());
	}

	public boolean executeGetOnRelation(ContextNode operationContextNode, Relation operationRelation, Operation operation, MessageResult operationResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean executeAddOnRelation(ContextNode operationContextNode, Relation operationRelation, Operation operation, MessageResult operationResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean executeModOnRelation(ContextNode operationContextNode, Relation operationRelation, Operation operation, MessageResult operationResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean executeDelOnRelation(ContextNode operationContextNode, Relation operationRelation, Operation operation, MessageResult operationResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	/*
	 * Operations on literals
	 */

	@Override
	public boolean executeOnLiteral(ContextNode operationContextNode, Literal operationLiteral, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (operation instanceof GetOperation)
			return this.executeGetOnLiteral(operationContextNode, operationLiteral, operation, messageResult, executionContext);
		else if (operation instanceof AddOperation)
			return this.executeAddOnLiteral(operationContextNode, operationLiteral, operation, messageResult, executionContext);
		else if (operation instanceof ModOperation)
			return this.executeModOnLiteral(operationContextNode, operationLiteral, operation, messageResult, executionContext);
		else if (operation instanceof DelOperation)
			return this.executeDelOnLiteral(operationContextNode, operationLiteral, operation, messageResult, executionContext);
		else
			throw new Xdi2MessagingException("Unknown operation: " + operation.getOperationXri());
	}

	public boolean executeGetOnLiteral(ContextNode operationContextNode, Literal operationLiteral, Operation operation, MessageResult operationResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean executeAddOnLiteral(ContextNode operationContextNode, Literal operationLiteral, Operation operation, MessageResult operationResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean executeModOnLiteral(ContextNode operationContextNode, Literal operationLiteral, Operation operation, MessageResult operationResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean executeDelOnLiteral(ContextNode operationContextNode, Literal operationLiteral, Operation operation, MessageResult operationResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}
}
