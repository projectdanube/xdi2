package xdi2.messaging.container.contributor.impl;

import xdi2.core.Graph;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIStatement;
import xdi2.core.util.XDIAddressUtil;
import xdi2.core.util.XDIStatementUtil;
import xdi2.messaging.container.MessagingContainer;
import xdi2.messaging.container.contributor.Contributor;
import xdi2.messaging.container.contributor.ContributorMap;
import xdi2.messaging.container.contributor.ContributorMount;
import xdi2.messaging.container.contributor.ContributorResult;
import xdi2.messaging.container.exceptions.Xdi2MessagingException;
import xdi2.messaging.container.execution.ExecutionContext;
import xdi2.messaging.container.impl.AbstractExtension;
import xdi2.messaging.container.impl.graph.GraphContextHandler;
import xdi2.messaging.operations.ConnectOperation;
import xdi2.messaging.operations.DelOperation;
import xdi2.messaging.operations.DoOperation;
import xdi2.messaging.operations.GetOperation;
import xdi2.messaging.operations.Operation;
import xdi2.messaging.operations.PushOperation;
import xdi2.messaging.operations.SendOperation;
import xdi2.messaging.operations.SetOperation;

public abstract class AbstractContributor extends AbstractExtension<MessagingContainer> implements Contributor {

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
	public ContributorResult executeOnAddress(XDIAddress[] contributorXDIAddresses, XDIAddress contributorsXDIAddress, XDIAddress relativeTargetXDIAddress, Operation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		// execute on address

		if (operation instanceof GetOperation)
			return this.executeGetOnAddress(contributorXDIAddresses, contributorsXDIAddress, relativeTargetXDIAddress, (GetOperation) operation, operationResultGraph, executionContext);
		else if (operation instanceof SetOperation)
			return this.executeSetOnAddress(contributorXDIAddresses, contributorsXDIAddress, relativeTargetXDIAddress, (SetOperation) operation, operationResultGraph, executionContext);
		else if (operation instanceof DelOperation)
			return this.executeDelOnAddress(contributorXDIAddresses, contributorsXDIAddress, relativeTargetXDIAddress, (DelOperation) operation, operationResultGraph, executionContext);
		else if (operation instanceof DoOperation)
			return this.executeDoOnAddress(contributorXDIAddresses, contributorsXDIAddress, relativeTargetXDIAddress, (DoOperation) operation, operationResultGraph, executionContext);
		else if (operation instanceof ConnectOperation)
			return this.executeConnectOnAddress(contributorXDIAddresses, contributorsXDIAddress, relativeTargetXDIAddress, (ConnectOperation) operation, operationResultGraph, executionContext);
		else if (operation instanceof SendOperation)
			return this.executeSendOnAddress(contributorXDIAddresses, contributorsXDIAddress, relativeTargetXDIAddress, (SendOperation) operation, operationResultGraph, executionContext);
		else if (operation instanceof PushOperation)
			return this.executePushOnAddress(contributorXDIAddresses, contributorsXDIAddress, relativeTargetXDIAddress, (PushOperation) operation, operationResultGraph, executionContext);
		else
			throw new Xdi2MessagingException("Unknown operation: " + operation.getOperationXDIAddress(), null, executionContext);
	}

	public ContributorResult executeGetOnAddress(XDIAddress[] contributorXDIAddresses, XDIAddress contributorsXDIAddress, XDIAddress relativeTargetXDIAddress, GetOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		return ContributorResult.DEFAULT;
	}

	public ContributorResult executeSetOnAddress(XDIAddress[] contributorXDIAddresses, XDIAddress contributorsXDIAddress, XDIAddress relativeTargetXDIAddress, SetOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		return ContributorResult.DEFAULT;
	}

	public ContributorResult executeDelOnAddress(XDIAddress[] contributorXDIAddresses, XDIAddress contributorsXDIAddress, XDIAddress relativeTargetXDIAddress, DelOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		return ContributorResult.DEFAULT;
	}

	public ContributorResult executeDoOnAddress(XDIAddress[] contributorXDIAddresses, XDIAddress contributorsXDIAddress, XDIAddress relativeTargetXDIAddress, DoOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		return ContributorResult.DEFAULT;
	}

	public ContributorResult executeConnectOnAddress(XDIAddress[] contributorXDIAddresses, XDIAddress contributorsXDIAddress, XDIAddress relativeTargetXDIAddress, ConnectOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		return ContributorResult.DEFAULT;
	}

	public ContributorResult executeSendOnAddress(XDIAddress[] contributorXDIAddresses, XDIAddress contributorsXDIAddress, XDIAddress relativeTargetXDIAddress, SendOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		return ContributorResult.DEFAULT;
	}

	public ContributorResult executePushOnAddress(XDIAddress[] contributorXDIAddresses, XDIAddress contributorsXDIAddress, XDIAddress relativeTargetXDIAddress, PushOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		return ContributorResult.DEFAULT;
	}

	/*
	 * Operations on statements
	 */

	@Override
	public ContributorResult executeOnStatement(XDIAddress[] contributorXDIAddresses, XDIAddress contributorsXDIAddress, XDIStatement relativeTargetStatement, Operation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		// execute on statement

		if (operation instanceof GetOperation)
			return this.executeGetOnStatement(contributorXDIAddresses, contributorsXDIAddress, relativeTargetStatement, (GetOperation) operation, operationResultGraph, executionContext);
		else if (operation instanceof SetOperation)
			return this.executeSetOnStatement(contributorXDIAddresses, contributorsXDIAddress, relativeTargetStatement, (SetOperation) operation, operationResultGraph, executionContext);
		else if (operation instanceof DelOperation)
			return this.executeDelOnStatement(contributorXDIAddresses, contributorsXDIAddress, relativeTargetStatement, (DelOperation) operation, operationResultGraph, executionContext);
		else if (operation instanceof DoOperation)
			return this.executeDoOnStatement(contributorXDIAddresses, contributorsXDIAddress, relativeTargetStatement, (DoOperation) operation, operationResultGraph, executionContext);
		else if (operation instanceof ConnectOperation)
			return this.executeConnectOnStatement(contributorXDIAddresses, contributorsXDIAddress, relativeTargetStatement, (ConnectOperation) operation, operationResultGraph, executionContext);
		else if (operation instanceof SendOperation)
			return this.executeSendOnStatement(contributorXDIAddresses, contributorsXDIAddress, relativeTargetStatement, (SendOperation) operation, operationResultGraph, executionContext);
		else if (operation instanceof PushOperation)
			return this.executePushOnStatement(contributorXDIAddresses, contributorsXDIAddress, relativeTargetStatement, (PushOperation) operation, operationResultGraph, executionContext);
		else
			throw new Xdi2MessagingException("Unknown operation: " + operation.getOperationXDIAddress(), null, executionContext);
	}

	public ContributorResult executeGetOnStatement(XDIAddress[] contributorXDIAddresses, XDIAddress contributorsXDIAddress, XDIStatement relativeTargetStatement, GetOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (relativeTargetStatement.isContextNodeStatement())
			return this.executeGetOnContextNodeStatement(contributorXDIAddresses, contributorsXDIAddress, relativeTargetStatement, operation, operationResultGraph, executionContext);
		else if (relativeTargetStatement.isRelationStatement())
			return this.executeGetOnRelationStatement(contributorXDIAddresses, contributorsXDIAddress, relativeTargetStatement, operation, operationResultGraph, executionContext);
		else if (relativeTargetStatement.isLiteralStatement())
			return this.executeGetOnLiteralStatement(contributorXDIAddresses, contributorsXDIAddress, relativeTargetStatement, operation, operationResultGraph, executionContext);
		else
			throw new Xdi2MessagingException("Invalid statement: " + relativeTargetStatement, null, executionContext);
	}

	public ContributorResult executeSetOnStatement(XDIAddress[] contributorXDIAddresses, XDIAddress contributorsXDIAddress, XDIStatement relativeTargetStatement, SetOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (relativeTargetStatement.isContextNodeStatement())
			return this.executeSetOnContextNodeStatement(contributorXDIAddresses, contributorsXDIAddress, relativeTargetStatement, operation, operationResultGraph, executionContext);
		else if (relativeTargetStatement.isRelationStatement())
			return this.executeSetOnRelationStatement(contributorXDIAddresses, contributorsXDIAddress, relativeTargetStatement, operation, operationResultGraph, executionContext);
		else if (relativeTargetStatement.isLiteralStatement())
			return this.executeSetOnLiteralStatement(contributorXDIAddresses, contributorsXDIAddress, relativeTargetStatement, operation, operationResultGraph, executionContext);
		else
			throw new Xdi2MessagingException("Invalid statement: " + relativeTargetStatement, null, executionContext);
	}

	public ContributorResult executeDelOnStatement(XDIAddress[] contributorXDIAddresses, XDIAddress contributorsXDIAddress, XDIStatement relativeTargetStatement, DelOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (relativeTargetStatement.isContextNodeStatement())
			return this.executeDelOnContextNodeStatement(contributorXDIAddresses, contributorsXDIAddress, relativeTargetStatement, operation, operationResultGraph, executionContext);
		else if (relativeTargetStatement.isRelationStatement())
			return this.executeDelOnRelationStatement(contributorXDIAddresses, contributorsXDIAddress, relativeTargetStatement, operation, operationResultGraph, executionContext);
		else if (relativeTargetStatement.isLiteralStatement())
			return this.executeDelOnLiteralStatement(contributorXDIAddresses, contributorsXDIAddress, relativeTargetStatement, operation, operationResultGraph, executionContext);
		else
			throw new Xdi2MessagingException("Invalid statement: " + relativeTargetStatement, null, executionContext);
	}

	public ContributorResult executeDoOnStatement(XDIAddress[] contributorXDIAddresses, XDIAddress contributorsXDIAddress, XDIStatement relativeTargetStatement, DoOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (relativeTargetStatement.isContextNodeStatement())
			return this.executeDoOnContextNodeStatement(contributorXDIAddresses, contributorsXDIAddress, relativeTargetStatement, operation, operationResultGraph, executionContext);
		else if (relativeTargetStatement.isRelationStatement())
			return this.executeDoOnRelationStatement(contributorXDIAddresses, contributorsXDIAddress, relativeTargetStatement, operation, operationResultGraph, executionContext);
		else if (relativeTargetStatement.isLiteralStatement())
			return this.executeDoOnLiteralStatement(contributorXDIAddresses, contributorsXDIAddress, relativeTargetStatement, operation, operationResultGraph, executionContext);
		else
			throw new Xdi2MessagingException("Invalid statement: " + relativeTargetStatement, null, executionContext);
	}

	public ContributorResult executeConnectOnStatement(XDIAddress[] contributorXDIAddresses, XDIAddress contributorsXDIAddress, XDIStatement relativeTargetStatement, ConnectOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (relativeTargetStatement.isContextNodeStatement())
			return this.executeConnectOnContextNodeStatement(contributorXDIAddresses, contributorsXDIAddress, relativeTargetStatement, operation, operationResultGraph, executionContext);
		else if (relativeTargetStatement.isRelationStatement())
			return this.executeConnectOnRelationStatement(contributorXDIAddresses, contributorsXDIAddress, relativeTargetStatement, operation, operationResultGraph, executionContext);
		else if (relativeTargetStatement.isLiteralStatement())
			return this.executeConnectOnLiteralStatement(contributorXDIAddresses, contributorsXDIAddress, relativeTargetStatement, operation, operationResultGraph, executionContext);
		else
			throw new Xdi2MessagingException("Invalid statement: " + relativeTargetStatement, null, executionContext);
	}

	public ContributorResult executeSendOnStatement(XDIAddress[] contributorXDIAddresses, XDIAddress contributorsXDIAddress, XDIStatement relativeTargetStatement, SendOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (relativeTargetStatement.isContextNodeStatement())
			return this.executeSendOnContextNodeStatement(contributorXDIAddresses, contributorsXDIAddress, relativeTargetStatement, operation, operationResultGraph, executionContext);
		else if (relativeTargetStatement.isRelationStatement())
			return this.executeSendOnRelationStatement(contributorXDIAddresses, contributorsXDIAddress, relativeTargetStatement, operation, operationResultGraph, executionContext);
		else if (relativeTargetStatement.isLiteralStatement())
			return this.executeSendOnLiteralStatement(contributorXDIAddresses, contributorsXDIAddress, relativeTargetStatement, operation, operationResultGraph, executionContext);
		else
			throw new Xdi2MessagingException("Invalid statement: " + relativeTargetStatement, null, executionContext);
	}

	public ContributorResult executePushOnStatement(XDIAddress[] contributorXDIAddresses, XDIAddress contributorsXDIAddress, XDIStatement relativeTargetStatement, PushOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (relativeTargetStatement.isContextNodeStatement())
			return this.executePushOnContextNodeStatement(contributorXDIAddresses, contributorsXDIAddress, relativeTargetStatement, operation, operationResultGraph, executionContext);
		else if (relativeTargetStatement.isRelationStatement())
			return this.executePushOnRelationStatement(contributorXDIAddresses, contributorsXDIAddress, relativeTargetStatement, operation, operationResultGraph, executionContext);
		else if (relativeTargetStatement.isLiteralStatement())
			return this.executePushOnLiteralStatement(contributorXDIAddresses, contributorsXDIAddress, relativeTargetStatement, operation, operationResultGraph, executionContext);
		else
			throw new Xdi2MessagingException("Invalid statement: " + relativeTargetStatement, null, executionContext);
	}

	/*
	 * Operations on context node statements
	 */

	public ContributorResult executeGetOnContextNodeStatement(XDIAddress[] contributorXDIAddresses, XDIAddress contributorsXDIAddress, XDIStatement relativeTargetStatement, GetOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDIAddress relativeTargetXDIAddress = relativeTargetStatement.getContextNodeXDIAddress();
		XDIStatement targetStatement = XDIStatementUtil.concatXDIStatement(contributorsXDIAddress, relativeTargetStatement);

		Graph tempResultGraph = MemoryGraphFactory.getInstance().openGraph();

		ContributorResult contributorResult = this.executeGetOnAddress(contributorXDIAddresses, contributorsXDIAddress, relativeTargetXDIAddress, operation, tempResultGraph, executionContext);

		new GraphContextHandler(tempResultGraph).executeGetOnContextNodeStatement(targetStatement, operation, operationResultGraph, executionContext);

		return contributorResult;
	}

	public ContributorResult executeSetOnContextNodeStatement(XDIAddress[] contributorXDIAddresses, XDIAddress contributorsXDIAddress, XDIStatement relativeTargetStatement, SetOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDIAddress relativeTargetXDIAddress = XDIAddressUtil.concatXDIAddresses(relativeTargetStatement.getContextNodeXDIAddress(), relativeTargetStatement.getContextNodeXDIArc());

		return this.executeSetOnAddress(contributorXDIAddresses, contributorsXDIAddress, relativeTargetXDIAddress, operation, operationResultGraph, executionContext);
	}

	public ContributorResult executeDelOnContextNodeStatement(XDIAddress[] contributorXDIAddresses, XDIAddress contributorsXDIAddress, XDIStatement relativeTargetStatement, DelOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDIAddress relativeTargetXDIAddress = XDIAddressUtil.concatXDIAddresses(relativeTargetStatement.getContextNodeXDIAddress(), relativeTargetStatement.getContextNodeXDIArc());

		return this.executeDelOnAddress(contributorXDIAddresses, contributorsXDIAddress, relativeTargetXDIAddress, operation, operationResultGraph, executionContext);
	}

	public ContributorResult executeDoOnContextNodeStatement(XDIAddress[] contributorXDIAddresses, XDIAddress contributorsXDIAddress, XDIStatement relativeTargetStatement, DoOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		return ContributorResult.DEFAULT;
	}

	public ContributorResult executeConnectOnContextNodeStatement(XDIAddress[] contributorXDIAddresses, XDIAddress contributorsXDIAddress, XDIStatement relativeTargetStatement, ConnectOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		return ContributorResult.DEFAULT;
	}

	public ContributorResult executeSendOnContextNodeStatement(XDIAddress[] contributorXDIAddresses, XDIAddress contributorsXDIAddress, XDIStatement relativeTargetStatement, SendOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		return ContributorResult.DEFAULT;
	}

	public ContributorResult executePushOnContextNodeStatement(XDIAddress[] contributorXDIAddresses, XDIAddress contributorsXDIAddress, XDIStatement relativeTargetStatement, PushOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		return ContributorResult.DEFAULT;
	}

	/*
	 * Operations on relation statements
	 */

	public ContributorResult executeGetOnRelationStatement(XDIAddress[] contributorXDIAddresses, XDIAddress contributorsXDIAddress, XDIStatement relativeTargetStatement, GetOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDIAddress relativeTargetXDIAddress = relativeTargetStatement.getContextNodeXDIAddress();
		XDIStatement targetStatement = XDIStatementUtil.concatXDIStatement(contributorsXDIAddress, relativeTargetStatement);

		Graph tempResultGraph = MemoryGraphFactory.getInstance().openGraph();

		ContributorResult contributorResult = this.executeGetOnAddress(contributorXDIAddresses, contributorsXDIAddress, relativeTargetXDIAddress, operation, tempResultGraph, executionContext);

		new GraphContextHandler(tempResultGraph).executeGetOnRelationStatement(targetStatement, operation, operationResultGraph, executionContext);

		return contributorResult;
	}

	public ContributorResult executeSetOnRelationStatement(XDIAddress[] contributorXDIAddresses, XDIAddress contributorsXDIAddress, XDIStatement relativeTargetStatement, SetOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		return ContributorResult.DEFAULT;
	}

	public ContributorResult executeDelOnRelationStatement(XDIAddress[] contributorXDIAddresses, XDIAddress contributorsXDIAddress, XDIStatement relativeTargetStatement, DelOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		return ContributorResult.DEFAULT;
	}

	public ContributorResult executeDoOnRelationStatement(XDIAddress[] contributorXDIAddresses, XDIAddress contributorsXDIAddress, XDIStatement relativeTargetStatement, DoOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		return ContributorResult.DEFAULT;
	}

	public ContributorResult executeConnectOnRelationStatement(XDIAddress[] contributorXDIAddresses, XDIAddress contributorsXDIAddress, XDIStatement relativeTargetStatement, ConnectOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		return ContributorResult.DEFAULT;
	}

	public ContributorResult executeSendOnRelationStatement(XDIAddress[] contributorXDIAddresses, XDIAddress contributorsXDIAddress, XDIStatement relativeTargetStatement, SendOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		return ContributorResult.DEFAULT;
	}

	public ContributorResult executePushOnRelationStatement(XDIAddress[] contributorXDIAddresses, XDIAddress contributorsXDIAddress, XDIStatement relativeTargetStatement, PushOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		return ContributorResult.DEFAULT;
	}

	/*
	 * Operations on literal statements
	 */

	public ContributorResult executeGetOnLiteralStatement(XDIAddress[] contributorXDIAddresses, XDIAddress contributorsXDIAddress, XDIStatement relativeTargetStatement, GetOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDIAddress relativeTargetXDIAddress = relativeTargetStatement.getContextNodeXDIAddress();
		XDIStatement targetStatement = XDIStatementUtil.concatXDIStatement(contributorsXDIAddress, relativeTargetStatement);

		Graph tempResultGraph = MemoryGraphFactory.getInstance().openGraph();

		ContributorResult contributorResult = this.executeGetOnAddress(contributorXDIAddresses, contributorsXDIAddress, relativeTargetXDIAddress, operation, tempResultGraph, executionContext);

		new GraphContextHandler(tempResultGraph).executeGetOnLiteralStatement(targetStatement, operation, operationResultGraph, executionContext);

		return contributorResult;
	}

	public ContributorResult executeSetOnLiteralStatement(XDIAddress[] contributorXDIAddresses, XDIAddress contributorsXDIAddress, XDIStatement relativeTargetStatement, SetOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		return ContributorResult.DEFAULT;
	}

	public ContributorResult executeDelOnLiteralStatement(XDIAddress[] contributorXDIAddresses, XDIAddress contributorsXDIAddress, XDIStatement relativeTargetStatement, DelOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		return ContributorResult.DEFAULT;
	}

	public ContributorResult executeDoOnLiteralStatement(XDIAddress[] contributorXDIAddresses, XDIAddress contributorsXDIAddress, XDIStatement relativeTargetStatement, DoOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		return ContributorResult.DEFAULT;
	}

	public ContributorResult executeConnectOnLiteralStatement(XDIAddress[] contributorXDIAddresses, XDIAddress contributorsXDIAddress, XDIStatement relativeTargetStatement, ConnectOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		return ContributorResult.DEFAULT;
	}

	public ContributorResult executeSendOnLiteralStatement(XDIAddress[] contributorXDIAddresses, XDIAddress contributorsXDIAddress, XDIStatement relativeTargetStatement, SendOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		return ContributorResult.DEFAULT;
	}

	public ContributorResult executePushOnLiteralStatement(XDIAddress[] contributorXDIAddresses, XDIAddress contributorsXDIAddress, XDIStatement relativeTargetStatement, PushOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

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
