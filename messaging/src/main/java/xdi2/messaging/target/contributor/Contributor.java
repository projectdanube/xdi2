package xdi2.messaging.target.contributor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.ExecutionContext;
import xdi2.messaging.target.MessagingTarget;

public interface Contributor {

	/*
	 * Init and shutdown
	 */

	public void init(MessagingTarget messagingTarget) throws Exception;
	public void shutdown(MessagingTarget messagingTarget) throws Exception;

	/*
	 * Contributor addresses
	 */

	public String[] getAddresses();
	public boolean containsAddress(String address);

	/*
	 * Enabled?
	 */

	public boolean isEnabled();
	public void setEnabled(boolean enabled);

	/*
	 * Sub-contributors
	 */

	public ContributorMap getContributors();
	public void setContributors(ContributorMap contributors);
	public void setContributors(Map<XDI3Segment, List<Contributor>> contributors);
	public void setContributorsList(ArrayList<Contributor> contributors);

	/*
	 * Contributor methods
	 */
	
	/**
	 * Executes an XDI operation on an address.
	 * @param contributorChainXris The individual XRIs in the contributor chain.
	 * @param contributorChainXri The complete XRI of the contributor chain.
	 * @param relativeTargetAddress The relative target address.
	 * @param operation The operation that is being executed.
	 * @param operationMessageResult The operation's message result.
	 * @param executionContext An "execution context" object for the entire XDI message envelope.
	 * @return True, if the operation has been fully handled and the server should stop processing it.
	 */
	public boolean executeOnAddress(XDI3Segment[] contributorXris, XDI3Segment contributorsXri, XDI3Segment relativeTargetAddress, Operation operation, MessageResult operationMessageResult, ExecutionContext executionContext) throws Xdi2MessagingException;

	/**
	 * Executes an XDI operation on a statement.
	 * @param contributorChainXris The individual XRIs in the contributor chain.
	 * @param contributorChainXri The complete XRI of the contributor chain.
	 * @param relativeTargetStatement The relative target statement.
	 * @param operation The operation that is being executed.
	 * @param operationMessageResult The operation's message result.
	 * @param executionContext An "execution context" object for the entire XDI message envelope.
	 * @return True, if the operation has been fully handled and the server should stop processing it.
	 */
	public boolean executeOnStatement(XDI3Segment[] contributorChainXris, XDI3Segment contributorChainXri, XDI3Statement relativeTargetStatement, Operation operation, MessageResult operationMessageResult, ExecutionContext executionContext) throws Xdi2MessagingException;
}
