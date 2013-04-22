package xdi2.messaging.target.contributor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.Literal;
import xdi2.core.Relation;
import xdi2.core.util.CopyUtil;
import xdi2.core.util.VariableUtil;
import xdi2.core.util.XDI3Util;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;
import xdi2.messaging.AddOperation;
import xdi2.messaging.DelOperation;
import xdi2.messaging.DoOperation;
import xdi2.messaging.GetOperation;
import xdi2.messaging.MessageResult;
import xdi2.messaging.ModOperation;
import xdi2.messaging.Operation;
import xdi2.messaging.SetOperation;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.ExecutionContext;

public abstract class AbstractContributor implements Contributor {

	private static final Logger log = LoggerFactory.getLogger(AbstractContributor.class);

	private ContributorMap contributors;

	public AbstractContributor() {

		this.contributors = new ContributorMap();
	}

	/*
	 * Operations on addresses
	 */

	@Override
	public boolean executeOnAddress(XDI3Segment[] contributorXris, XDI3Segment relativeTargetAddress, XDI3Segment targetAddress, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// execute contributors (address)

		if (this.getContributors().executeContributorsAddress(contributorXris, relativeTargetAddress, targetAddress, operation, messageResult, executionContext)) {

			return true;
		}

		// execute on address

		if (operation instanceof GetOperation)
			return this.executeGetOnAddress(contributorXris, relativeTargetAddress, targetAddress, (GetOperation) operation, messageResult, executionContext);
		else if (operation instanceof AddOperation)
			return this.executeAddOnAddress(contributorXris, relativeTargetAddress, targetAddress, (AddOperation) operation, messageResult, executionContext);
		else if (operation instanceof ModOperation)
			return this.executeModOnAddress(contributorXris, relativeTargetAddress, targetAddress, (ModOperation) operation, messageResult, executionContext);
		else if (operation instanceof SetOperation)
			return this.executeSetOnAddress(contributorXris, relativeTargetAddress, targetAddress, (SetOperation) operation, messageResult, executionContext);
		else if (operation instanceof DelOperation)
			return this.executeDelOnAddress(contributorXris, relativeTargetAddress, targetAddress, (DelOperation) operation, messageResult, executionContext);
		else if (operation instanceof DoOperation)
			return this.executeDoOnAddress(contributorXris, relativeTargetAddress, targetAddress, (DoOperation) operation, messageResult, executionContext);
		else
			throw new Xdi2MessagingException("Unknown operation: " + operation.getOperationXri(), null, executionContext);
	}

	public boolean executeGetOnAddress(XDI3Segment[] contributorXris, XDI3Segment relativeTargetAddress, XDI3Segment targetAddress, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment relativeContextNodeXri = relativeTargetAddress;
		XDI3Segment contextNodeXri = targetAddress;

		return this.getContext(contributorXris, relativeContextNodeXri, contextNodeXri, operation, messageResult, executionContext);
	}

	public boolean executeAddOnAddress(XDI3Segment[] contributorXris, XDI3Segment relativeTargetAddress, XDI3Segment targetAddress, AddOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment relativeContextNodeXri = relativeTargetAddress;
		XDI3Segment contextNodeXri = targetAddress;

		return this.addContext(contributorXris, relativeContextNodeXri, contextNodeXri, operation, messageResult, executionContext);
	}

	public boolean executeModOnAddress(XDI3Segment[] contributorXris, XDI3Segment relativeTargetAddress, XDI3Segment targetAddress, ModOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment relativeContextNodeXri = relativeTargetAddress;
		XDI3Segment contextNodeXri = targetAddress;

		return this.modContext(contributorXris, relativeContextNodeXri, contextNodeXri, operation, messageResult, executionContext);
	}

	public boolean executeSetOnAddress(XDI3Segment[] contributorXris, XDI3Segment relativeTargetAddress, XDI3Segment targetAddress, SetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment relativeContextNodeXri = relativeTargetAddress;
		XDI3Segment contextNodeXri = targetAddress;

		return this.setContext(contributorXris, relativeContextNodeXri, contextNodeXri, operation, messageResult, executionContext);
	}

	public boolean executeDelOnAddress(XDI3Segment[] contributorXris, XDI3Segment relativeTargetAddress, XDI3Segment targetAddress, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment relativeContextNodeXri = relativeTargetAddress;
		XDI3Segment contextNodeXri = targetAddress;

		return this.delContext(contributorXris, relativeContextNodeXri, contextNodeXri, operation, messageResult, executionContext);
	}

	public boolean executeDoOnAddress(XDI3Segment[] contributorXris, XDI3Segment relativeTargetAddress, XDI3Segment targetAddress, DoOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment relativeContextNodeXri = relativeTargetAddress;
		XDI3Segment contextNodeXri = targetAddress;

		return this.doContext(contributorXris, relativeContextNodeXri, contextNodeXri, operation, messageResult, executionContext);
	}

	/*
	 * Operations on statements
	 */

	@Override
	public boolean executeOnStatement(XDI3Segment[] contributorXris, XDI3Statement relativeTargetStatement, XDI3Statement targetStatement, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// execute contributors (statement)

		if (this.getContributors().executeContributorsStatement(contributorXris, relativeTargetStatement, targetStatement, operation, messageResult, executionContext)) {

			return true;
		}

		// execute on statement

		if (operation instanceof GetOperation)
			return this.executeGetOnStatement(contributorXris, relativeTargetStatement, targetStatement, (GetOperation) operation, messageResult, executionContext);
		else if (operation instanceof AddOperation)
			return this.executeAddOnStatement(contributorXris, relativeTargetStatement, targetStatement, (AddOperation) operation, messageResult, executionContext);
		else if (operation instanceof ModOperation)
			return this.executeModOnStatement(contributorXris, relativeTargetStatement, targetStatement, (ModOperation) operation, messageResult, executionContext);
		else if (operation instanceof SetOperation)
			return this.executeSetOnStatement(contributorXris, relativeTargetStatement, targetStatement, (SetOperation) operation, messageResult, executionContext);
		else if (operation instanceof DelOperation)
			return this.executeDelOnStatement(contributorXris, relativeTargetStatement, targetStatement, (DelOperation) operation, messageResult, executionContext);
		else if (operation instanceof DoOperation)
			return this.executeDoOnStatement(contributorXris, relativeTargetStatement, targetStatement, (DoOperation) operation, messageResult, executionContext);
		else
			throw new Xdi2MessagingException("Unknown operation: " + operation.getOperationXri(), null, executionContext);
	}

	public boolean executeGetOnStatement(XDI3Segment[] contributorXris, XDI3Statement relativeTargetStatement, XDI3Statement targetStatement, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (targetStatement.isContextNodeStatement())
			return this.executeGetOnContextNodeStatement(contributorXris, relativeTargetStatement, targetStatement, operation, messageResult, executionContext);
		else if (targetStatement.isRelationStatement())
			return this.executeGetOnRelationStatement(contributorXris, relativeTargetStatement, targetStatement, operation, messageResult, executionContext);
		else if (targetStatement.isLiteralStatement())
			return this.executeGetOnLiteralStatement(contributorXris, relativeTargetStatement, targetStatement, operation, messageResult, executionContext);
		else
			throw new Xdi2MessagingException("Invalid statement: " + targetStatement, null, executionContext);
	}

	public boolean executeAddOnStatement(XDI3Segment[] contributorXris, XDI3Statement relativeTargetStatement, XDI3Statement targetStatement, AddOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (targetStatement.isContextNodeStatement())
			return this.executeAddOnContextNodeStatement(contributorXris, relativeTargetStatement, targetStatement, operation, messageResult, executionContext);
		else if (targetStatement.isRelationStatement())
			return this.executeAddOnRelationStatement(contributorXris, relativeTargetStatement, targetStatement, operation, messageResult, executionContext);
		else if (targetStatement.isLiteralStatement())
			return this.executeAddOnLiteralStatement(contributorXris, relativeTargetStatement, targetStatement, operation, messageResult, executionContext);
		else
			throw new Xdi2MessagingException("Invalid statement: " + targetStatement, null, executionContext);
	}

	public boolean executeModOnStatement(XDI3Segment[] contributorXris, XDI3Statement relativeTargetStatement, XDI3Statement targetStatement, ModOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (targetStatement.isContextNodeStatement())
			return this.executeModOnContextNodeStatement(contributorXris, relativeTargetStatement, targetStatement, operation, messageResult, executionContext);
		else if (targetStatement.isRelationStatement())
			return this.executeModOnRelationStatement(contributorXris, relativeTargetStatement, targetStatement, operation, messageResult, executionContext);
		else if (targetStatement.isLiteralStatement())
			return this.executeModOnLiteralStatement(contributorXris, relativeTargetStatement, targetStatement, operation, messageResult, executionContext);
		else
			throw new Xdi2MessagingException("Invalid statement: " + targetStatement, null, executionContext);
	}

	public boolean executeSetOnStatement(XDI3Segment[] contributorXris, XDI3Statement relativeTargetStatement, XDI3Statement targetStatement, SetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (targetStatement.isContextNodeStatement())
			return this.executeSetOnContextNodeStatement(contributorXris, relativeTargetStatement, targetStatement, operation, messageResult, executionContext);
		else if (targetStatement.isRelationStatement())
			return this.executeSetOnRelationStatement(contributorXris, relativeTargetStatement, targetStatement, operation, messageResult, executionContext);
		else if (targetStatement.isLiteralStatement())
			return this.executeSetOnLiteralStatement(contributorXris, relativeTargetStatement, targetStatement, operation, messageResult, executionContext);
		else
			throw new Xdi2MessagingException("Invalid statement: " + targetStatement, null, executionContext);
	}

	public boolean executeDelOnStatement(XDI3Segment[] contributorXris, XDI3Statement relativeTargetStatement, XDI3Statement targetStatement, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (targetStatement.isContextNodeStatement())
			return this.executeDelOnContextNodeStatement(contributorXris, relativeTargetStatement, targetStatement, operation, messageResult, executionContext);
		else if (targetStatement.isRelationStatement())
			return this.executeDelOnRelationStatement(contributorXris, relativeTargetStatement, targetStatement, operation, messageResult, executionContext);
		else if (targetStatement.isLiteralStatement())
			return this.executeDelOnLiteralStatement(contributorXris, relativeTargetStatement, targetStatement, operation, messageResult, executionContext);
		else
			throw new Xdi2MessagingException("Invalid statement: " + targetStatement, null, executionContext);
	}

	public boolean executeDoOnStatement(XDI3Segment[] contributorXris, XDI3Statement relativeTargetStatement, XDI3Statement targetStatement, DoOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (targetStatement.isContextNodeStatement())
			return this.executeDoOnContextNodeStatement(contributorXris, relativeTargetStatement, targetStatement, operation, messageResult, executionContext);
		else if (targetStatement.isRelationStatement())
			return this.executeDoOnRelationStatement(contributorXris, relativeTargetStatement, targetStatement, operation, messageResult, executionContext);
		else if (targetStatement.isLiteralStatement())
			return this.executeDoOnLiteralStatement(contributorXris, relativeTargetStatement, targetStatement, operation, messageResult, executionContext);
		else
			throw new Xdi2MessagingException("Invalid statement: " + targetStatement, null, executionContext);
	}

	/*
	 * Operations on context node statements
	 */

	public boolean executeGetOnContextNodeStatement(XDI3Segment[] contributorXris, XDI3Statement relativeContextNodeStatement, XDI3Statement contextNodeStatement, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment relativeContextNodeXri = relativeContextNodeStatement == null ? null : relativeContextNodeStatement.getContextNodeXri();
		XDI3Segment contextNodeXri = contextNodeStatement.getContextNodeXri();

		return this.getContext(contributorXris, relativeContextNodeXri, contextNodeXri, operation, messageResult, executionContext);
	}

	public boolean executeAddOnContextNodeStatement(XDI3Segment[] contributorXris, XDI3Statement relativeContextNodeStatement, XDI3Statement contextNodeStatement, AddOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment relativeContextNodeXri = relativeContextNodeStatement == null ? null : relativeContextNodeStatement.getContextNodeXri();
		XDI3Segment contextNodeXri = contextNodeStatement.getContextNodeXri();

		return this.addContext(contributorXris, relativeContextNodeXri, contextNodeXri, operation, messageResult, executionContext);
	}

	public boolean executeModOnContextNodeStatement(XDI3Segment[] contributorXris, XDI3Statement relativeContextNodeStatement, XDI3Statement contextNodeStatement, ModOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment relativeContextNodeXri = relativeContextNodeStatement == null ? null : relativeContextNodeStatement.getContextNodeXri();
		XDI3Segment contextNodeXri = contextNodeStatement.getContextNodeXri();

		return this.modContext(contributorXris, relativeContextNodeXri, contextNodeXri, operation, messageResult, executionContext);
	}

	public boolean executeSetOnContextNodeStatement(XDI3Segment[] contributorXris, XDI3Statement relativeContextNodeStatement, XDI3Statement contextNodeStatement, SetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment relativeContextNodeXri = relativeContextNodeStatement == null ? null : relativeContextNodeStatement.getContextNodeXri();
		XDI3Segment contextNodeXri = contextNodeStatement.getContextNodeXri();

		return this.setContext(contributorXris, relativeContextNodeXri, contextNodeXri, operation, messageResult, executionContext);
	}

	public boolean executeDelOnContextNodeStatement(XDI3Segment[] contributorXris, XDI3Statement relativeContextNodeStatement, XDI3Statement contextNodeStatement, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment relativeContextNodeXri = relativeContextNodeStatement == null ? null : relativeContextNodeStatement.getContextNodeXri();
		XDI3Segment contextNodeXri = contextNodeStatement.getContextNodeXri();

		return this.delContext(contributorXris, relativeContextNodeXri, contextNodeXri, operation, messageResult, executionContext);
	}

	public boolean executeDoOnContextNodeStatement(XDI3Segment[] contributorXris, XDI3Statement relativeContextNodeStatement, XDI3Statement contextNodeStatement, DoOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment relativeContextNodeXri = relativeContextNodeStatement == null ? null : relativeContextNodeStatement.getContextNodeXri();
		XDI3Segment contextNodeXri = contextNodeStatement.getContextNodeXri();

		return this.doContext(contributorXris, relativeContextNodeXri, contextNodeXri, operation, messageResult, executionContext);
	}

	/*
	 * Operations on relation statements
	 */

	public boolean executeGetOnRelationStatement(XDI3Segment[] contributorXris, XDI3Statement relativeRelationStatement, XDI3Statement relationStatement, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment relativeContextNodeXri = relativeRelationStatement == null ? null : relativeRelationStatement.getContextNodeXri();
		XDI3Segment contextNodeXri = relationStatement.getContextNodeXri();
		XDI3Segment arcXri = relationStatement.getArcXri();
		XDI3Segment targetContextNodeXri = relationStatement.getTargetContextNodeXri();

		return this.getRelation(contributorXris, relativeContextNodeXri, contextNodeXri, arcXri, targetContextNodeXri, operation, messageResult, executionContext);
	}

	public boolean executeAddOnRelationStatement(XDI3Segment[] contributorXris, XDI3Statement relativeRelationStatement, XDI3Statement relationStatement, AddOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment relativeContextNodeXri = relativeRelationStatement == null ? null : relativeRelationStatement.getContextNodeXri();
		XDI3Segment contextNodeXri = relationStatement.getContextNodeXri();
		XDI3Segment arcXri = relationStatement.getArcXri();
		XDI3Segment targetContextNodeXri = relationStatement.getTargetContextNodeXri();

		return this.addRelation(contributorXris, relativeContextNodeXri, contextNodeXri, arcXri, targetContextNodeXri, operation, messageResult, executionContext);
	}

	public boolean executeModOnRelationStatement(XDI3Segment[] contributorXris, XDI3Statement relativeRelationStatement, XDI3Statement relationStatement, ModOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment relativeContextNodeXri = relativeRelationStatement == null ? null : relativeRelationStatement.getContextNodeXri();
		XDI3Segment contextNodeXri = relationStatement.getContextNodeXri();
		XDI3Segment arcXri = relationStatement.getArcXri();
		XDI3Segment targetContextNodeXri = relationStatement.getTargetContextNodeXri();

		return this.modRelation(contributorXris, relativeContextNodeXri, contextNodeXri, arcXri, targetContextNodeXri, operation, messageResult, executionContext);
	}

	public boolean executeSetOnRelationStatement(XDI3Segment[] contributorXris, XDI3Statement relativeRelationStatement, XDI3Statement relationStatement, SetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment relativeContextNodeXri = relativeRelationStatement == null ? null : relativeRelationStatement.getContextNodeXri();
		XDI3Segment contextNodeXri = relationStatement.getContextNodeXri();
		XDI3Segment arcXri = relationStatement.getArcXri();
		XDI3Segment targetContextNodeXri = relationStatement.getTargetContextNodeXri();

		return this.setRelation(contributorXris, relativeContextNodeXri, contextNodeXri, arcXri, targetContextNodeXri, operation, messageResult, executionContext);
	}

	public boolean executeDelOnRelationStatement(XDI3Segment[] contributorXris, XDI3Statement relativeRelationStatement, XDI3Statement relationStatement, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment relativeContextNodeXri = relativeRelationStatement == null ? null : relativeRelationStatement.getContextNodeXri();
		XDI3Segment contextNodeXri = relationStatement.getContextNodeXri();
		XDI3Segment arcXri = relationStatement.getArcXri();
		XDI3Segment targetContextNodeXri = relationStatement.getTargetContextNodeXri();

		return this.delRelation(contributorXris, relativeContextNodeXri, contextNodeXri, arcXri, targetContextNodeXri, operation, messageResult, executionContext);
	}

	public boolean executeDoOnRelationStatement(XDI3Segment[] contributorXris, XDI3Statement relativeRelationStatement, XDI3Statement relationStatement, DoOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment relativeContextNodeXri = relativeRelationStatement == null ? null : relativeRelationStatement.getContextNodeXri();
		XDI3Segment contextNodeXri = relationStatement.getContextNodeXri();
		XDI3Segment arcXri = relationStatement.getArcXri();
		XDI3Segment targetContextNodeXri = relationStatement.getTargetContextNodeXri();

		return this.doRelation(contributorXris, relativeContextNodeXri, contextNodeXri, arcXri, targetContextNodeXri, operation, messageResult, executionContext);
	}

	/*
	 * Operations on literal statements
	 */

	public boolean executeGetOnLiteralStatement(XDI3Segment[] contributorXris, XDI3Statement relativeLiteralStatement, XDI3Statement literalStatement, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment relativeContextNodeXri = relativeLiteralStatement == null ? null : relativeLiteralStatement.getContextNodeXri();
		XDI3Segment contextNodeXri = literalStatement.getContextNodeXri();
		String literalData = literalStatement.getLiteralData();

		return this.getLiteral(contributorXris, relativeContextNodeXri, contextNodeXri, literalData, operation, messageResult, executionContext);
	}

	public boolean executeAddOnLiteralStatement(XDI3Segment[] contributorXris, XDI3Statement relativeLiteralStatement, XDI3Statement literalStatement, AddOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment relativeContextNodeXri = relativeLiteralStatement == null ? null : relativeLiteralStatement.getContextNodeXri();
		XDI3Segment contextNodeXri = literalStatement.getContextNodeXri();
		String literalData = literalStatement.getLiteralData();

		return this.addLiteral(contributorXris, relativeContextNodeXri, contextNodeXri, literalData, operation, messageResult, executionContext);
	}

	public boolean executeModOnLiteralStatement(XDI3Segment[] contributorXris, XDI3Statement relativeLiteralStatement, XDI3Statement literalStatement, ModOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment relativeContextNodeXri = relativeLiteralStatement == null ? null : relativeLiteralStatement.getContextNodeXri();
		XDI3Segment contextNodeXri = literalStatement.getContextNodeXri();
		String literalData = literalStatement.getLiteralData();

		return this.modLiteral(contributorXris, relativeContextNodeXri, contextNodeXri, literalData, operation, messageResult, executionContext);
	}

	public boolean executeSetOnLiteralStatement(XDI3Segment[] contributorXris, XDI3Statement relativeLiteralStatement, XDI3Statement literalStatement, SetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment relativeContextNodeXri = relativeLiteralStatement == null ? null : relativeLiteralStatement.getContextNodeXri();
		XDI3Segment contextNodeXri = literalStatement.getContextNodeXri();
		String literalData = literalStatement.getLiteralData();

		return this.setLiteral(contributorXris, relativeContextNodeXri, contextNodeXri, literalData, operation, messageResult, executionContext);
	}

	public boolean executeDelOnLiteralStatement(XDI3Segment[] contributorXris, XDI3Statement relativeLiteralStatement, XDI3Statement literalStatement, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment relativeContextNodeXri = relativeLiteralStatement == null ? null : relativeLiteralStatement.getContextNodeXri();
		XDI3Segment contextNodeXri = literalStatement.getContextNodeXri();
		String literalData = literalStatement.getLiteralData();

		return this.delLiteral(contributorXris, relativeContextNodeXri, contextNodeXri, literalData, operation, messageResult, executionContext);
	}

	public boolean executeDoOnLiteralStatement(XDI3Segment[] contributorXris, XDI3Statement relativeLiteralStatement, XDI3Statement literalStatement, DoOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment relativeContextNodeXri = relativeLiteralStatement == null ? null : relativeLiteralStatement.getContextNodeXri();
		XDI3Segment contextNodeXri = literalStatement.getContextNodeXri();
		String literalData = literalStatement.getLiteralData();

		return this.doLiteral(contributorXris, relativeContextNodeXri, contextNodeXri, literalData, operation, messageResult, executionContext);
	}

	/*
	 * Methods to be overridden by subclasses
	 */

	public boolean getContext(XDI3Segment[] contributorXris, XDI3Segment relativeContextNodeXri, XDI3Segment contextNodeXri, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean addContext(XDI3Segment[] contributorXris, XDI3Segment relativeContextNodeXri, XDI3Segment contextNodeXri, AddOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean modContext(XDI3Segment[] contributorXris, XDI3Segment relativeContextNodeXri, XDI3Segment contextNodeXri, ModOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean setContext(XDI3Segment[] contributorXris, XDI3Segment relativeContextNodeXri, XDI3Segment contextNodeXri, SetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean delContext(XDI3Segment[] contributorXris, XDI3Segment relativeContextNodeXri, XDI3Segment contextNodeXri, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean doContext(XDI3Segment[] contributorXris, XDI3Segment relativeContextNodeXri, XDI3Segment contextNodeXri, DoOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean getRelation(XDI3Segment[] contributorXris, XDI3Segment relativeContextNodeXri, XDI3Segment arcXri, XDI3Segment targetContextNodeXri, XDI3Segment contextNodeXri, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		MessageResult tempMessageResult = new MessageResult();

		this.getContext(contributorXris, relativeContextNodeXri, contextNodeXri, operation, tempMessageResult, executionContext);

		ContextNode tempContextNode = tempMessageResult.getGraph().findContextNode(contextNodeXri, false);
		if (tempContextNode == null) return false;

		if (VariableUtil.isVariable(targetContextNodeXri)) {

			Iterator<Relation> relations;

			if (VariableUtil.isVariable(arcXri)) {

				relations = tempContextNode.getRelations();
			} else {

				relations = tempContextNode.getRelations(arcXri);
			}

			while (relations.hasNext()) CopyUtil.copyRelation(relations.next(), messageResult.getGraph(), null);
		} else {

			Relation relation = tempContextNode.getRelation(arcXri, targetContextNodeXri);
			if (relation == null) return false;

			CopyUtil.copyRelation(relation, messageResult.getGraph(), null);
		}

		return false;
	}

	public boolean addRelation(XDI3Segment[] contributorXris, XDI3Segment relativeContextNodeXri, XDI3Segment arcXri, XDI3Segment targetContextNodeXri, XDI3Segment contextNodeXri, AddOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean modRelation(XDI3Segment[] contributorXris, XDI3Segment relativeContextNodeXri, XDI3Segment arcXri, XDI3Segment targetContextNodeXri, XDI3Segment contextNodeXri, ModOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean setRelation(XDI3Segment[] contributorXris, XDI3Segment relativeContextNodeXri, XDI3Segment arcXri, XDI3Segment targetContextNodeXri, XDI3Segment contextNodeXri, SetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean delRelation(XDI3Segment[] contributorXris, XDI3Segment relativeContextNodeXri, XDI3Segment arcXri, XDI3Segment targetContextNodeXri, XDI3Segment contextNodeXri,  DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean doRelation(XDI3Segment[] contributorXris, XDI3Segment relativeContextNodeXri, XDI3Segment arcXri, XDI3Segment targetContextNodeXri, XDI3Segment contextNodeXri,  DoOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean getLiteral(XDI3Segment[] contributorXris, XDI3Segment relativeContextNodeXri, XDI3Segment contextNodeXri, String literalData, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		MessageResult tempMessageResult = new MessageResult();

		this.getContext(contributorXris, relativeContextNodeXri, contextNodeXri, operation, tempMessageResult, executionContext);

		ContextNode tempContextNode = tempMessageResult.getGraph().findContextNode(contextNodeXri, false);
		if (tempContextNode == null) return false;

		Literal tempLiteral = tempContextNode.getLiteral();
		if (tempLiteral == null) return false;

		if (literalData.isEmpty() || literalData.equals(tempLiteral.getLiteralData())) {

			CopyUtil.copyLiteral(tempLiteral, messageResult.getGraph(), null);
		}

		return false;
	}

	public boolean addLiteral(XDI3Segment[] contributorXris, XDI3Segment relativeContextNodeXri, XDI3Segment contextNodeXri, String literalData, AddOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean modLiteral(XDI3Segment[] contributorXris, XDI3Segment relativeContextNodeXri, XDI3Segment contextNodeXri, String literalData, ModOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean setLiteral(XDI3Segment[] contributorXris, XDI3Segment relativeContextNodeXri, XDI3Segment contextNodeXri, String literalData, SetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean delLiteral(XDI3Segment[] contributorXris, XDI3Segment relativeContextNodeXri, XDI3Segment contextNodeXri, String literalData, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean doLiteral(XDI3Segment[] contributorXris, XDI3Segment relativeContextNodeXri, XDI3Segment contextNodeXri, String literalData, DoOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	/*
	 * Filter methods
	 */

	public boolean getContextFilter(XDI3Segment[] contributorXris, XDI3Segment relativeContextNodeXri, XDI3Segment contextNodeXri, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment tempContextNodeXri = XDI3Util.parentXri(contextNodeXri, - relativeContextNodeXri.getNumSubSegments());

		log.debug("Filtering " + tempContextNodeXri + " with lower context node " + relativeContextNodeXri);
		
		MessageResult tempMessageResult = new MessageResult();
		this.getContext(contributorXris, null, tempContextNodeXri, operation, tempMessageResult, executionContext);
		ContextNode contextNode = tempMessageResult.getGraph().findContextNode(contextNodeXri, false);
		if (contextNode != null) CopyUtil.copyContextNode(contextNode, messageResult.getGraph(), null);

		return true;
	}
	
	/*
	 * Getters and setters
	 */

	public ContributorMap getContributors() {

		return this.contributors;
	}

	public void setContributors(ContributorMap contributors) {

		this.contributors = contributors;
	}

	public void setContributors(Map<XDI3Segment, List<Contributor>> contributors) {

		this.contributors.clear();
		this.contributors.putAll(contributors);
	}

	public void setContributorsList(ArrayList<Contributor> contributors) {

		this.contributors.clear();
		for (Contributor contributor : contributors) this.contributors.addContributor(contributor);
	}
}
