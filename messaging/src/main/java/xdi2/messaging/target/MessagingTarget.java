package xdi2.messaging.target;

import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;
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
	 * Returns the owner authority of the messaging target.
	 * This may be null.
	 */
	public XRI3Segment getOwnerAuthority();

	/**
	 * Executes all messages in an XDI messaging envelope against this messaging target.
	 * @param messageEnvelope The XDI message envelope containing XDI messages to be executed.
	 * @param messageResult The result produced by executing the message envelope.
	 * @param executionContext An "execution context" object that carries state between
	 * messaging targets, interceptors and contributors.
	 */
	public void execute(MessageEnvelope messageEnvelope, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException;
}
