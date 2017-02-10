package xdi2.messaging.container;

import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.container.exceptions.Xdi2MessagingException;
import xdi2.messaging.container.execution.ExecutionContext;
import xdi2.messaging.container.execution.ExecutionResult;

/**
 * A MessagingContainer can process XDI messages and produce a result graph.
 *
 * @author markus
 */
public interface MessagingContainer {

	/**
	 * This method gets called when the messaging container is initialized.
	 */
	public void init() throws Exception;

	/**
	 * This method gets called when the messaging container is no longer needed.
	 */
	public void shutdown() throws Exception;

	/**
	 * Executes a messaging request against this messaging container.
	 * @param messageEnvelope The XDI message envelope to be executed.
	 * @param executionContext An "execution context" object that carries state between
	 * messaging containers, interceptors and contributors.
	 * @param executionResult The execution result produced by executing the messaging request.
	 */
	public void execute(MessageEnvelope messageEnvelope, ExecutionContext executionContext, ExecutionResult executionResult) throws Xdi2MessagingException;

	/**
	 * Returns the owner peer root XDI arc of the messaging container.
	 * This may be null.
	 */
	public XDIArc getOwnerPeerRootXDIArc();

	/**
	 * Returns the owner XDI address of the messaging container.
	 * This may be null.
	 */
	public XDIAddress getOwnerXDIAddress();

	/**
	 * Returns whether this messaging container owns
	 * @param peerRootXDIArc
	 * @return
	 */
	public boolean ownsPeerRootXDIArc(XDIArc peerRootXDIArc);
}
