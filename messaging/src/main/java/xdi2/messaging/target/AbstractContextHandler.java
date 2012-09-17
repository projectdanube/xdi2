package xdi2.messaging.target;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.Literal;
import xdi2.core.Relation;
import xdi2.core.Statement;
import xdi2.core.Statement.ContextNodeStatement;
import xdi2.core.Statement.LiteralStatement;
import xdi2.core.Statement.RelationStatement;
import xdi2.core.constants.XDIConstants;
import xdi2.core.features.variables.Variables;
import xdi2.core.util.CopyUtil;
import xdi2.core.util.XDIUtil;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.messaging.AddOperation;
import xdi2.messaging.DelOperation;
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
	public void executeOnAddress(XRI3Segment targetAddress, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// execute on address

		if (operation instanceof GetOperation)
			this.executeGetOnAddress(targetAddress, (GetOperation) operation, messageResult, executionContext);
		else if (operation instanceof AddOperation)
			this.executeAddOnAddress(targetAddress, (AddOperation) operation, messageResult, executionContext);
		else if (operation instanceof ModOperation)
			this.executeModOnAddress(targetAddress, (ModOperation) operation, messageResult, executionContext);
		else if (operation instanceof DelOperation)
			this.executeDelOnAddress(targetAddress, (DelOperation) operation, messageResult, executionContext);
		else
			throw new Xdi2MessagingException("Unknown operation: " + operation.getOperationXri(), null, executionContext);
	}

	public void executeGetOnAddress(XRI3Segment targetAddress, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XRI3Segment contextNodeXri = targetAddress;

		this.getContext(contextNodeXri, operation, messageResult, executionContext);
	}

	public void executeAddOnAddress(XRI3Segment targetAddress, AddOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XRI3Segment contextNodeXri = targetAddress;

		this.addContext(contextNodeXri, operation, messageResult, executionContext);
	}

	public void executeModOnAddress(XRI3Segment targetAddress, ModOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XRI3Segment contextNodeXri = targetAddress;

		this.modContext(contextNodeXri, operation, messageResult, executionContext);
	}

	public void executeDelOnAddress(XRI3Segment targetAddress, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XRI3Segment contextNodeXri = targetAddress;

		this.delContext(contextNodeXri, operation, messageResult, executionContext);
	}

	/*
	 * Operations on statements
	 */

	@Override
	public void executeOnStatement(Statement targetStatement, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// execute on statement

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

		XRI3Segment contextNodeXri = new XRI3Segment(contextNodeStatement.getSubject().toString() + contextNodeStatement.getObject().toString());

		this.getContext(contextNodeXri, operation, messageResult, executionContext);
	}

	public void executeAddOnContextNodeStatement(ContextNodeStatement contextNodeStatement, AddOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XRI3Segment contextNodeXri = new XRI3Segment(contextNodeStatement.getSubject().toString() + contextNodeStatement.getObject().toString());

		this.addContext(contextNodeXri, operation, messageResult, executionContext);
	}

	public void executeModOnContextNodeStatement(ContextNodeStatement contextNodeStatement, ModOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XRI3Segment contextNodeXri = new XRI3Segment(contextNodeStatement.getSubject().toString() + contextNodeStatement.getObject().toString());

		this.modContext(contextNodeXri, operation, messageResult, executionContext);
	}

	public void executeDelOnContextNodeStatement(ContextNodeStatement contextNodeStatement, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XRI3Segment contextNodeXri = new XRI3Segment(contextNodeStatement.getSubject().toString() + contextNodeStatement.getObject().toString());

		this.delContext(contextNodeXri, operation, messageResult, executionContext);
	}

	/*
	 * Operations on relation statements
	 */

	public void executeGetOnRelationStatement(RelationStatement relationStatement, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XRI3Segment contextNodeXri = relationStatement.getSubject();
		XRI3Segment arcXri = relationStatement.getPredicate();
		XRI3Segment targetContextNodeXri = relationStatement.getObject();

		this.getRelation(contextNodeXri, arcXri, targetContextNodeXri, operation, messageResult, executionContext);
	}

	public void executeAddOnRelationStatement(RelationStatement relationStatement, AddOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XRI3Segment contextNodeXri = relationStatement.getSubject();
		XRI3Segment arcXri = relationStatement.getPredicate();
		XRI3Segment targetContextNodeXri = relationStatement.getObject();

		this.addRelation(contextNodeXri, arcXri, targetContextNodeXri, operation, messageResult, executionContext);
	}

	public void executeModOnRelationStatement(RelationStatement relationStatement, ModOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XRI3Segment contextNodeXri = relationStatement.getSubject();
		XRI3Segment arcXri = relationStatement.getPredicate();
		XRI3Segment targetContextNodeXri = relationStatement.getObject();

		this.modRelation(contextNodeXri, arcXri, targetContextNodeXri, operation, messageResult, executionContext);
	}

	public void executeDelOnRelationStatement(RelationStatement relationStatement, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XRI3Segment contextNodeXri = relationStatement.getSubject();
		XRI3Segment arcXri = relationStatement.getPredicate();
		XRI3Segment targetContextNodeXri = relationStatement.getObject();

		this.delRelation(contextNodeXri, arcXri, targetContextNodeXri, operation, messageResult, executionContext);
	}

	/*
	 * Operations on literal statements
	 */

	public void executeGetOnLiteralStatement(LiteralStatement literalStatement, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XRI3Segment contextNodeXri = literalStatement.getSubject();
		String literalData = XDIUtil.dataXriSegmentToString(literalStatement.getObject());

		this.getLiteral(contextNodeXri, literalData, operation, messageResult, executionContext);
	}

	public void executeAddOnLiteralStatement(LiteralStatement literalStatement, AddOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XRI3Segment contextNodeXri = literalStatement.getSubject();
		String literalData = XDIUtil.dataXriSegmentToString(literalStatement.getObject());

		this.addLiteral(contextNodeXri, literalData, operation, messageResult, executionContext);
	}

	public void executeModOnLiteralStatement(LiteralStatement literalStatement, ModOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XRI3Segment contextNodeXri = literalStatement.getSubject();
		String literalData = XDIUtil.dataXriSegmentToString(literalStatement.getObject());

		this.modLiteral(contextNodeXri, literalData, operation, messageResult, executionContext);
	}

	public void executeDelOnLiteralStatement(LiteralStatement literalStatement, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XRI3Segment contextNodeXri = literalStatement.getSubject();
		String literalData = XDIUtil.dataXriSegmentToString(literalStatement.getObject());

		this.delLiteral(contextNodeXri, literalData, operation, messageResult, executionContext);
	}

	/*
	 * Methods to be overridden by subclasses
	 */

	public void getContext(XRI3Segment contextNodeXri, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void addContext(XRI3Segment contextNodeXri, AddOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void modContext(XRI3Segment contextNodeXri, ModOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void delContext(XRI3Segment contextNodeXri, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void getRelation(XRI3Segment contextNodeXri, XRI3Segment arcXri, XRI3Segment targetContextNodeXri, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		MessageResult tempMessageResult = new MessageResult();

		this.getContext(contextNodeXri, operation, tempMessageResult, executionContext);

		ContextNode tempContextNode = tempMessageResult.getGraph().findContextNode(contextNodeXri, false);
		if (tempContextNode == null) return;

		boolean isObjectVariable = Variables.isVariable(targetContextNodeXri);

		if (arcXri.equals(XDIConstants.XRI_S_LITERAL)) {

			if (isObjectVariable) {

				Literal literal = tempContextNode.getLiteral();
				if (literal == null) return;

				CopyUtil.copyLiteral(literal, messageResult.getGraph(), null);
			}
		} else {

			if (isObjectVariable) {
				
				Iterator<Relation> relations = tempContextNode.getRelations(arcXri);

				while (relations.hasNext()) CopyUtil.copyRelation(relations.next(), messageResult.getGraph(), null);
			} else {
				
				Relation relation = tempContextNode.getRelation(arcXri, targetContextNodeXri);
				if (relation == null) return;

				CopyUtil.copyRelation(relation, messageResult.getGraph(), null);
			}
		}
	}

	public void addRelation(XRI3Segment contextNodeXri, XRI3Segment arcXri, XRI3Segment targetContextNodeXri, AddOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void modRelation(XRI3Segment contextNodeXri, XRI3Segment arcXri, XRI3Segment targetContextNodeXri, ModOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void delRelation(XRI3Segment contextNodeXri, XRI3Segment arcXri, XRI3Segment targetContextNodeXri, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void getLiteral(XRI3Segment contextNodeXri, String literalData, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		MessageResult tempMessageResult = new MessageResult();

		this.getContext(contextNodeXri, operation, tempMessageResult, executionContext);

		ContextNode tempContextNode = tempMessageResult.getGraph().findContextNode(contextNodeXri, false);
		if (tempContextNode == null) return;

		Literal tempLiteral = tempContextNode.getLiteral();
		if (tempLiteral == null) return;

		if (! tempLiteral.getLiteralData().equals(literalData)) return;

		CopyUtil.copyLiteral(tempLiteral, messageResult.getGraph(), null);
	}

	public void addLiteral(XRI3Segment contextNodeXri, String literalData, AddOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void modLiteral(XRI3Segment contextNodeXri, String literalData, ModOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void delLiteral(XRI3Segment contextNodeXri, String literalData, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}
}
