package xdi2.messaging.target.contributor;

import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIStatement;
import xdi2.core.util.StatementUtil;
import xdi2.core.util.AddressUtil;
import xdi2.messaging.DelOperation;
import xdi2.messaging.DoOperation;
import xdi2.messaging.GetOperation;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import xdi2.messaging.SetOperation;
import xdi2.messaging.context.ExecutionContext;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.impl.AbstractExtension;
import xdi2.messaging.target.impl.graph.GraphContextHandler;

public abstract class AbstractContributor extends AbstractExtension<MessagingTarget> implements Contributor {

	private ContributorMap contributors;

	public AbstractContributor(int initPriority, int shutdownPriority) {

		super(initPriority, shutdownPriority);

		this.contributors = new ContributorMap();
	}

	public AbstractContributor() {

		super();

		this.contributors = new ContributorMap ();
	}

	/*
	 * Operations on addresses
	 */

	@Override
	public ContributorResult executeOnAddress(XDIAddress[] contributorAddresss, XDIAddress contributorsAddress, XDIAddress relativeTargetAddress, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// execute on address

		if (operation instanceof GetOperation)
			return this.executeGetOnAddress(contributorAddresss, contributorsAddress, relativeTargetAddress, (GetOperation) operation, messageResult, executionContext);
		else if (operation instanceof SetOperation)
			return this.executeSetOnAddress(contributorAddresss, contributorsAddress, relativeTargetAddress, (SetOperation) operation, messageResult, executionContext);
		else if (operation instanceof DelOperation)
			return this.executeDelOnAddress(contributorAddresss, contributorsAddress, relativeTargetAddress, (DelOperation) operation, messageResult, executionContext);
		else if (operation instanceof DoOperation)
			return this.executeDoOnAddress(contributorAddresss, contributorsAddress, relativeTargetAddress, (DoOperation) operation, messageResult, executionContext);
		else
			throw new Xdi2MessagingException("Unknown operation: " + operation.getOperationAddress(), null, executionContext);
	}

	public ContributorResult executeGetOnAddress(XDIAddress[] contributorAddresss, XDIAddress contributorsAddress, XDIAddress relativeTargetAddress, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return ContributorResult.DEFAULT;
	}

	public ContributorResult executeSetOnAddress(XDIAddress[] contributorAddresss, XDIAddress contributorsAddress, XDIAddress relativeTargetAddress, SetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return ContributorResult.DEFAULT;
	}

	public ContributorResult executeDelOnAddress(XDIAddress[] contributorAddresss, XDIAddress contributorsAddress, XDIAddress relativeTargetAddress, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return ContributorResult.DEFAULT;
	}

	public ContributorResult executeDoOnAddress(XDIAddress[] contributorAddresss, XDIAddress contributorsAddress, XDIAddress relativeTargetAddress, DoOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return ContributorResult.DEFAULT;
	}

	/*
	 * Operations on statements
	 */

	@Override
	public ContributorResult executeOnStatement(XDIAddress[] contributorAddresss, XDIAddress contributorsAddress, XDIStatement relativeTargetStatement, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// execute on statement

		if (operation instanceof GetOperation)
			return this.executeGetOnStatement(contributorAddresss, contributorsAddress, relativeTargetStatement, (GetOperation) operation, messageResult, executionContext);
		else if (operation instanceof SetOperation)
			return this.executeSetOnStatement(contributorAddresss, contributorsAddress, relativeTargetStatement, (SetOperation) operation, messageResult, executionContext);
		else if (operation instanceof DelOperation)
			return this.executeDelOnStatement(contributorAddresss, contributorsAddress, relativeTargetStatement, (DelOperation) operation, messageResult, executionContext);
		else if (operation instanceof DoOperation)
			return this.executeDoOnStatement(contributorAddresss, contributorsAddress, relativeTargetStatement, (DoOperation) operation, messageResult, executionContext);
		else
			throw new Xdi2MessagingException("Unknown operation: " + operation.getOperationAddress(), null, executionContext);
	}

	public ContributorResult executeGetOnStatement(XDIAddress[] contributorAddresss, XDIAddress contributorsAddress, XDIStatement relativeTargetStatement, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (relativeTargetStatement.isContextNodeStatement())
			return this.executeGetOnContextNodeStatement(contributorAddresss, contributorsAddress, relativeTargetStatement, operation, messageResult, executionContext);
		else if (relativeTargetStatement.isRelationStatement())
			return this.executeGetOnRelationStatement(contributorAddresss, contributorsAddress, relativeTargetStatement, operation, messageResult, executionContext);
		else if (relativeTargetStatement.isLiteralStatement())
			return this.executeGetOnLiteralStatement(contributorAddresss, contributorsAddress, relativeTargetStatement, operation, messageResult, executionContext);
		else
			throw new Xdi2MessagingException("Invalid statement: " + relativeTargetStatement, null, executionContext);
	}

	public ContributorResult executeSetOnStatement(XDIAddress[] contributorAddresss, XDIAddress contributorsAddress, XDIStatement relativeTargetStatement, SetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (relativeTargetStatement.isContextNodeStatement())
			return this.executeSetOnContextNodeStatement(contributorAddresss, contributorsAddress, relativeTargetStatement, operation, messageResult, executionContext);
		else if (relativeTargetStatement.isRelationStatement())
			return this.executeSetOnRelationStatement(contributorAddresss, contributorsAddress, relativeTargetStatement, operation, messageResult, executionContext);
		else if (relativeTargetStatement.isLiteralStatement())
			return this.executeSetOnLiteralStatement(contributorAddresss, contributorsAddress, relativeTargetStatement, operation, messageResult, executionContext);
		else
			throw new Xdi2MessagingException("Invalid statement: " + relativeTargetStatement, null, executionContext);
	}

	public ContributorResult executeDelOnStatement(XDIAddress[] contributorAddresss, XDIAddress contributorsAddress, XDIStatement relativeTargetStatement, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (relativeTargetStatement.isContextNodeStatement())
			return this.executeDelOnContextNodeStatement(contributorAddresss, contributorsAddress, relativeTargetStatement, operation, messageResult, executionContext);
		else if (relativeTargetStatement.isRelationStatement())
			return this.executeDelOnRelationStatement(contributorAddresss, contributorsAddress, relativeTargetStatement, operation, messageResult, executionContext);
		else if (relativeTargetStatement.isLiteralStatement())
			return this.executeDelOnLiteralStatement(contributorAddresss, contributorsAddress, relativeTargetStatement, operation, messageResult, executionContext);
		else
			throw new Xdi2MessagingException("Invalid statement: " + relativeTargetStatement, null, executionContext);
	}

	public ContributorResult executeDoOnStatement(XDIAddress[] contributorAddresss, XDIAddress contributorsAddress, XDIStatement relativeTargetStatement, DoOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (relativeTargetStatement.isContextNodeStatement())
			return this.executeDoOnContextNodeStatement(contributorAddresss, contributorsAddress, relativeTargetStatement, operation, messageResult, executionContext);
		else if (relativeTargetStatement.isRelationStatement())
			return this.executeDoOnRelationStatement(contributorAddresss, contributorsAddress, relativeTargetStatement, operation, messageResult, executionContext);
		else if (relativeTargetStatement.isLiteralStatement())
			return this.executeDoOnLiteralStatement(contributorAddresss, contributorsAddress, relativeTargetStatement, operation, messageResult, executionContext);
		else
			throw new Xdi2MessagingException("Invalid statement: " + relativeTargetStatement, null, executionContext);
	}

	/*
	 * Operations on context node statements
	 */

	public ContributorResult executeGetOnContextNodeStatement(XDIAddress[] contributorAddresss, XDIAddress contributorsAddress, XDIStatement relativeTargetStatement, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDIAddress relativeTargetAddress = relativeTargetStatement.getContextNodeAddress();
		XDIStatement targetStatement = StatementUtil.concatAddressStatement(contributorsAddress, relativeTargetStatement);

		MessageResult tempMessageResult = new MessageResult();

		ContributorResult contributorResult = this.executeGetOnAddress(contributorAddresss, contributorsAddress, relativeTargetAddress, operation, tempMessageResult, executionContext);

		new GraphContextHandler(tempMessageResult.getGraph()).executeGetOnContextNodeStatement(targetStatement, operation, messageResult, executionContext);

		return contributorResult;
	}

	public ContributorResult executeSetOnContextNodeStatement(XDIAddress[] contributorAddresss, XDIAddress contributorsAddress, XDIStatement relativeTargetStatement, SetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDIAddress relativeTargetAddress = AddressUtil.concatAddresses(relativeTargetStatement.getContextNodeAddress(), relativeTargetStatement.getContextNodeArc());

		return this.executeSetOnAddress(contributorAddresss, contributorsAddress, relativeTargetAddress, operation, messageResult, executionContext);
	}

	public ContributorResult executeDelOnContextNodeStatement(XDIAddress[] contributorAddresss, XDIAddress contributorsAddress, XDIStatement relativeTargetStatement, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDIAddress relativeTargetAddress = AddressUtil.concatAddresses(relativeTargetStatement.getContextNodeAddress(), relativeTargetStatement.getContextNodeArc());

		return this.executeDelOnAddress(contributorAddresss, contributorsAddress, relativeTargetAddress, operation, messageResult, executionContext);
	}

	public ContributorResult executeDoOnContextNodeStatement(XDIAddress[] contributorAddresss, XDIAddress contributorsAddress, XDIStatement relativeTargetStatement, DoOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return ContributorResult.DEFAULT;
	}

	/*
	 * Operations on relation statements
	 */

	public ContributorResult executeGetOnRelationStatement(XDIAddress[] contributorAddresss, XDIAddress contributorsAddress, XDIStatement relativeTargetStatement, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDIAddress relativeTargetAddress = relativeTargetStatement.getContextNodeAddress();
		XDIStatement targetStatement = StatementUtil.concatAddressStatement(contributorsAddress, relativeTargetStatement);

		MessageResult tempMessageResult = new MessageResult();

		ContributorResult contributorResult = this.executeGetOnAddress(contributorAddresss, contributorsAddress, relativeTargetAddress, operation, tempMessageResult, executionContext);

		new GraphContextHandler(tempMessageResult.getGraph()).executeGetOnRelationStatement(targetStatement, operation, messageResult, executionContext);

		return contributorResult;
	}

	public ContributorResult executeSetOnRelationStatement(XDIAddress[] contributorAddresss, XDIAddress contributorsAddress, XDIStatement relativeTargetStatement, SetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return ContributorResult.DEFAULT;
	}

	public ContributorResult executeDelOnRelationStatement(XDIAddress[] contributorAddresss, XDIAddress contributorsAddress, XDIStatement relativeTargetStatement, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return ContributorResult.DEFAULT;
	}

	public ContributorResult executeDoOnRelationStatement(XDIAddress[] contributorAddresss, XDIAddress contributorsAddress, XDIStatement relativeTargetStatement, DoOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return ContributorResult.DEFAULT;
	}

	/*
	 * Operations on literal statements
	 */

	public ContributorResult executeGetOnLiteralStatement(XDIAddress[] contributorAddresss, XDIAddress contributorsAddress, XDIStatement relativeTargetStatement, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDIAddress relativeTargetAddress = relativeTargetStatement.getContextNodeAddress();
		XDIStatement targetStatement = StatementUtil.concatAddressStatement(contributorsAddress, relativeTargetStatement);

		MessageResult tempMessageResult = new MessageResult();

		ContributorResult contributorResult = this.executeGetOnAddress(contributorAddresss, contributorsAddress, relativeTargetAddress, operation, tempMessageResult, executionContext);

		new GraphContextHandler(tempMessageResult.getGraph()).executeGetOnLiteralStatement(targetStatement, operation, messageResult, executionContext);

		return contributorResult;
	}

	public ContributorResult executeSetOnLiteralStatement(XDIAddress[] contributorAddresss, XDIAddress contributorsAddress, XDIStatement relativeTargetStatement, SetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return ContributorResult.DEFAULT;
	}

	public ContributorResult executeDelOnLiteralStatement(XDIAddress[] contributorAddresss, XDIAddress contributorsAddress, XDIStatement relativeTargetStatement, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return ContributorResult.DEFAULT;
	}

	public ContributorResult executeDoOnLiteralStatement(XDIAddress[] contributorAddresss, XDIAddress contributorsAddress, XDIStatement relativeTargetStatement, DoOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return ContributorResult.DEFAULT;
	}

	/*
	 * Contributor mount
	 */

	@Override
	public ContributorMount getContributorMount() {

		ContributorMount contributorMount = this.getClass().getAnnotation(ContributorMount.class);
		if (contributorMount == null) throw new NullPointerException("No ContributorMount annotation on contributor " + this.getClass().getSimpleName());

		return contributorMount;
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
}
