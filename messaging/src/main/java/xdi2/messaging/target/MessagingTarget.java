package xdi2.messaging.target;

import xdi2.core.syntax.XDIArc;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.context.ExecutionContext;
import xdi2.messaging.context.ExecutionResult;
import xdi2.messaging.exceptions.Xdi2MessagingException;

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
	 * @return The execution result produced by executing the messaging request.
	 */
	public ExecutionResult execute(MessageEnvelope messageEnvelope, ExecutionContext executionContext) throws Xdi2MessagingException;

	/**
	 * Executes a messaging request against this messaging target, using a default execution context.
	 * @param messageEnvelope The XDI message envelope to be executed.
	 * @return The execution result produced by executing the messaging request.
	 */
	public ExecutionResult execute(MessageEnvelope messageEnvelope) throws Xdi2MessagingException;

	/**
	 * Returns the owner peer root XRI of the messaging target.
	 * This may be null.
	 */
	public XDIArc getOwnerPeerRootXDIArc();
}
