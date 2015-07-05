package xdi2.client.manipulator.impl.signing;

import xdi2.core.features.signatures.Signature;
import xdi2.messaging.Message;

/**
 * The purpose of this interface is to create a signature on an XDI message.
 * This is used by the SigningMessageEnvelopeManipulator.
 */
public interface Signer {

	/**
	 * Sign an XDI message.
	 */
	public Signature<?, ?> sign(Message message);
}
