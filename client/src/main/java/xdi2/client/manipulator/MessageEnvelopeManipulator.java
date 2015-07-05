package xdi2.client.manipulator;

import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.messaging.MessageEnvelope;

/**
 * This is used to manipulate message envelopes before they are sent
 * by an XDIClient
 * 
 * @author markus
 */
public interface MessageEnvelopeManipulator extends Manipulator {

	/**
	 * Manipulate a message envelope.
	 * @param messageEnvelope The message envelope to manipulate.
	 */
	public void manipulate(MessageEnvelope messageEnvelope) throws Xdi2ClientException;
}
