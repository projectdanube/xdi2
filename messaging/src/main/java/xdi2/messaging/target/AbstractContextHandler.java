package xdi2.messaging.target;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.Literal;
import xdi2.core.Relation;
import xdi2.core.features.variables.Variables;
import xdi2.core.util.CopyUtil;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;
import xdi2.messaging.AddOperation;
import xdi2.messaging.DelOperation;
import xdi2.messaging.DoOperation;
import xdi2.messaging.GetOperation;
import xdi2.messaging.MessageResult;
import xdi2.messaging.ModOperation;
import xdi2.messaging.Operation;
import xdi2.messaging.exceptions.Xdi2MessagingException;

public abstract class AbstractContextHandler implements StatementHandler, AddressHandler {

	public AbstractContextHandler() {

	}

	/*
	 * Operations on addresses
	 */

	@Override
	public void executeOnAddress(XDI3Segment targetAddress, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// execute on address

		if (operation instanceof GetOperation)
			this.executeGetOnAddress(targetAddress, (GetOperation) operation, messageResult, executionContext);
		else if (operation instanceof AddOperation)
			this.executeAddOnAddress(targetAddress, (AddOperation) operation, messageResult, executionContext);
		else if (operation instanceof ModOperation)
			this.executeModOnAddress(targetAddress, (ModOperation) operation, messageResult, executionContext);
		else if (operation instanceof DelOperation)
			this.executeDelOnAddress(targetAddress, (DelOperation) operation, messageResult, executionContext);
		else if (operation instanceof DoOperation)
			this.executeDoOnAddress(targetAddress, (DoOperation) operation, messageResult, executionContext);
		else
			throw new Xdi2MessagingException("Unknown operation: " + operation.getOperationXri(), null, executionContext);
	}

	public void executeGetOnAddress(XDI3Segment targetAddress, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment contextNodeXri = targetAddress;

		this.getContext(contextNodeXri, operation, messageResult, executionContext);
	}

	public void executeAddOnAddress(XDI3Segment targetAddress, AddOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment contextNodeXri = targetAddress;

		this.addContext(contextNodeXri, operation, messageResult, executionContext);
	}

	public void executeModOnAddress(XDI3Segment targetAddress, ModOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment contextNodeXri = targetAddress;

		this.modContext(contextNodeXri, operation, messageResult, executionContext);
	}

	public void executeDelOnAddress(XDI3Segment targetAddress, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment contextNodeXri = targetAddress;

		this.delContext(contextNodeXri, operation, messageResult, executionContext);
	}

	public void executeDoOnAddress(XDI3Segment targetAddress, DoOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment contextNodeXri = targetAddress;

		this.doContext(contextNodeXri, operation, messageResult, executionContext);
	}

	/*
	 * Operations on statements
	 */

	@Override
	public void executeOnStatement(XDI3Statement targetStatement, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// execute on statement

		if (operation instanceof GetOperation)
			this.executeGetOnStatement(targetStatement, (GetOperation) operation, messageResult, executionContext);
		else if (operation instanceof AddOperation)
			this.executeAddOnStatement(targetStatement, (AddOperation) operation, messageResult, executionContext);
		else if (operation instanceof ModOperation)
			this.executeModOnStatement(targetStatement, (ModOperation) operation, messageResult, executionContext);
		else if (operation instanceof DelOperation)
			this.executeDelOnStatement(targetStatement, (DelOperation) operation, messageResult, executionContext);
		else if (operation instanceof DoOperation)
			this.executeDoOnStatement(targetStatement, (DoOperation) operation, messageResult, executionContext);
		else
			throw new Xdi2MessagingException("Unknown operation: " + operation.getOperationXri(), null, executionContext);
	}

	public void executeGetOnStatement(XDI3Statement targetStatement, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (targetStatement.isContextNodeStatement())
			this.executeGetOnContextNodeStatement(targetStatement, operation, messageResult, executionContext);
		else if (targetStatement.isRelationStatement())
			this.executeGetOnRelationStatement(targetStatement, operation, messageResult, executionContext);
		else if (targetStatement.isLiteralStatement())
			this.executeGetOnLiteralStatement(targetStatement, operation, messageResult, executionContext);
		else
			throw new Xdi2MessagingException("Invalid statement: " + targetStatement, null, executionContext);
	}

	public void executeAddOnStatement(XDI3Statement targetStatement, AddOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (targetStatement.isContextNodeStatement())
			this.executeAddOnContextNodeStatement(targetStatement, operation, messageResult, executionContext);
		else if (targetStatement.isRelationStatement())
			this.executeAddOnRelationStatement(targetStatement, operation, messageResult, executionContext);
		else if (targetStatement.isLiteralStatement())
			this.executeAddOnLiteralStatement(targetStatement, operation, messageResult, executionContext);
		else
			throw new Xdi2MessagingException("Invalid statement: " + targetStatement, null, executionContext);
	}

	public void executeModOnStatement(XDI3Statement targetStatement, ModOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (targetStatement.isContextNodeStatement())
			this.executeModOnContextNodeStatement(targetStatement, operation, messageResult, executionContext);
		else if (targetStatement.isRelationStatement())
			this.executeModOnRelationStatement(targetStatement, operation, messageResult, executionContext);
		else if (targetStatement.isLiteralStatement())
			this.executeModOnLiteralStatement(targetStatement, operation, messageResult, executionContext);
		else
			throw new Xdi2MessagingException("Invalid statement: " + targetStatement, null, executionContext);
	}

	public void executeDelOnStatement(XDI3Statement targetStatement, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (targetStatement.isContextNodeStatement())
			this.executeDelOnContextNodeStatement(targetStatement, operation, messageResult, executionContext);
		else if (targetStatement.isRelationStatement())
			this.executeDelOnRelationStatement(targetStatement, operation, messageResult, executionContext);
		else if (targetStatement.isLiteralStatement())
			this.executeDelOnLiteralStatement(targetStatement, operation, messageResult, executionContext);
		else
			throw new Xdi2MessagingException("Invalid statement: " + targetStatement, null, executionContext);
	}

	public void executeDoOnStatement(XDI3Statement targetStatement, DoOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

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

	public void executeGetOnContextNodeStatement(XDI3Statement contextNodeStatement, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment contextNodeXri = contextNodeStatement.getContextNodeXri();

		this.getContext(contextNodeXri, operation, messageResult, executionContext);
	}

	public void executeAddOnContextNodeStatement(XDI3Statement contextNodeStatement, AddOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment contextNodeXri = contextNodeStatement.getContextNodeXri();

		this.addContext(contextNodeXri, operation, messageResult, executionContext);
	}

	public void executeModOnContextNodeStatement(XDI3Statement contextNodeStatement, ModOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment contextNodeXri = contextNodeStatement.getContextNodeXri();

		this.modContext(contextNodeXri, operation, messageResult, executionContext);
	}

	public void executeDelOnContextNodeStatement(XDI3Statement contextNodeStatement, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment contextNodeXri = contextNodeStatement.getContextNodeXri();

		this.delContext(contextNodeXri, operation, messageResult, executionContext);
	}

	public void executeDoOnContextNodeStatement(XDI3Statement contextNodeStatement, DoOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment contextNodeXri = contextNodeStatement.getContextNodeXri();

		this.doContext(contextNodeXri, operation, messageResult, executionContext);
	}

	/*
	 * Operations on relation statements
	 */

	public void executeGetOnRelationStatement(XDI3Statement relationStatement, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment contextNodeXri = relationStatement.getContextNodeXri();
		XDI3Segment arcXri = relationStatement.getPredicate();
		XDI3Segment targetContextNodeXri = relationStatement.getObject();

		this.getRelation(contextNodeXri, arcXri, targetContextNodeXri, operation, messageResult, executionContext);
	}

	public void executeAddOnRelationStatement(XDI3Statement relationStatement, AddOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment contextNodeXri = relationStatement.getContextNodeXri();
		XDI3Segment arcXri = relationStatement.getPredicate();
		XDI3Segment targetContextNodeXri = relationStatement.getObject();

		this.addRelation(contextNodeXri, arcXri, targetContextNodeXri, operation, messageResult, executionContext);
	}

	public void executeModOnRelationStatement(XDI3Statement relationStatement, ModOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment contextNodeXri = relationStatement.getContextNodeXri();
		XDI3Segment arcXri = relationStatement.getPredicate();
		XDI3Segment targetContextNodeXri = relationStatement.getObject();

		this.modRelation(contextNodeXri, arcXri, targetContextNodeXri, operation, messageResult, executionContext);
	}

	public void executeDelOnRelationStatement(XDI3Statement relationStatement, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment contextNodeXri = relationStatement.getContextNodeXri();
		XDI3Segment arcXri = relationStatement.getPredicate();
		XDI3Segment targetContextNodeXri = relationStatement.getObject();

		this.delRelation(contextNodeXri, arcXri, targetContextNodeXri, operation, messageResult, executionContext);
	}

	public void executeDoOnRelationStatement(XDI3Statement relationStatement, DoOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment contextNodeXri = relationStatement.getContextNodeXri();
		XDI3Segment arcXri = relationStatement.getPredicate();
		XDI3Segment targetContextNodeXri = relationStatement.getObject();

		this.doRelation(contextNodeXri, arcXri, targetContextNodeXri, operation, messageResult, executionContext);
	}

	/*
	 * Operations on literal statements
	 */

	public void executeGetOnLiteralStatement(XDI3Statement literalStatement, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment contextNodeXri = literalStatement.getContextNodeXri();
		String literalData = literalStatement.getLiteralData();

		this.getLiteral(contextNodeXri, literalData, operation, messageResult, executionContext);
	}

	public void executeAddOnLiteralStatement(XDI3Statement literalStatement, AddOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment contextNodeXri = literalStatement.getContextNodeXri();
		String literalData = literalStatement.getLiteralData();

		this.addLiteral(contextNodeXri, literalData, operation, messageResult, executionContext);
	}

	public void executeModOnLiteralStatement(XDI3Statement literalStatement, ModOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment contextNodeXri = literalStatement.getContextNodeXri();
		String literalData = literalStatement.getLiteralData();

		this.modLiteral(contextNodeXri, literalData, operation, messageResult, executionContext);
	}

	public void executeDelOnLiteralStatement(XDI3Statement literalStatement, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment contextNodeXri = literalStatement.getContextNodeXri();
		String literalData = literalStatement.getLiteralData();

		this.delLiteral(contextNodeXri, literalData, operation, messageResult, executionContext);
	}

	public void executeDoOnLiteralStatement(XDI3Statement literalStatement, DoOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment contextNodeXri = literalStatement.getContextNodeXri();
		String literalData = literalStatement.getLiteralData();

		this.doLiteral(contextNodeXri, literalData, operation, messageResult, executionContext);
	}

	/*
	 * Methods to be overridden by subclasses
	 */

	public void getContext(XDI3Segment contextNodeXri, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void addContext(XDI3Segment contextNodeXri, AddOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void modContext(XDI3Segment contextNodeXri, ModOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void delContext(XDI3Segment contextNodeXri, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void doContext(XDI3Segment contextNodeXri, DoOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void getRelation(XDI3Segment contextNodeXri, XDI3Segment arcXri, XDI3Segment targetContextNodeXri, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		MessageResult tempMessageResult = new MessageResult();

		this.getContext(contextNodeXri, operation, tempMessageResult, executionContext);

		ContextNode tempContextNode = tempMessageResult.getGraph().findContextNode(contextNodeXri, false);
		if (tempContextNode == null) return;

		if (Variables.isVariableSingle(targetContextNodeXri)) {

			Iterator<Relation> relations = tempContextNode.getRelations(arcXri);

			if (Variables.isVariableSingle(arcXri)) {

				relations = tempContextNode.getRelations();
			} else {

				relations = tempContextNode.getRelations(arcXri);
			}

			while (relations.hasNext()) CopyUtil.copyRelation(relations.next(), messageResult.getGraph(), null);
		} else {

			Relation relation = tempContextNode.getRelation(arcXri, targetContextNodeXri);
			if (relation == null) return;

			CopyUtil.copyRelation(relation, messageResult.getGraph(), null);
		}
	}

	public void addRelation(XDI3Segment contextNodeXri, XDI3Segment arcXri, XDI3Segment targetContextNodeXri, AddOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void modRelation(XDI3Segment contextNodeXri, XDI3Segment arcXri, XDI3Segment targetContextNodeXri, ModOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void delRelation(XDI3Segment contextNodeXri, XDI3Segment arcXri, XDI3Segment targetContextNodeXri, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void doRelation(XDI3Segment contextNodeXri, XDI3Segment arcXri, XDI3Segment targetContextNodeXri, DoOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void getLiteral(XDI3Segment contextNodeXri, String literalData, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		MessageResult tempMessageResult = new MessageResult();

		this.getContext(contextNodeXri, operation, tempMessageResult, executionContext);

		ContextNode tempContextNode = tempMessageResult.getGraph().findContextNode(contextNodeXri, false);
		if (tempContextNode == null) return;

		Literal tempLiteral = tempContextNode.getLiteral();
		if (tempLiteral == null) return;

		if (literalData.isEmpty() || literalData.equals(tempLiteral.getLiteralData())) {

			CopyUtil.copyLiteral(tempLiteral, messageResult.getGraph(), null);
		}
	}

	public void addLiteral(XDI3Segment contextNodeXri, String literalData, AddOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void modLiteral(XDI3Segment contextNodeXri, String literalData, ModOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void delLiteral(XDI3Segment contextNodeXri, String literalData, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void doLiteral(XDI3Segment contextNodeXri, String literalData, DoOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}
}
