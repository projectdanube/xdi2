package xdi2.messaging.target.contributor.impl;

import xdi2.core.Graph;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIStatement;
import xdi2.core.util.XDIAddressUtil;
import xdi2.core.util.XDIStatementUtil;
import xdi2.messaging.operations.DelOperation;
import xdi2.messaging.operations.DoOperation;
import xdi2.messaging.operations.GetOperation;
import xdi2.messaging.operations.Operation;
import xdi2.messaging.operations.SetOperation;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.contributor.Contributor;
import xdi2.messaging.target.contributor.ContributorMap;
import xdi2.messaging.target.contributor.ContributorMount;
import xdi2.messaging.target.contributor.ContributorResult;
import xdi2.messaging.target.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.execution.ExecutionContext;
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
	public ContributorResult executeOnAddress(XDIAddress[] contributorAddresses, XDIAddress contributorsAddress, XDIAddress relativeTargetAddress, Operation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		// execute on address

		if (operation instanceof GetOperation)
			return this.executeGetOnAddress(contributorAddresses, contributorsAddress, relativeTargetAddress, (GetOperation) operation, operationResultGraph, executionContext);
		else if (operation instanceof SetOperation)
			return this.executeSetOnAddress(contributorAddresses, contributorsAddress, relativeTargetAddress, (SetOperation) operation, operationResultGraph, executionContext);
		else if (operation instanceof DelOperation)
			return this.executeDelOnAddress(contributorAddresses, contributorsAddress, relativeTargetAddress, (DelOperation) operation, operationResultGraph, executionContext);
		else if (operation instanceof DoOperation)
			return this.executeDoOnAddress(contributorAddresses, contributorsAddress, relativeTargetAddress, (DoOperation) operation, operationResultGraph, executionContext);

		return ContributorResult.DEFAULT;
	}

	public ContributorResult executeGetOnAddress(XDIAddress[] contributorAddresses, XDIAddress contributorsAddress, XDIAddress relativeTargetAddress, GetOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		return ContributorResult.DEFAULT;
	}

	public ContributorResult executeSetOnAddress(XDIAddress[] contributorAddresses, XDIAddress contributorsAddress, XDIAddress relativeTargetAddress, SetOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		return ContributorResult.DEFAULT;
	}

	public ContributorResult executeDelOnAddress(XDIAddress[] contributorAddresses, XDIAddress contributorsAddress, XDIAddress relativeTargetAddress, DelOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		return ContributorResult.DEFAULT;
	}

	public ContributorResult executeDoOnAddress(XDIAddress[] contributorAddresses, XDIAddress contributorsAddress, XDIAddress relativeTargetAddress, DoOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		return ContributorResult.DEFAULT;
	}

	/*
	 * Operations on statements
	 */

	@Override
	public ContributorResult executeOnStatement(XDIAddress[] contributorAddresses, XDIAddress contributorsAddress, XDIStatement relativeTargetStatement, Operation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		// execute on statement

		if (operation instanceof GetOperation)
			return this.executeGetOnStatement(contributorAddresses, contributorsAddress, relativeTargetStatement, (GetOperation) operation, operationResultGraph, executionContext);
		else if (operation instanceof SetOperation)
			return this.executeSetOnStatement(contributorAddresses, contributorsAddress, relativeTargetStatement, (SetOperation) operation, operationResultGraph, executionContext);
		else if (operation instanceof DelOperation)
			return this.executeDelOnStatement(contributorAddresses, contributorsAddress, relativeTargetStatement, (DelOperation) operation, operationResultGraph, executionContext);
		else if (operation instanceof DoOperation)
			return this.executeDoOnStatement(contributorAddresses, contributorsAddress, relativeTargetStatement, (DoOperation) operation, operationResultGraph, executionContext);
		else
			throw new Xdi2MessagingException("Unknown operation: " + operation.getOperationXDIAddress(), null, executionContext);
	}

	public ContributorResult executeGetOnStatement(XDIAddress[] contributorAddresses, XDIAddress contributorsAddress, XDIStatement relativeTargetStatement, GetOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (relativeTargetStatement.isContextNodeStatement())
			return this.executeGetOnContextNodeStatement(contributorAddresses, contributorsAddress, relativeTargetStatement, operation, operationResultGraph, executionContext);
		else if (relativeTargetStatement.isRelationStatement())
			return this.executeGetOnRelationStatement(contributorAddresses, contributorsAddress, relativeTargetStatement, operation, operationResultGraph, executionContext);
		else if (relativeTargetStatement.isLiteralStatement())
			return this.executeGetOnLiteralStatement(contributorAddresses, contributorsAddress, relativeTargetStatement, operation, operationResultGraph, executionContext);
		else
			throw new Xdi2MessagingException("Invalid statement: " + relativeTargetStatement, null, executionContext);
	}

	public ContributorResult executeSetOnStatement(XDIAddress[] contributorAddresses, XDIAddress contributorsAddress, XDIStatement relativeTargetStatement, SetOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (relativeTargetStatement.isContextNodeStatement())
			return this.executeSetOnContextNodeStatement(contributorAddresses, contributorsAddress, relativeTargetStatement, operation, operationResultGraph, executionContext);
		else if (relativeTargetStatement.isRelationStatement())
			return this.executeSetOnRelationStatement(contributorAddresses, contributorsAddress, relativeTargetStatement, operation, operationResultGraph, executionContext);
		else if (relativeTargetStatement.isLiteralStatement())
			return this.executeSetOnLiteralStatement(contributorAddresses, contributorsAddress, relativeTargetStatement, operation, operationResultGraph, executionContext);
		else
			throw new Xdi2MessagingException("Invalid statement: " + relativeTargetStatement, null, executionContext);
	}

	public ContributorResult executeDelOnStatement(XDIAddress[] contributorAddresses, XDIAddress contributorsAddress, XDIStatement relativeTargetStatement, DelOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (relativeTargetStatement.isContextNodeStatement())
			return this.executeDelOnContextNodeStatement(contributorAddresses, contributorsAddress, relativeTargetStatement, operation, operationResultGraph, executionContext);
		else if (relativeTargetStatement.isRelationStatement())
			return this.executeDelOnRelationStatement(contributorAddresses, contributorsAddress, relativeTargetStatement, operation, operationResultGraph, executionContext);
		else if (relativeTargetStatement.isLiteralStatement())
			return this.executeDelOnLiteralStatement(contributorAddresses, contributorsAddress, relativeTargetStatement, operation, operationResultGraph, executionContext);
		else
			throw new Xdi2MessagingException("Invalid statement: " + relativeTargetStatement, null, executionContext);
	}

	public ContributorResult executeDoOnStatement(XDIAddress[] contributorAddresses, XDIAddress contributorsAddress, XDIStatement relativeTargetStatement, DoOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (relativeTargetStatement.isContextNodeStatement())
			return this.executeDoOnContextNodeStatement(contributorAddresses, contributorsAddress, relativeTargetStatement, operation, operationResultGraph, executionContext);
		else if (relativeTargetStatement.isRelationStatement())
			return this.executeDoOnRelationStatement(contributorAddresses, contributorsAddress, relativeTargetStatement, operation, operationResultGraph, executionContext);
		else if (relativeTargetStatement.isLiteralStatement())
			return this.executeDoOnLiteralStatement(contributorAddresses, contributorsAddress, relativeTargetStatement, operation, operationResultGraph, executionContext);
		else
			throw new Xdi2MessagingException("Invalid statement: " + relativeTargetStatement, null, executionContext);
	}

	/*
	 * Operations on context node statements
	 */

	public ContributorResult executeGetOnContextNodeStatement(XDIAddress[] contributorAddresses, XDIAddress contributorsAddress, XDIStatement relativeTargetStatement, GetOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDIAddress relativeTargetAddress = relativeTargetStatement.getContextNodeXDIAddress();
		XDIStatement targetStatement = XDIStatementUtil.concatXDIStatement(contributorsAddress, relativeTargetStatement);

		Graph tempResultGraph = MemoryGraphFactory.getInstance().openGraph();

		ContributorResult contributorResult = this.executeGetOnAddress(contributorAddresses, contributorsAddress, relativeTargetAddress, operation, tempResultGraph, executionContext);

		new GraphContextHandler(tempResultGraph).executeGetOnContextNodeStatement(targetStatement, operation, operationResultGraph, executionContext);

		return contributorResult;
	}

	public ContributorResult executeSetOnContextNodeStatement(XDIAddress[] contributorAddresses, XDIAddress contributorsAddress, XDIStatement relativeTargetStatement, SetOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDIAddress relativeTargetAddress = XDIAddressUtil.concatXDIAddresses(relativeTargetStatement.getContextNodeXDIAddress(), relativeTargetStatement.getContextNodeXDIArc());

		return this.executeSetOnAddress(contributorAddresses, contributorsAddress, relativeTargetAddress, operation, operationResultGraph, executionContext);
	}

	public ContributorResult executeDelOnContextNodeStatement(XDIAddress[] contributorAddresses, XDIAddress contributorsAddress, XDIStatement relativeTargetStatement, DelOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDIAddress relativeTargetAddress = XDIAddressUtil.concatXDIAddresses(relativeTargetStatement.getContextNodeXDIAddress(), relativeTargetStatement.getContextNodeXDIArc());

		return this.executeDelOnAddress(contributorAddresses, contributorsAddress, relativeTargetAddress, operation, operationResultGraph, executionContext);
	}

	public ContributorResult executeDoOnContextNodeStatement(XDIAddress[] contributorAddresses, XDIAddress contributorsAddress, XDIStatement relativeTargetStatement, DoOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		return ContributorResult.DEFAULT;
	}

	/*
	 * Operations on relation statements
	 */

	public ContributorResult executeGetOnRelationStatement(XDIAddress[] contributorAddresses, XDIAddress contributorsAddress, XDIStatement relativeTargetStatement, GetOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDIAddress relativeTargetAddress = relativeTargetStatement.getContextNodeXDIAddress();
		XDIStatement targetStatement = XDIStatementUtil.concatXDIStatement(contributorsAddress, relativeTargetStatement);

		Graph tempResultGraph = MemoryGraphFactory.getInstance().openGraph();

		ContributorResult contributorResult = this.executeGetOnAddress(contributorAddresses, contributorsAddress, relativeTargetAddress, operation, tempResultGraph, executionContext);

		new GraphContextHandler(tempResultGraph).executeGetOnRelationStatement(targetStatement, operation, operationResultGraph, executionContext);

		return contributorResult;
	}

	public ContributorResult executeSetOnRelationStatement(XDIAddress[] contributorAddresses, XDIAddress contributorsAddress, XDIStatement relativeTargetStatement, SetOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		return ContributorResult.DEFAULT;
	}

	public ContributorResult executeDelOnRelationStatement(XDIAddress[] contributorAddresses, XDIAddress contributorsAddress, XDIStatement relativeTargetStatement, DelOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		return ContributorResult.DEFAULT;
	}

	public ContributorResult executeDoOnRelationStatement(XDIAddress[] contributorAddresses, XDIAddress contributorsAddress, XDIStatement relativeTargetStatement, DoOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		return ContributorResult.DEFAULT;
	}

	/*
	 * Operations on literal statements
	 */

	public ContributorResult executeGetOnLiteralStatement(XDIAddress[] contributorAddresses, XDIAddress contributorsAddress, XDIStatement relativeTargetStatement, GetOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDIAddress relativeTargetAddress = relativeTargetStatement.getContextNodeXDIAddress();
		XDIStatement targetStatement = XDIStatementUtil.concatXDIStatement(contributorsAddress, relativeTargetStatement);

		Graph tempResultGraph = MemoryGraphFactory.getInstance().openGraph();

		ContributorResult contributorResult = this.executeGetOnAddress(contributorAddresses, contributorsAddress, relativeTargetAddress, operation, tempResultGraph, executionContext);

		new GraphContextHandler(tempResultGraph).executeGetOnLiteralStatement(targetStatement, operation, operationResultGraph, executionContext);

		return contributorResult;
	}

	public ContributorResult executeSetOnLiteralStatement(XDIAddress[] contributorAddresses, XDIAddress contributorsAddress, XDIStatement relativeTargetStatement, SetOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		return ContributorResult.DEFAULT;
	}

	public ContributorResult executeDelOnLiteralStatement(XDIAddress[] contributorAddresses, XDIAddress contributorsAddress, XDIStatement relativeTargetStatement, DelOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		return ContributorResult.DEFAULT;
	}

	public ContributorResult executeDoOnLiteralStatement(XDIAddress[] contributorAddresses, XDIAddress contributorsAddress, XDIStatement relativeTargetStatement, DoOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		return ContributorResult.DEFAULT;
	}

	/*
	 * Contributor mount
	 */

	@Override
	public ContributorMount getContributorMount() {

		ContributorMount contributorMount = this.getClass().getAnnotation(ContributorMount.class);
		if (contributorMount == null) return null;

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
