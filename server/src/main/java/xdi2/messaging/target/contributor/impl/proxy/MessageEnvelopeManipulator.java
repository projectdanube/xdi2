package xdi2.messaging.target.contributor.impl.proxy;

import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.ExecutionContext;

/**
 * This is used to manipulate message envelopes before they are forwarded
 * to another XDI endpoint by the ProxyContributor.
 * 
 * @author markus
 */
public interface MessageEnvelopeManipulator {

	/**
	 * Manipulate a message envelope.
	 * @param messageEnvelope The message envelope to manipulate.
	 * @param executionContext The current execution context.
	 */
	public void manipulate(MessageEnvelope messageEnvelope, ExecutionContext executionContext) throws Xdi2MessagingException;
}
