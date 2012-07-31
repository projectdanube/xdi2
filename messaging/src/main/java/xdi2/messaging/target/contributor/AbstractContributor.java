package xdi2.messaging.target.contributor;

import xdi2.core.ContextNode;
import xdi2.core.Relation;
import xdi2.core.Statement;
import xdi2.core.Statement.ContextNodeStatement;
import xdi2.core.Statement.LiteralStatement;
import xdi2.core.Statement.RelationStatement;
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

	private boolean filter;

	public AbstractContributor(boolean filter) {

		this.filter = filter;
	}

	public AbstractContributor() {

		this(true);
	}

	/*
	 * Operations on addresses
	 */

	@Override
	public boolean executeOnAddress(XRI3Segment targetAddress, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XRI3Segment contextNodeXri = targetAddress;

		if (operation instanceof GetOperation)
			return this.get(contextNodeXri, (GetOperation) operation, messageResult, executionContext);
		else if (operation instanceof AddOperation)
			return this.add(contextNodeXri, (AddOperation) operation, messageResult, executionContext);
		else if (operation instanceof ModOperation)
			return this.mod(contextNodeXri, (ModOperation) operation, messageResult, executionContext);
		else if (operation instanceof DelOperation)
			return this.del(contextNodeXri, (DelOperation) operation, messageResult, executionContext);
		else
			throw new Xdi2MessagingException("Unknown operation: " + operation.getOperationXri(), null, operation);
	}

	/*
	 * Operations on statements
	 */

	@Override
	public boolean executeOnStatement(Statement targetStatement, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XRI3Segment contextNodeXri = targetStatement instanceof ContextNodeStatement ? new XRI3Segment(targetStatement.getSubject().toString() + targetStatement.getObject().toString()) : targetStatement.getSubject();

		if (targetStatement instanceof ContextNodeStatement) {

			if (operation instanceof GetOperation)
				return this.get(contextNodeXri, (GetOperation) operation, messageResult, executionContext);
			else if (operation instanceof AddOperation)
				return this.add(contextNodeXri, (AddOperation) operation, messageResult, executionContext);
			else if (operation instanceof ModOperation)
				return this.mod(contextNodeXri, (ModOperation) operation, messageResult, executionContext);
			else if (operation instanceof DelOperation)
				return this.del(contextNodeXri, (DelOperation) operation, messageResult, executionContext);
		} else if (targetStatement instanceof RelationStatement) {

			XRI3Segment arcXri = targetStatement.getPredicate();
			XRI3Segment targetContextNodeXri = targetStatement.getObject();

			if (operation instanceof GetOperation)
				return this.getRelation(contextNodeXri, arcXri, targetContextNodeXri, (GetOperation) operation, messageResult, executionContext);
			else if (operation instanceof AddOperation)
				return this.addRelation(contextNodeXri, arcXri, targetContextNodeXri, (AddOperation) operation, messageResult, executionContext);
			else if (operation instanceof ModOperation)
				return this.modRelation(contextNodeXri, arcXri, targetContextNodeXri, (ModOperation) operation, messageResult, executionContext);
			else if (operation instanceof DelOperation)
				return this.delRelation(contextNodeXri, arcXri, targetContextNodeXri, (DelOperation) operation, messageResult, executionContext);
		} else if (targetStatement instanceof LiteralStatement) {

			String literalData = XDIUtil.dataXriSegmentToString(targetStatement.getObject());

			if (operation instanceof GetOperation)
				return this.getLiteral(contextNodeXri, literalData, (GetOperation) operation, messageResult, executionContext);
			else if (operation instanceof AddOperation)
				return this.addLiteral(contextNodeXri, literalData, (AddOperation) operation, messageResult, executionContext);
			else if (operation instanceof ModOperation)
				return this.modLiteral(contextNodeXri, literalData, (ModOperation) operation, messageResult, executionContext);
			else if (operation instanceof DelOperation)
				return this.delLiteral(contextNodeXri, literalData, (DelOperation) operation, messageResult, executionContext);
		} else {

			throw new Xdi2MessagingException("Unknown operation: " + operation.getOperationXri(), null, operation);
		}

		return false;
	}

	/*
	 * Contributor methods
	 */

	public boolean get(XRI3Segment contextNodeXri, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean add(XRI3Segment contextNodeXri, AddOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean mod(XRI3Segment contextNodeXri, ModOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean del(XRI3Segment contextNodeXri, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean getRelation(XRI3Segment contextNodeXri, XRI3Segment arcXri, XRI3Segment targetContextNodeXri, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		MessageResult tempMessageResult = new MessageResult();

		if (this.getFilter()) {

			this.get(targetContextNodeXri, operation, tempMessageResult, executionContext);

			ContextNode tempContextNode = tempMessageResult.getGraph().findContextNode(contextNodeXri, false);
			if (tempContextNode == null) return false;

			Relation tempRelation = tempContextNode.getRelation(arcXri, targetContextNodeXri);
			if (tempRelation == null) return false;

			CopyUtil.copyRelation(tempRelation, messageResult.getGraph(), null);
		}

		return false;
	}

	public boolean addRelation(XRI3Segment contextNodeXri, XRI3Segment arcXri, XRI3Segment targetContextNodeXri, AddOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean modRelation(XRI3Segment contextNodeXri, XRI3Segment arcXri, XRI3Segment targetContextNodeXri, ModOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean delRelation(XRI3Segment contextNodeXri, XRI3Segment arcXri, XRI3Segment targetContextNodeXri, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean getLiteral(XRI3Segment contextNodeXri, String literalData, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean addLiteral(XRI3Segment contextNodeXri, String literalData, AddOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean modLiteral(XRI3Segment contextNodeXri, String literalData, ModOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean delLiteral(XRI3Segment contextNodeXri, String literalData, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	/*
	 * Getters and setters
	 */

	public boolean getFilter() {

		return this.filter;
	}

	public void setFilter(boolean filter) {

		this.filter = filter;
	}
}
