package xdi2.messaging.target.contributor.impl.xdi.manipulator.impl.signature;

import xdi2.core.features.signatures.Signature;
import xdi2.messaging.Message;
import xdi2.messaging.target.MessagingTarget;

/**
 * The purpose of this interface is to create a signature on an XDI message.
 * This is used by the SignatureMessageEnvelopeManipulator.
 */
public interface SignatureCreator {

	/*
	 * Init and shutdown
	 */

	public void init(MessagingTarget messagingTarget, SignatureMessageEnvelopeManipulator signatureMessageEnvelopeManipulator) throws Exception;
	public void shutdown(MessagingTarget messagingTarget, SignatureMessageEnvelopeManipulator signatureMessageEnvelopeManipulator) throws Exception;

	/**
	 * Creates a signature on an XDI message.
	 */
	public void createSignature(Message message, Signature<?, ?> signature);
}
