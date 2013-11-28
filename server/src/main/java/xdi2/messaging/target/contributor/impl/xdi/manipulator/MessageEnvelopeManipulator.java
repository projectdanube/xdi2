package xdi2.messaging.target.contributor.impl.xdi.manipulator;

import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.ExecutionContext;
import xdi2.messaging.target.MessagingTarget;

/**
 * This is used to manipulate message envelopes before they are forwarded
 * to another XDI endpoint by the XdiContributor.
 * 
 * @author markus
 */
public interface MessageEnvelopeManipulator {

	/*
	 * Init and shutdown
	 */

	public void init(MessagingTarget messagingTarget) throws Exception;
	public void shutdown(MessagingTarget messagingTarget) throws Exception;

	/**
	 * Manipulate a message envelope.
	 * @param messageEnvelope The message envelope to manipulate.
	 * @param executionContext The current execution context.
	 */
	public void manipulate(MessageEnvelope messageEnvelope, ExecutionContext executionContext) throws Xdi2MessagingException;
}
