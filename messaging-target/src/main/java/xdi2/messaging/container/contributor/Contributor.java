package xdi2.messaging.container.contributor;

import xdi2.core.Graph;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIStatement;
import xdi2.messaging.container.Extension;
import xdi2.messaging.container.MessagingContainer;
import xdi2.messaging.container.exceptions.Xdi2MessagingException;
import xdi2.messaging.container.execution.ExecutionContext;
import xdi2.messaging.operations.Operation;

public interface Contributor extends Extension<MessagingContainer> {

	/*
	 * Contributor mount
	 */

	public ContributorMount getContributorMount();

	/*
	 * Sub-contributors
	 */

	public ContributorMap getContributors();
	public void setContributors(ContributorMap contributors);

	/*
	 * Contributor methods
	 */

	/**
	 * Executes an XDI operation on an address.
	 * @param contributorChainXDIAddresses The individual addresses in the contributor chain.
	 * @param contributorChainXDIAddress The complete address of the contributor chain.
	 * @param relativeTargetXDIAddress The relative target address.
	 * @param operation The operation that is being executed.
	 * @param operationResultGraph The operation's message result.
	 * @param executionContext An "execution context" object for the entire XDI message envelope.
	 * @return True, if the operation has been fully handled and the server should stop processing it.
	 */
	public ContributorResult executeOnAddress(XDIAddress[] contributorXDIAddresses, XDIAddress contributorsXDIAddress, XDIAddress relativeTargetXDIAddress, Operation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException;

	/**
	 * Executes an XDI operation on a statement.
	 * @param contributorChainXDIAddresses The individual addresses in the contributor chain.
	 * @param contributorChainXDIAddress The complete address of the contributor chain.
	 * @param relativeTargetXDIStatement The relative target statement.
	 * @param operation The operation that is being executed.
	 * @param operationResultGraph The operation's message result.
	 * @param executionContext An "execution context" object for the entire XDI message envelope.
	 * @return True, if the operation has been fully handled and the server should stop processing it.
	 */
	public ContributorResult executeOnStatement(XDIAddress[] contributorChainXDIAddresses, XDIAddress contributorChainXDIAddress, XDIStatement relativeTargetXDIStatement, Operation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException;
}
