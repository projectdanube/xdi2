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

	private boolean enabled;
	private ContributorMap contributors;

	public AbstractContributor() {

		this.enabled = true;
		this.contributors = new ContributorMap();
	}

	/*
	 * Operations on addresses
	 */

	@Override
	public boolean executeOnAddress(XDI3Segment[] contributorXris, XDI3Segment contributorsXri, XDI3Segment relativeTargetAddress, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// execute on address

		if (operation instanceof GetOperation)
			return this.executeGetOnAddress(contributorXris, contributorsXri, relativeTargetAddress, (GetOperation) operation, messageResult, executionContext);
		else if (operation instanceof AddOperation)
			return this.executeAddOnAddress(contributorXris, contributorsXri, relativeTargetAddress, (AddOperation) operation, messageResult, executionContext);
		else if (operation instanceof ModOperation)
			return this.executeModOnAddress(contributorXris, contributorsXri, relativeTargetAddress, (ModOperation) operation, messageResult, executionContext);
		else if (operation instanceof SetOperation)
			return this.executeSetOnAddress(contributorXris, contributorsXri, relativeTargetAddress, (SetOperation) operation, messageResult, executionContext);
		else if (operation instanceof DelOperation)
			return this.executeDelOnAddress(contributorXris, contributorsXri, relativeTargetAddress, (DelOperation) operation, messageResult, executionContext);
		else if (operation instanceof DoOperation)
			return this.executeDoOnAddress(contributorXris, contributorsXri, relativeTargetAddress, (DoOperation) operation, messageResult, executionContext);
		else
			throw new Xdi2MessagingException("Unknown operation: " + operation.getOperationXri(), null, executionContext);
	}

	public boolean executeGetOnAddress(XDI3Segment[] contributorXris, XDI3Segment contributorsXri, XDI3Segment relativeTargetAddress, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment contextNodeXri = relativeTargetAddress;

		return this.getContext(contributorXris, contributorsXri, contextNodeXri, operation, messageResult, executionContext);
	}

	public boolean executeAddOnAddress(XDI3Segment[] contributorXris, XDI3Segment contributorsXri, XDI3Segment relativeTargetAddress, AddOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment contextNodeXri = relativeTargetAddress;

		return this.addContext(contributorXris, contributorsXri, contextNodeXri, operation, messageResult, executionContext);
	}

	public boolean executeModOnAddress(XDI3Segment[] contributorXris, XDI3Segment contributorsXri, XDI3Segment relativeTargetAddress, ModOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment contextNodeXri = relativeTargetAddress;

		return this.modContext(contributorXris, contributorsXri, contextNodeXri, operation, messageResult, executionContext);
	}

	public boolean executeSetOnAddress(XDI3Segment[] contributorXris, XDI3Segment contributorsXri, XDI3Segment relativeTargetAddress, SetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment contextNodeXri = relativeTargetAddress;

		return this.setContext(contributorXris, contributorsXri, contextNodeXri, operation, messageResult, executionContext);
	}

	public boolean executeDelOnAddress(XDI3Segment[] contributorXris, XDI3Segment contributorsXri, XDI3Segment relativeTargetAddress, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment contextNodeXri = relativeTargetAddress;

		return this.delContext(contributorXris, contributorsXri, contextNodeXri, operation, messageResult, executionContext);
	}

	public boolean executeDoOnAddress(XDI3Segment[] contributorXris, XDI3Segment contributorsXri, XDI3Segment relativeTargetAddress, DoOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment contextNodeXri = relativeTargetAddress;

		return this.doContext(contributorXris, contributorsXri, contextNodeXri, operation, messageResult, executionContext);
	}

	/*
	 * Operations on statements
	 */

	@Override
	public boolean executeOnStatement(XDI3Segment[] contributorXris, XDI3Segment contributorsXri, XDI3Statement relativeTargetStatement, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// execute on statement

		if (operation instanceof GetOperation)
			return this.executeGetOnStatement(contributorXris, contributorsXri, relativeTargetStatement, (GetOperation) operation, messageResult, executionContext);
		else if (operation instanceof AddOperation)
			return this.executeAddOnStatement(contributorXris, contributorsXri, relativeTargetStatement, (AddOperation) operation, messageResult, executionContext);
		else if (operation instanceof ModOperation)
			return this.executeModOnStatement(contributorXris, contributorsXri, relativeTargetStatement, (ModOperation) operation, messageResult, executionContext);
		else if (operation instanceof SetOperation)
			return this.executeSetOnStatement(contributorXris, contributorsXri, relativeTargetStatement, (SetOperation) operation, messageResult, executionContext);
		else if (operation instanceof DelOperation)
			return this.executeDelOnStatement(contributorXris, contributorsXri, relativeTargetStatement, (DelOperation) operation, messageResult, executionContext);
		else if (operation instanceof DoOperation)
			return this.executeDoOnStatement(contributorXris, contributorsXri, relativeTargetStatement, (DoOperation) operation, messageResult, executionContext);
		else
			throw new Xdi2MessagingException("Unknown operation: " + operation.getOperationXri(), null, executionContext);
	}

	public boolean executeGetOnStatement(XDI3Segment[] contributorXris, XDI3Segment contributorsXri, XDI3Statement relativeTargetStatement, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (relativeTargetStatement.isContextNodeStatement())
			return this.executeGetOnContextNodeStatement(contributorXris, contributorsXri, relativeTargetStatement, operation, messageResult, executionContext);
		else if (relativeTargetStatement.isRelationStatement())
			return this.executeGetOnRelationStatement(contributorXris, contributorsXri, relativeTargetStatement, operation, messageResult, executionContext);
		else if (relativeTargetStatement.isLiteralStatement())
			return this.executeGetOnLiteralStatement(contributorXris, contributorsXri, relativeTargetStatement, operation, messageResult, executionContext);
		else
			throw new Xdi2MessagingException("Invalid statement: " + relativeTargetStatement, null, executionContext);
	}

	public boolean executeAddOnStatement(XDI3Segment[] contributorXris, XDI3Segment contributorsXri, XDI3Statement relativeTargetStatement, AddOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (relativeTargetStatement.isContextNodeStatement())
			return this.executeAddOnContextNodeStatement(contributorXris, contributorsXri, relativeTargetStatement, operation, messageResult, executionContext);
		else if (relativeTargetStatement.isRelationStatement())
			return this.executeAddOnRelationStatement(contributorXris, contributorsXri, relativeTargetStatement, operation, messageResult, executionContext);
		else if (relativeTargetStatement.isLiteralStatement())
			return this.executeAddOnLiteralStatement(contributorXris, contributorsXri, relativeTargetStatement, operation, messageResult, executionContext);
		else
			throw new Xdi2MessagingException("Invalid statement: " + relativeTargetStatement, null, executionContext);
	}

	public boolean executeModOnStatement(XDI3Segment[] contributorXris, XDI3Segment contributorsXri, XDI3Statement relativeTargetStatement, ModOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (relativeTargetStatement.isContextNodeStatement())
			return this.executeModOnContextNodeStatement(contributorXris, contributorsXri, relativeTargetStatement, operation, messageResult, executionContext);
		else if (relativeTargetStatement.isRelationStatement())
			return this.executeModOnRelationStatement(contributorXris, contributorsXri, relativeTargetStatement, operation, messageResult, executionContext);
		else if (relativeTargetStatement.isLiteralStatement())
			return this.executeModOnLiteralStatement(contributorXris, contributorsXri, relativeTargetStatement, operation, messageResult, executionContext);
		else
			throw new Xdi2MessagingException("Invalid statement: " + relativeTargetStatement, null, executionContext);
	}

	public boolean executeSetOnStatement(XDI3Segment[] contributorXris, XDI3Segment contributorsXri, XDI3Statement relativeTargetStatement, SetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (relativeTargetStatement.isContextNodeStatement())
			return this.executeSetOnContextNodeStatement(contributorXris, contributorsXri, relativeTargetStatement, operation, messageResult, executionContext);
		else if (relativeTargetStatement.isRelationStatement())
			return this.executeSetOnRelationStatement(contributorXris, contributorsXri, relativeTargetStatement, operation, messageResult, executionContext);
		else if (relativeTargetStatement.isLiteralStatement())
			return this.executeSetOnLiteralStatement(contributorXris, contributorsXri, relativeTargetStatement, operation, messageResult, executionContext);
		else
			throw new Xdi2MessagingException("Invalid statement: " + relativeTargetStatement, null, executionContext);
	}

	public boolean executeDelOnStatement(XDI3Segment[] contributorXris, XDI3Segment contributorsXri, XDI3Statement relativeTargetStatement, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (relativeTargetStatement.isContextNodeStatement())
			return this.executeDelOnContextNodeStatement(contributorXris, contributorsXri, relativeTargetStatement, operation, messageResult, executionContext);
		else if (relativeTargetStatement.isRelationStatement())
			return this.executeDelOnRelationStatement(contributorXris, contributorsXri, relativeTargetStatement, operation, messageResult, executionContext);
		else if (relativeTargetStatement.isLiteralStatement())
			return this.executeDelOnLiteralStatement(contributorXris, contributorsXri, relativeTargetStatement, operation, messageResult, executionContext);
		else
			throw new Xdi2MessagingException("Invalid statement: " + relativeTargetStatement, null, executionContext);
	}

	public boolean executeDoOnStatement(XDI3Segment[] contributorXris, XDI3Segment contributorsXri, XDI3Statement relativeTargetStatement, DoOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (relativeTargetStatement.isContextNodeStatement())
			return this.executeDoOnContextNodeStatement(contributorXris, contributorsXri, relativeTargetStatement, operation, messageResult, executionContext);
		else if (relativeTargetStatement.isRelationStatement())
			return this.executeDoOnRelationStatement(contributorXris, contributorsXri, relativeTargetStatement, operation, messageResult, executionContext);
		else if (relativeTargetStatement.isLiteralStatement())
			return this.executeDoOnLiteralStatement(contributorXris, contributorsXri, relativeTargetStatement, operation, messageResult, executionContext);
		else
			throw new Xdi2MessagingException("Invalid statement: " + relativeTargetStatement, null, executionContext);
	}

	/*
	 * Operations on context node statements
	 */

	public boolean executeGetOnContextNodeStatement(XDI3Segment[] contributorXris, XDI3Segment contributorsXri, XDI3Statement relativeTargetStatement, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment contextNodeXri = relativeTargetStatement.getTargetContextNodeXri();

		return this.getContext(contributorXris, contributorsXri, contextNodeXri, operation, messageResult, executionContext);
	}

	public boolean executeAddOnContextNodeStatement(XDI3Segment[] contributorXris, XDI3Segment contributorsXri, XDI3Statement relativeTargetStatement, AddOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment contextNodeXri = relativeTargetStatement.getTargetContextNodeXri();

		return this.addContext(contributorXris, contributorsXri, contextNodeXri, operation, messageResult, executionContext);
	}

	public boolean executeModOnContextNodeStatement(XDI3Segment[] contributorXris, XDI3Segment contributorsXri, XDI3Statement relativeTargetStatement, ModOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment contextNodeXri = relativeTargetStatement.getTargetContextNodeXri();

		return this.modContext(contributorXris, contributorsXri, contextNodeXri, operation, messageResult, executionContext);
	}

	public boolean executeSetOnContextNodeStatement(XDI3Segment[] contributorXris, XDI3Segment contributorsXri, XDI3Statement relativeTargetStatement, SetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment contextNodeXri = relativeTargetStatement.getTargetContextNodeXri();

		return this.setContext(contributorXris, contributorsXri, contextNodeXri, operation, messageResult, executionContext);
	}

	public boolean executeDelOnContextNodeStatement(XDI3Segment[] contributorXris, XDI3Segment contributorsXri, XDI3Statement relativeTargetStatement, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment contextNodeXri = relativeTargetStatement.getTargetContextNodeXri();

		return this.delContext(contributorXris, contributorsXri, contextNodeXri, operation, messageResult, executionContext);
	}

	public boolean executeDoOnContextNodeStatement(XDI3Segment[] contributorXris, XDI3Segment contributorsXri, XDI3Statement relativeTargetStatement, DoOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment contextNodeXri = relativeTargetStatement.getTargetContextNodeXri();

		return this.doContext(contributorXris, contributorsXri, contextNodeXri, operation, messageResult, executionContext);
	}

	/*
	 * Operations on relation statements
	 */

	public boolean executeGetOnRelationStatement(XDI3Segment[] contributorXris, XDI3Segment contributorsXri, XDI3Statement relativeTargetStatement, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment contextNodeXri = relativeTargetStatement.getContextNodeXri();
		XDI3Segment arcXri = relativeTargetStatement.getRelationArcXri();
		XDI3Segment targetContextNodeXri = relativeTargetStatement.getTargetContextNodeXri();

		return this.getRelation(contributorXris, contributorsXri, contextNodeXri, arcXri, targetContextNodeXri, operation, messageResult, executionContext);
	}

	public boolean executeAddOnRelationStatement(XDI3Segment[] contributorXris, XDI3Segment contributorsXri, XDI3Statement relativeTargetStatement, AddOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment contextNodeXri = relativeTargetStatement.getContextNodeXri();
		XDI3Segment arcXri = relativeTargetStatement.getRelationArcXri();
		XDI3Segment targetContextNodeXri = relativeTargetStatement.getTargetContextNodeXri();

		return this.addRelation(contributorXris, contributorsXri, contextNodeXri, arcXri, targetContextNodeXri, operation, messageResult, executionContext);
	}

	public boolean executeModOnRelationStatement(XDI3Segment[] contributorXris, XDI3Segment contributorsXri, XDI3Statement relativeTargetStatement, ModOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment contextNodeXri = relativeTargetStatement.getContextNodeXri();
		XDI3Segment arcXri = relativeTargetStatement.getRelationArcXri();
		XDI3Segment targetContextNodeXri = relativeTargetStatement.getTargetContextNodeXri();

		return this.modRelation(contributorXris, contributorsXri, contextNodeXri, arcXri, targetContextNodeXri, operation, messageResult, executionContext);
	}

	public boolean executeSetOnRelationStatement(XDI3Segment[] contributorXris, XDI3Segment contributorsXri, XDI3Statement relativeTargetStatement, SetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment contextNodeXri = relativeTargetStatement.getContextNodeXri();
		XDI3Segment arcXri = relativeTargetStatement.getRelationArcXri();
		XDI3Segment targetContextNodeXri = relativeTargetStatement.getTargetContextNodeXri();

		return this.setRelation(contributorXris, contributorsXri, contextNodeXri, arcXri, targetContextNodeXri, operation, messageResult, executionContext);
	}

	public boolean executeDelOnRelationStatement(XDI3Segment[] contributorXris, XDI3Segment contributorsXri, XDI3Statement relativeTargetStatement, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment contextNodeXri = relativeTargetStatement.getContextNodeXri();
		XDI3Segment arcXri = relativeTargetStatement.getRelationArcXri();
		XDI3Segment targetContextNodeXri = relativeTargetStatement.getTargetContextNodeXri();

		return this.delRelation(contributorXris, contributorsXri, contextNodeXri, arcXri, targetContextNodeXri, operation, messageResult, executionContext);
	}

	public boolean executeDoOnRelationStatement(XDI3Segment[] contributorXris, XDI3Segment contributorsXri, XDI3Statement relativeTargetStatement, DoOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment contextNodeXri = relativeTargetStatement.getContextNodeXri();
		XDI3Segment arcXri = relativeTargetStatement.getRelationArcXri();
		XDI3Segment targetContextNodeXri = relativeTargetStatement.getTargetContextNodeXri();

		return this.doRelation(contributorXris, contributorsXri, contextNodeXri, arcXri, targetContextNodeXri, operation, messageResult, executionContext);
	}

	/*
	 * Operations on literal statements
	 */

	public boolean executeGetOnLiteralStatement(XDI3Segment[] contributorXris, XDI3Segment contributorsXri, XDI3Statement relativeTargetStatement, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment contextNodeXri = relativeTargetStatement.getContextNodeXri();
		Object literalData = relativeTargetStatement.getLiteralData();

		return this.getLiteral(contributorXris, contributorsXri, contextNodeXri, literalData, operation, messageResult, executionContext);
	}

	public boolean executeAddOnLiteralStatement(XDI3Segment[] contributorXris, XDI3Segment contributorsXri, XDI3Statement relativeTargetStatement, AddOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment contextNodeXri = relativeTargetStatement.getContextNodeXri();
		Object literalData = relativeTargetStatement.getLiteralData();

		return this.addLiteral(contributorXris, contributorsXri, contextNodeXri, literalData, operation, messageResult, executionContext);
	}

	public boolean executeModOnLiteralStatement(XDI3Segment[] contributorXris, XDI3Segment contributorsXri, XDI3Statement relativeTargetStatement, ModOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment contextNodeXri = relativeTargetStatement.getContextNodeXri();
		Object literalData = relativeTargetStatement.getLiteralData();

		return this.modLiteral(contributorXris, contributorsXri, contextNodeXri, literalData, operation, messageResult, executionContext);
	}

	public boolean executeSetOnLiteralStatement(XDI3Segment[] contributorXris, XDI3Segment contributorsXri, XDI3Statement relativeTargetStatement, SetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment contextNodeXri = relativeTargetStatement.getContextNodeXri();
		Object literalData = relativeTargetStatement.getLiteralData();

		return this.setLiteral(contributorXris, contributorsXri, contextNodeXri, literalData, operation, messageResult, executionContext);
	}

	public boolean executeDelOnLiteralStatement(XDI3Segment[] contributorXris, XDI3Segment contributorsXri, XDI3Statement relativeTargetStatement, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment contextNodeXri = relativeTargetStatement.getContextNodeXri();
		Object literalData = relativeTargetStatement.getLiteralData();

		return this.delLiteral(contributorXris, contributorsXri, contextNodeXri, literalData, operation, messageResult, executionContext);
	}

	public boolean executeDoOnLiteralStatement(XDI3Segment[] contributorXris, XDI3Segment contributorsXri, XDI3Statement relativeTargetStatement, DoOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment contextNodeXri = relativeTargetStatement.getContextNodeXri();
		Object literalData = relativeTargetStatement.getLiteralData();

		return this.doLiteral(contributorXris, contributorsXri, contextNodeXri, literalData, operation, messageResult, executionContext);
	}

	/*
	 * Methods to be overridden by subclasses
	 */

	public boolean getContext(XDI3Segment[] contributorXris, XDI3Segment contributorsXri, XDI3Segment contextNodeXri, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean addContext(XDI3Segment[] contributorXris, XDI3Segment contributorsXri, XDI3Segment contextNodeXri, AddOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean modContext(XDI3Segment[] contributorXris, XDI3Segment contributorsXri, XDI3Segment contextNodeXri, ModOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean setContext(XDI3Segment[] contributorXris, XDI3Segment contributorsXri, XDI3Segment contextNodeXri, SetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean delContext(XDI3Segment[] contributorXris, XDI3Segment contributorsXri, XDI3Segment contextNodeXri, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean doContext(XDI3Segment[] contributorXris, XDI3Segment contributorsXri, XDI3Segment contextNodeXri, DoOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean getRelation(XDI3Segment[] contributorXris, XDI3Segment contributorsXri, XDI3Segment contextNodeXri, XDI3Segment arcXri, XDI3Segment targetContextNodeXri, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		MessageResult tempMessageResult = new MessageResult();

		boolean handled = this.getContext(contributorXris, contributorsXri, contextNodeXri, operation, tempMessageResult, executionContext);

		if (log.isDebugEnabled()) log.debug("Temp result: " + tempMessageResult);

		ContextNode tempContextNode = tempMessageResult.getGraph().getDeepContextNode(XDI3Util.concatXris(contributorsXri, contextNodeXri));
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

		return handled;
	}

	public boolean addRelation(XDI3Segment[] contributorXris, XDI3Segment contributorsXri, XDI3Segment contextNodeXri, XDI3Segment arcXri, XDI3Segment targetContextNodeXri, AddOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean modRelation(XDI3Segment[] contributorXris, XDI3Segment contributorsXri, XDI3Segment contextNodeXri, XDI3Segment arcXri, XDI3Segment targetContextNodeXri, ModOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean setRelation(XDI3Segment[] contributorXris, XDI3Segment contributorsXri, XDI3Segment contextNodeXri, XDI3Segment arcXri, XDI3Segment targetContextNodeXri, SetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean delRelation(XDI3Segment[] contributorXris, XDI3Segment contributorsXri, XDI3Segment contextNodeXri, XDI3Segment arcXri, XDI3Segment targetContextNodeXri,  DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean doRelation(XDI3Segment[] contributorXris, XDI3Segment contributorsXri, XDI3Segment contextNodeXri, XDI3Segment arcXri, XDI3Segment targetContextNodeXri,  DoOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean getLiteral(XDI3Segment[] contributorXris, XDI3Segment contributorsXri, XDI3Segment contextNodeXri, Object literalData, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		MessageResult tempMessageResult = new MessageResult();

		boolean handled = this.getContext(contributorXris, contributorsXri, contextNodeXri, operation, tempMessageResult, executionContext);

		if (log.isDebugEnabled()) log.debug("Temp result: " + tempMessageResult);

		ContextNode tempContextNode = tempMessageResult.getGraph().getDeepContextNode(XDI3Util.concatXris(contributorsXri, contextNodeXri));
		if (tempContextNode == null) return false;

		Literal tempLiteral = tempContextNode.getLiteral();
		if (tempLiteral == null) return false;

		if (literalData.equals(tempLiteral.getLiteralData())) {

			CopyUtil.copyLiteral(tempLiteral, messageResult.getGraph(), null);
		}

		return handled;
	}

	public boolean addLiteral(XDI3Segment[] contributorXris, XDI3Segment contributorsXri, XDI3Segment contextNodeXri, Object literalData, AddOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean modLiteral(XDI3Segment[] contributorXris, XDI3Segment contributorsXri, XDI3Segment contextNodeXri, Object literalData, ModOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean setLiteral(XDI3Segment[] contributorXris, XDI3Segment contributorsXri, XDI3Segment contextNodeXri, Object literalData, SetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean delLiteral(XDI3Segment[] contributorXris, XDI3Segment contributorsXri, XDI3Segment contextNodeXri, Object literalData, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean doLiteral(XDI3Segment[] contributorXris, XDI3Segment contributorsXri, XDI3Segment contextNodeXri, Object literalData, DoOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	/*
	 * Enabled?
	 */

	@Override
	public boolean isEnabled() {

		return this.enabled;
	}

	@Override
	public void setEnabled(boolean enabled) {

		this.enabled = enabled;
	}

	/*
	 * Sub-contributors
	 */

	@Override
	public ContributorMap getContributors() {

		return this.contributors;
	}

	@Override
	public void setContributors(ContributorMap contributors) {

		this.contributors = contributors;
	}

	@Override
	public void setContributors(Map<XDI3Segment, List<Contributor>> contributors) {

		this.contributors.clear();
		this.contributors.putAll(contributors);
	}

	@Override
	public void setContributorsList(ArrayList<Contributor> contributors) {

		this.contributors.clear();
		for (Contributor contributor : contributors) this.contributors.addContributor(contributor);
	}
}
