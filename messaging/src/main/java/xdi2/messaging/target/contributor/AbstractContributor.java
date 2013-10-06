package xdi2.messaging.target.contributor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import xdi2.core.util.StatementUtil;
import xdi2.core.util.XDI3Util;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;
import xdi2.messaging.DelOperation;
import xdi2.messaging.DoOperation;
import xdi2.messaging.GetOperation;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import xdi2.messaging.SetOperation;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.ExecutionContext;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.impl.graph.GraphContextHandler;

public abstract class AbstractContributor implements Contributor {

	private boolean enabled;
	private ContributorMap contributors;

	public AbstractContributor() {

		this.enabled = true;
		this.contributors = new ContributorMap();
	}

	/*
	 * Init and shutdown
	 */

	@Override
	public void init(MessagingTarget messagingTarget) throws Exception {

	}

	@Override
	public void shutdown(MessagingTarget messagingTarget) throws Exception {

	}

	/*
	 * Operations on addresses
	 */

	@Override
	public boolean executeOnAddress(XDI3Segment[] contributorXris, XDI3Segment contributorsXri, XDI3Segment relativeTargetAddress, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// execute on address

		if (operation instanceof GetOperation)
			return this.executeGetOnAddress(contributorXris, contributorsXri, relativeTargetAddress, (GetOperation) operation, messageResult, executionContext);
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

		return false;
	}

	public boolean executeSetOnAddress(XDI3Segment[] contributorXris, XDI3Segment contributorsXri, XDI3Segment relativeTargetAddress, SetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean executeDelOnAddress(XDI3Segment[] contributorXris, XDI3Segment contributorsXri, XDI3Segment relativeTargetAddress, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean executeDoOnAddress(XDI3Segment[] contributorXris, XDI3Segment contributorsXri, XDI3Segment relativeTargetAddress, DoOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	/*
	 * Operations on statements
	 */

	@Override
	public boolean executeOnStatement(XDI3Segment[] contributorXris, XDI3Segment contributorsXri, XDI3Statement relativeTargetStatement, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// execute on statement

		if (operation instanceof GetOperation)
			return this.executeGetOnStatement(contributorXris, contributorsXri, relativeTargetStatement, (GetOperation) operation, messageResult, executionContext);
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

		XDI3Segment relativeTargetAddress = relativeTargetStatement.getContextNodeXri();
		XDI3Statement targetStatement = StatementUtil.concatXriStatement(contributorsXri, relativeTargetStatement, false);

		MessageResult tempMessageResult = new MessageResult();

		boolean handled = this.executeGetOnAddress(contributorXris, contributorsXri, relativeTargetAddress, operation, tempMessageResult, executionContext);

		new GraphContextHandler(tempMessageResult.getGraph()).executeGetOnContextNodeStatement(targetStatement, operation, messageResult, executionContext);

		return handled;
	}

	public boolean executeSetOnContextNodeStatement(XDI3Segment[] contributorXris, XDI3Segment contributorsXri, XDI3Statement relativeTargetStatement, SetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment relativeTargetAddress = XDI3Util.concatXris(relativeTargetStatement.getContextNodeXri(), relativeTargetStatement.getContextNodeArcXri());

		return this.executeSetOnAddress(contributorXris, contributorsXri, relativeTargetAddress, operation, messageResult, executionContext);
	}

	public boolean executeDelOnContextNodeStatement(XDI3Segment[] contributorXris, XDI3Segment contributorsXri, XDI3Statement relativeTargetStatement, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment relativeTargetAddress = XDI3Util.concatXris(relativeTargetStatement.getContextNodeXri(), relativeTargetStatement.getContextNodeArcXri());

		return this.executeDelOnAddress(contributorXris, contributorsXri, relativeTargetAddress, operation, messageResult, executionContext);
	}

	public boolean executeDoOnContextNodeStatement(XDI3Segment[] contributorXris, XDI3Segment contributorsXri, XDI3Statement relativeTargetStatement, DoOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	/*
	 * Operations on relation statements
	 */

	public boolean executeGetOnRelationStatement(XDI3Segment[] contributorXris, XDI3Segment contributorsXri, XDI3Statement relativeTargetStatement, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment relativeTargetAddress = relativeTargetStatement.getContextNodeXri();
		XDI3Statement targetStatement = StatementUtil.concatXriStatement(contributorsXri, relativeTargetStatement, false);

		MessageResult tempMessageResult = new MessageResult();

		boolean handled = this.executeGetOnAddress(contributorXris, contributorsXri, relativeTargetAddress, operation, tempMessageResult, executionContext);

		new GraphContextHandler(tempMessageResult.getGraph()).executeGetOnRelationStatement(targetStatement, operation, messageResult, executionContext);

		return handled;
	}

	public boolean executeSetOnRelationStatement(XDI3Segment[] contributorXris, XDI3Segment contributorsXri, XDI3Statement relativeTargetStatement, SetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean executeDelOnRelationStatement(XDI3Segment[] contributorXris, XDI3Segment contributorsXri, XDI3Statement relativeTargetStatement, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean executeDoOnRelationStatement(XDI3Segment[] contributorXris, XDI3Segment contributorsXri, XDI3Statement relativeTargetStatement, DoOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	/*
	 * Operations on literal statements
	 */

	public boolean executeGetOnLiteralStatement(XDI3Segment[] contributorXris, XDI3Segment contributorsXri, XDI3Statement relativeTargetStatement, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment relativeTargetAddress = relativeTargetStatement.getContextNodeXri();
		XDI3Statement targetStatement = StatementUtil.concatXriStatement(contributorsXri, relativeTargetStatement, false);

		MessageResult tempMessageResult = new MessageResult();

		boolean handled = this.executeGetOnAddress(contributorXris, contributorsXri, relativeTargetAddress, operation, tempMessageResult, executionContext);

		new GraphContextHandler(tempMessageResult.getGraph()).executeGetOnLiteralStatement(targetStatement, operation, messageResult, executionContext);

		return handled;
	}

	public boolean executeSetOnLiteralStatement(XDI3Segment[] contributorXris, XDI3Segment contributorsXri, XDI3Statement relativeTargetStatement, SetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean executeDelOnLiteralStatement(XDI3Segment[] contributorXris, XDI3Segment contributorsXri, XDI3Statement relativeTargetStatement, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean executeDoOnLiteralStatement(XDI3Segment[] contributorXris, XDI3Segment contributorsXri, XDI3Statement relativeTargetStatement, DoOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	/*
	 * Contributor addresses
	 */

	@Override
	public String[] getAddresses() {

		ContributorXri contributorXri = this.getClass().getAnnotation(ContributorXri.class);
		if (contributorXri == null) throw new NullPointerException("No ContributorXri annotation on contributor " + this.getClass().getSimpleName());

		return contributorXri.addresses();
	}

	@Override
	public boolean containsAddress(String address) {

		return Arrays.asList(this.getAddresses()).contains(address);
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
