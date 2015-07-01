package xdi2.messaging.target.contributor.impl.proxy.manipulator.impl.signing;

import xdi2.core.features.signatures.Signature;
import xdi2.messaging.Message;
import xdi2.messaging.target.MessagingTarget;

/**
 * The purpose of this interface is to create a signature on an XDI message.
 * This is used by the SigningMessageEnvelopeManipulator.
 */
public interface Signer {

	/*
	 * Init and shutdown
	 */

	public void init(MessagingTarget messagingTarget, SigningProxyManipulator signatureMessageEnvelopeManipulator) throws Exception;
	public void shutdown(MessagingTarget messagingTarget, SigningProxyManipulator signatureMessageEnvelopeManipulator) throws Exception;

	/**
	 * Sign an XDI message.
	 */
	public Signature<?, ?> sign(Message message);
}
