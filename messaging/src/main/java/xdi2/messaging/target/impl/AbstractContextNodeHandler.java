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
	public boolean executeOnContextNode(ContextNode targetContextNode, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (operation instanceof GetOperation)
			return this.executeGetOnContextNode(targetContextNode, operation, messageResult, executionContext);
		else if (operation instanceof AddOperation)
			return this.executeAddOnContextNode(targetContextNode, operation, messageResult, executionContext);
		else if (operation instanceof ModOperation)
			return this.executeModOnContextNode(targetContextNode, operation, messageResult, executionContext);
		else if (operation instanceof DelOperation)
			return this.executeDelOnContextNode(targetContextNode, operation, messageResult, executionContext);
		else
			throw new Xdi2MessagingException("Unknown operation: " + operation.getOperationXri());
	}

	public boolean executeGetOnContextNode(ContextNode targetContextNode, Operation operation, MessageResult operationResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean executeAddOnContextNode(ContextNode targetContextNode, Operation operation, MessageResult operationResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean executeModOnContextNode(ContextNode targetContextNode, Operation operation, MessageResult operationResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean executeDelOnContextNode(ContextNode targetContextNode, Operation operation, MessageResult operationResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	/*
	 * Operations on relations
	 */

	@Override
	public boolean executeOnRelation(ContextNode targetContextNode, Relation targetRelation, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (operation instanceof GetOperation)
			return this.executeGetOnRelation(targetContextNode, targetRelation, operation, messageResult, executionContext);
		else if (operation instanceof AddOperation)
			return this.executeAddOnRelation(targetContextNode, targetRelation, operation, messageResult, executionContext);
		else if (operation instanceof ModOperation)
			return this.executeModOnRelation(targetContextNode, targetRelation, operation, messageResult, executionContext);
		else if (operation instanceof DelOperation)
			return this.executeDelOnRelation(targetContextNode, targetRelation, operation, messageResult, executionContext);
		else
			throw new Xdi2MessagingException("Unknown operation: " + operation.getOperationXri());
	}

	public boolean executeGetOnRelation(ContextNode targetContextNode, Relation targetRelation, Operation operation, MessageResult operationResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean executeAddOnRelation(ContextNode targetContextNode, Relation targetRelation, Operation operation, MessageResult operationResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean executeModOnRelation(ContextNode targetContextNode, Relation targetRelation, Operation operation, MessageResult operationResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean executeDelOnRelation(ContextNode targetContextNode, Relation targetRelation, Operation operation, MessageResult operationResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	/*
	 * Operations on literals
	 */

	@Override
	public boolean executeOnLiteral(ContextNode targetContextNode, Literal targetLiteral, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (operation instanceof GetOperation)
			return this.executeGetOnLiteral(targetContextNode, targetLiteral, operation, messageResult, executionContext);
		else if (operation instanceof AddOperation)
			return this.executeAddOnLiteral(targetContextNode, targetLiteral, operation, messageResult, executionContext);
		else if (operation instanceof ModOperation)
			return this.executeModOnLiteral(targetContextNode, targetLiteral, operation, messageResult, executionContext);
		else if (operation instanceof DelOperation)
			return this.executeDelOnLiteral(targetContextNode, targetLiteral, operation, messageResult, executionContext);
		else
			throw new Xdi2MessagingException("Unknown operation: " + operation.getOperationXri());
	}

	public boolean executeGetOnLiteral(ContextNode targetContextNode, Literal targetLiteral, Operation operation, MessageResult operationResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean executeAddOnLiteral(ContextNode targetContextNode, Literal targetLiteral, Operation operation, MessageResult operationResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean executeModOnLiteral(ContextNode targetContextNode, Literal targetLiteral, Operation operation, MessageResult operationResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean executeDelOnLiteral(ContextNode targetContextNode, Literal targetLiteral, Operation operation, MessageResult operationResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}
}
