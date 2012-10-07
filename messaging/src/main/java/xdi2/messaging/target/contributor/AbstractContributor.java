package xdi2.messaging.target.contributor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import xdi2.messaging.target.ExecutionContext;

public abstract class AbstractContributor implements Contributor {

	private ContributorMap contributors;

	public AbstractContributor() {

		this.contributors = new ContributorMap();
	}

	/*
	 * Operations on addresses
	 */

	@Override
	public boolean executeOnAddress(XRI3Segment[] contributorXris, XRI3Segment relativeTargetAddress, XRI3Segment targetAddress, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

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
		else if (operation instanceof DelOperation)
			return this.executeDelOnAddress(contributorXris, relativeTargetAddress, targetAddress, (DelOperation) operation, messageResult, executionContext);
		else
			throw new Xdi2MessagingException("Unknown operation: " + operation.getOperationXri(), null, executionContext);
	}

	public boolean executeGetOnAddress(XRI3Segment[] contributorXris, XRI3Segment relativeTargetAddress, XRI3Segment targetAddress, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XRI3Segment relativeContextNodeXri = relativeTargetAddress;
		XRI3Segment contextNodeXri = targetAddress;

		return this.getContext(contributorXris, relativeContextNodeXri, contextNodeXri, operation, messageResult, executionContext);
	}

	public boolean executeAddOnAddress(XRI3Segment[] contributorXris, XRI3Segment relativeTargetAddress, XRI3Segment targetAddress, AddOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XRI3Segment relativeContextNodeXri = relativeTargetAddress;
		XRI3Segment contextNodeXri = targetAddress;

		return this.addContext(contributorXris, relativeContextNodeXri, contextNodeXri, operation, messageResult, executionContext);
	}

	public boolean executeModOnAddress(XRI3Segment[] contributorXris, XRI3Segment relativeTargetAddress, XRI3Segment targetAddress, ModOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XRI3Segment relativeContextNodeXri = relativeTargetAddress;
		XRI3Segment contextNodeXri = targetAddress;

		return this.modContext(contributorXris, relativeContextNodeXri, contextNodeXri, operation, messageResult, executionContext);
	}

	public boolean executeDelOnAddress(XRI3Segment[] contributorXris, XRI3Segment relativeTargetAddress, XRI3Segment targetAddress, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XRI3Segment relativeContextNodeXri = relativeTargetAddress;
		XRI3Segment contextNodeXri = targetAddress;

		return this.delContext(contributorXris, relativeContextNodeXri, contextNodeXri, operation, messageResult, executionContext);
	}

	/*
	 * Operations on statements
	 */

	@Override
	public boolean executeOnStatement(XRI3Segment[] contributorXris, Statement relativeTargetStatement, Statement targetStatement, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

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
		else if (operation instanceof DelOperation)
			return this.executeDelOnStatement(contributorXris, relativeTargetStatement, targetStatement, (DelOperation) operation, messageResult, executionContext);
		else
			throw new Xdi2MessagingException("Unknown operation: " + operation.getOperationXri(), null, executionContext);
	}

	public boolean executeGetOnStatement(XRI3Segment[] contributorXris, Statement relativeTargetStatement, Statement targetStatement, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (targetStatement instanceof ContextNodeStatement)
			return this.executeGetOnContextNodeStatement(contributorXris, (ContextNodeStatement) relativeTargetStatement, (ContextNodeStatement) targetStatement, operation, messageResult, executionContext);
		else if (targetStatement instanceof RelationStatement)
			return this.executeGetOnRelationStatement(contributorXris, (RelationStatement) relativeTargetStatement, (RelationStatement) targetStatement, operation, messageResult, executionContext);
		else if (targetStatement instanceof LiteralStatement)
			return this.executeGetOnLiteralStatement(contributorXris, (LiteralStatement) relativeTargetStatement, (LiteralStatement) targetStatement, operation, messageResult, executionContext);
		else
			throw new Xdi2MessagingException("Unknown statement type: " + targetStatement.getClass().getCanonicalName(), null, executionContext);
	}

	public boolean executeAddOnStatement(XRI3Segment[] contributorXris, Statement relativeTargetStatement, Statement targetStatement, AddOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (targetStatement instanceof ContextNodeStatement)
			return this.executeAddOnContextNodeStatement(contributorXris, (ContextNodeStatement) relativeTargetStatement, (ContextNodeStatement) targetStatement, operation, messageResult, executionContext);
		else if (targetStatement instanceof RelationStatement)
			return this.executeAddOnRelationStatement(contributorXris, (RelationStatement) relativeTargetStatement, (RelationStatement) targetStatement, operation, messageResult, executionContext);
		else if (targetStatement instanceof LiteralStatement)
			return this.executeAddOnLiteralStatement(contributorXris, (LiteralStatement) relativeTargetStatement, (LiteralStatement) targetStatement, operation, messageResult, executionContext);
		else
			throw new Xdi2MessagingException("Unknown statement type: " + targetStatement.getClass().getCanonicalName(), null, executionContext);
	}

	public boolean executeModOnStatement(XRI3Segment[] contributorXris, Statement relativeTargetStatement, Statement targetStatement, ModOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (targetStatement instanceof ContextNodeStatement)
			return this.executeModOnContextNodeStatement(contributorXris, (ContextNodeStatement) relativeTargetStatement, (ContextNodeStatement) targetStatement, operation, messageResult, executionContext);
		else if (targetStatement instanceof RelationStatement)
			return this.executeModOnRelationStatement(contributorXris, (RelationStatement) relativeTargetStatement, (RelationStatement) targetStatement, operation, messageResult, executionContext);
		else if (targetStatement instanceof LiteralStatement)
			return this.executeModOnLiteralStatement(contributorXris, (LiteralStatement) relativeTargetStatement, (LiteralStatement) targetStatement, operation, messageResult, executionContext);
		else
			throw new Xdi2MessagingException("Unknown statement type: " + targetStatement.getClass().getCanonicalName(), null, executionContext);
	}

	public boolean executeDelOnStatement(XRI3Segment[] contributorXris, Statement relativeTargetStatement, Statement targetStatement, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (targetStatement instanceof ContextNodeStatement)
			return this.executeDelOnContextNodeStatement(contributorXris, (ContextNodeStatement) relativeTargetStatement, (ContextNodeStatement) targetStatement, operation, messageResult, executionContext);
		else if (targetStatement instanceof RelationStatement)
			return this.executeDelOnRelationStatement(contributorXris, (RelationStatement) relativeTargetStatement, (RelationStatement) targetStatement, operation, messageResult, executionContext);
		else if (targetStatement instanceof LiteralStatement)
			return this.executeDelOnLiteralStatement(contributorXris, (LiteralStatement) relativeTargetStatement, (LiteralStatement) targetStatement, operation, messageResult, executionContext);
		else
			throw new Xdi2MessagingException("Unknown statement type: " + targetStatement.getClass().getCanonicalName(), null, executionContext);
	}

	/*
	 * Operations on context node statements
	 */

	public boolean executeGetOnContextNodeStatement(XRI3Segment[] contributorXris, ContextNodeStatement relativeContextNodeStatement, ContextNodeStatement contextNodeStatement, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XRI3Segment relativeContextNodeXri = new XRI3Segment(relativeContextNodeStatement.getSubject().toString() + relativeContextNodeStatement.getObject().toString());
		XRI3Segment contextNodeXri = new XRI3Segment(contextNodeStatement.getSubject().toString() + contextNodeStatement.getObject().toString());

		return this.getContext(contributorXris, relativeContextNodeXri, contextNodeXri, operation, messageResult, executionContext);
	}

	public boolean executeAddOnContextNodeStatement(XRI3Segment[] contributorXris, ContextNodeStatement relativeContextNodeStatement, ContextNodeStatement contextNodeStatement, AddOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XRI3Segment relativeContextNodeXri = new XRI3Segment(relativeContextNodeStatement.getSubject().toString() + relativeContextNodeStatement.getObject().toString());
		XRI3Segment contextNodeXri = new XRI3Segment(contextNodeStatement.getSubject().toString() + contextNodeStatement.getObject().toString());

		return this.addContext(contributorXris, relativeContextNodeXri, contextNodeXri, operation, messageResult, executionContext);
	}

	public boolean executeModOnContextNodeStatement(XRI3Segment[] contributorXris, ContextNodeStatement relativeContextNodeStatement, ContextNodeStatement contextNodeStatement, ModOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XRI3Segment relativeContextNodeXri = new XRI3Segment(relativeContextNodeStatement.getSubject().toString() + relativeContextNodeStatement.getObject().toString());
		XRI3Segment contextNodeXri = new XRI3Segment(contextNodeStatement.getSubject().toString() + contextNodeStatement.getObject().toString());

		return this.modContext(contributorXris, relativeContextNodeXri, contextNodeXri, operation, messageResult, executionContext);
	}

	public boolean executeDelOnContextNodeStatement(XRI3Segment[] contributorXris, ContextNodeStatement relativeContextNodeStatement, ContextNodeStatement contextNodeStatement, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XRI3Segment relativeContextNodeXri = new XRI3Segment(relativeContextNodeStatement.getSubject().toString() + relativeContextNodeStatement.getObject().toString());
		XRI3Segment contextNodeXri = new XRI3Segment(contextNodeStatement.getSubject().toString() + contextNodeStatement.getObject().toString());

		return this.delContext(contributorXris, relativeContextNodeXri, contextNodeXri, operation, messageResult, executionContext);
	}

	/*
	 * Operations on relation statements
	 */

	public boolean executeGetOnRelationStatement(XRI3Segment[] contributorXris, RelationStatement relativeRelationStatement, RelationStatement relationStatement, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XRI3Segment relativeContextNodeXri = relativeRelationStatement.getSubject();
		XRI3Segment contextNodeXri = relationStatement.getSubject();
		XRI3Segment arcXri = relationStatement.getPredicate();
		XRI3Segment targetContextNodeXri = relationStatement.getObject();

		return this.getRelation(contributorXris, relativeContextNodeXri, contextNodeXri, arcXri, targetContextNodeXri, operation, messageResult, executionContext);
	}

	public boolean executeAddOnRelationStatement(XRI3Segment[] contributorXris, RelationStatement relativeRelationStatement, RelationStatement relationStatement, AddOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XRI3Segment relativeContextNodeXri = relativeRelationStatement.getSubject();
		XRI3Segment contextNodeXri = relationStatement.getSubject();
		XRI3Segment arcXri = relationStatement.getPredicate();
		XRI3Segment targetContextNodeXri = relationStatement.getObject();

		return this.addRelation(contributorXris, relativeContextNodeXri, contextNodeXri, arcXri, targetContextNodeXri, operation, messageResult, executionContext);
	}

	public boolean executeModOnRelationStatement(XRI3Segment[] contributorXris, RelationStatement relativeRelationStatement, RelationStatement relationStatement, ModOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XRI3Segment relativeContextNodeXri = relativeRelationStatement.getSubject();
		XRI3Segment contextNodeXri = relationStatement.getSubject();
		XRI3Segment arcXri = relationStatement.getPredicate();
		XRI3Segment targetContextNodeXri = relationStatement.getObject();

		return this.modRelation(contributorXris, relativeContextNodeXri, contextNodeXri, arcXri, targetContextNodeXri, operation, messageResult, executionContext);
	}

	public boolean executeDelOnRelationStatement(XRI3Segment[] contributorXris, RelationStatement relativeRelationStatement, RelationStatement relationStatement, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XRI3Segment relativeContextNodeXri = relativeRelationStatement.getSubject();
		XRI3Segment contextNodeXri = relationStatement.getSubject();
		XRI3Segment arcXri = relationStatement.getPredicate();
		XRI3Segment targetContextNodeXri = relationStatement.getObject();

		return this.delRelation(contributorXris, relativeContextNodeXri, contextNodeXri, arcXri, targetContextNodeXri, operation, messageResult, executionContext);
	}

	/*
	 * Operations on literal statements
	 */

	public boolean executeGetOnLiteralStatement(XRI3Segment[] contributorXris, LiteralStatement relativeLiteralStatement, LiteralStatement literalStatement, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XRI3Segment relativeContextNodeXri = relativeLiteralStatement.getSubject();
		XRI3Segment contextNodeXri = literalStatement.getSubject();
		String literalData = XDIUtil.dataXriSegmentToString(literalStatement.getObject());

		return this.getLiteral(contributorXris, relativeContextNodeXri, contextNodeXri, literalData, operation, messageResult, executionContext);
	}

	public boolean executeAddOnLiteralStatement(XRI3Segment[] contributorXris, LiteralStatement relativeLiteralStatement, LiteralStatement literalStatement, AddOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XRI3Segment relativeContextNodeXri = relativeLiteralStatement.getSubject();
		XRI3Segment contextNodeXri = literalStatement.getSubject();
		String literalData = XDIUtil.dataXriSegmentToString(literalStatement.getObject());

		return this.addLiteral(contributorXris, relativeContextNodeXri, contextNodeXri, literalData, operation, messageResult, executionContext);
	}

	public boolean executeModOnLiteralStatement(XRI3Segment[] contributorXris, LiteralStatement relativeLiteralStatement, LiteralStatement literalStatement, ModOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XRI3Segment relativeContextNodeXri = relativeLiteralStatement.getSubject();
		XRI3Segment contextNodeXri = literalStatement.getSubject();
		String literalData = XDIUtil.dataXriSegmentToString(literalStatement.getObject());

		return this.modLiteral(contributorXris, relativeContextNodeXri, contextNodeXri, literalData, operation, messageResult, executionContext);
	}

	public boolean executeDelOnLiteralStatement(XRI3Segment[] contributorXris, LiteralStatement relativeLiteralStatement, LiteralStatement literalStatement, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XRI3Segment relativeContextNodeXri = relativeLiteralStatement.getSubject();
		XRI3Segment contextNodeXri = literalStatement.getSubject();
		String literalData = XDIUtil.dataXriSegmentToString(literalStatement.getObject());

		return this.delLiteral(contributorXris, relativeContextNodeXri, contextNodeXri, literalData, operation, messageResult, executionContext);
	}

	/*
	 * Methods to be overridden by subclasses
	 */

	public boolean getContext(XRI3Segment[] contributorXris, XRI3Segment relativeContextNodeXri, XRI3Segment contextNodeXri, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean addContext(XRI3Segment[] contributorXris, XRI3Segment relativeContextNodeXri, XRI3Segment contextNodeXri, AddOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean modContext(XRI3Segment[] contributorXris, XRI3Segment relativeContextNodeXri, XRI3Segment contextNodeXri, ModOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean delContext(XRI3Segment[] contributorXris, XRI3Segment relativeContextNodeXri, XRI3Segment contextNodeXri, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean getRelation(XRI3Segment[] contributorXris, XRI3Segment relativeContextNodeXri, XRI3Segment arcXri, XRI3Segment targetContextNodeXri, XRI3Segment contextNodeXri, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		MessageResult tempMessageResult = new MessageResult();

		this.getContext(contributorXris, relativeContextNodeXri, contextNodeXri, operation, tempMessageResult, executionContext);

		ContextNode tempContextNode = tempMessageResult.getGraph().findContextNode(contextNodeXri, false);
		if (tempContextNode == null) return false;

		boolean isObjectVariable = Variables.isVariable(targetContextNodeXri);

		if (arcXri.equals(XDIConstants.XRI_S_LITERAL)) {

			if (isObjectVariable) {

				Literal literal = tempContextNode.getLiteral();
				if (literal == null) return false;

				CopyUtil.copyLiteral(literal, messageResult.getGraph(), null);
			}
		} else {

			if (isObjectVariable) {

				Iterator<Relation> relations = tempContextNode.getRelations(arcXri);

				while (relations.hasNext()) CopyUtil.copyRelation(relations.next(), messageResult.getGraph(), null);
			} else {

				Relation relation = tempContextNode.getRelation(arcXri, targetContextNodeXri);
				if (relation == null) return false;

				CopyUtil.copyRelation(relation, messageResult.getGraph(), null);
			}
		}

		return false;
	}

	public boolean addRelation(XRI3Segment[] contributorXris, XRI3Segment relativeContextNodeXri, XRI3Segment arcXri, XRI3Segment targetContextNodeXri, XRI3Segment contextNodeXri, AddOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean modRelation(XRI3Segment[] contributorXris, XRI3Segment relativeContextNodeXri, XRI3Segment arcXri, XRI3Segment targetContextNodeXri, XRI3Segment contextNodeXri, ModOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean delRelation(XRI3Segment[] contributorXris, XRI3Segment relativeContextNodeXri, XRI3Segment arcXri, XRI3Segment targetContextNodeXri,XRI3Segment contextNodeXri,  DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean getLiteral(XRI3Segment[] contributorXris, XRI3Segment relativeContextNodeXri, XRI3Segment contextNodeXri, String literalData, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		MessageResult tempMessageResult = new MessageResult();

		this.getContext(contributorXris, relativeContextNodeXri, contextNodeXri, operation, tempMessageResult, executionContext);

		ContextNode tempContextNode = tempMessageResult.getGraph().findContextNode(contextNodeXri, false);
		if (tempContextNode == null) return false;

		Literal tempLiteral = tempContextNode.getLiteral();
		if (tempLiteral == null) return false;

		if (! tempLiteral.getLiteralData().equals(literalData)) return false;

		CopyUtil.copyLiteral(tempLiteral, messageResult.getGraph(), null);

		return false;
	}

	public boolean addLiteral(XRI3Segment[] contributorXris, XRI3Segment relativeContextNodeXri, XRI3Segment contextNodeXri, String literalData, AddOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean modLiteral(XRI3Segment[] contributorXris, XRI3Segment relativeContextNodeXri, XRI3Segment contextNodeXri, String literalData, ModOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean delLiteral(XRI3Segment[] contributorXris, XRI3Segment relativeContextNodeXri, XRI3Segment contextNodeXri, String literalData, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
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

	public void setContributors(Map<XRI3Segment, List<Contributor>> contributors) {

		this.contributors.clear();
		this.contributors.putAll(contributors);
	}

	public void setContributorsList(ArrayList<Contributor> contributors) {

		this.contributors.clear();
		for (Contributor contributor : contributors) this.contributors.addContributor(contributor);
	}
}
