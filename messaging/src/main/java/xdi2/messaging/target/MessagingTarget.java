package xdi2.messaging.target;

import xdi2.core.Graph;
import xdi2.core.syntax.XDIArc;
import xdi2.messaging.context.ExecutionContext;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.request.MessagingRequest;

/**
 * A MessagingTarget can process XDI messages and produce a result graph.
 *
 * @author markus
 */
public interface MessagingTarget {

	/**
	 * This method gets called when the messaging target is initialized.
	 */
	public void init() throws Exception;

	/**
	 * This method gets called when the messaging target is no longer needed.
	 */
	public void shutdown() throws Exception;

	/**
	 * Executes a messaging request against this messaging target.
	 * @param messagingRequest The XDI messaging request to be executed.
	 * @param executionContext An "execution context" object that carries state between
	 * messaging targets, interceptors and contributors.
	 * @return The result graph produced by executing the messaging request.
	 */
	public Graph execute(MessagingRequest messagingRequest, ExecutionContext executionContext) throws Xdi2MessagingException;

	/**
	 * Executes a messaging request against this messaging target.
	 * @param messagingRequest The XDI messaging request to be executed.
	 * @return The result produced by executing the messaging request.
	 */
	public Graph execute(MessagingRequest messagingRequest) throws Xdi2MessagingException;

	/**
	 * Returns the owner peer root XRI of the messaging target.
	 * This may be null.
	 */
	public XDIArc getOwnerPeerRootXDIArc();
}
