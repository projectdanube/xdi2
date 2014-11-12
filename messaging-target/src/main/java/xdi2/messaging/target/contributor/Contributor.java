package xdi2.messaging.target.contributor;

import xdi2.core.Graph;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIStatement;
import xdi2.messaging.operations.Operation;
import xdi2.messaging.target.Extension;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.execution.ExecutionContext;

public interface Contributor extends Extension<MessagingTarget> {

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
	 * @param contributorChainAddresses The individual XRIs in the contributor chain.
	 * @param contributorChainAddress The complete XRI of the contributor chain.
	 * @param relativeTargetAddress The relative target address.
	 * @param operation The operation that is being executed.
	 * @param operationResultGraph The operation's message result.
	 * @param executionContext An "execution context" object for the entire XDI message envelope.
	 * @return True, if the operation has been fully handled and the server should stop processing it.
	 */
	public ContributorResult executeOnAddress(XDIAddress[] contributorAddresses, XDIAddress contributorsAddress, XDIAddress relativeTargetAddress, Operation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException;

	/**
	 * Executes an XDI operation on a statement.
	 * @param contributorChainAddresses The individual XRIs in the contributor chain.
	 * @param contributorChainAddress The complete XRI of the contributor chain.
	 * @param relativeTargetStatement The relative target statement.
	 * @param operation The operation that is being executed.
	 * @param operationResultGraph The operation's message result.
	 * @param executionContext An "execution context" object for the entire XDI message envelope.
	 * @return True, if the operation has been fully handled and the server should stop processing it.
	 */
	public ContributorResult executeOnStatement(XDIAddress[] contributorChainAddresses, XDIAddress contributorChainAddress, XDIStatement relativeTargetStatement, Operation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException;
}
