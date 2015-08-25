package xdi2.messaging.target;

import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.target.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.execution.ExecutionContext;
import xdi2.messaging.target.execution.ExecutionResult;

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
	 * @param messageEnvelope The XDI message envelope to be executed.
	 * @param executionContext An "execution context" object that carries state between
	 * messaging targets, interceptors and contributors.
	 * @param executionResult The execution result produced by executing the messaging request.
	 */
	public void execute(MessageEnvelope messageEnvelope, ExecutionContext executionContext, ExecutionResult executionResult) throws Xdi2MessagingException;

	/**
	 * Returns the owner peer root XDI arc of the messaging target.
	 * This may be null.
	 */
	public XDIArc getOwnerPeerRootXDIArc();

	/**
	 * Returns the owner XDI address of the messaging target.
	 * This may be null.
	 */
	public XDIAddress getOwnerXDIAddress();
}
