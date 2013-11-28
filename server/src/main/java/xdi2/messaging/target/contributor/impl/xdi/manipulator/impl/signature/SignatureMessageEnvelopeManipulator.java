package xdi2.messaging.target.contributor.impl.xdi.manipulator.impl.signature;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.features.signatures.Signature;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.ExecutionContext;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.contributor.impl.xdi.manipulator.AbstractMessageEnvelopeManipulator;
import xdi2.messaging.target.contributor.impl.xdi.manipulator.MessageEnvelopeManipulator;

public class SignatureMessageEnvelopeManipulator extends AbstractMessageEnvelopeManipulator implements MessageEnvelopeManipulator {

	private static Logger log = LoggerFactory.getLogger(SignatureMessageEnvelopeManipulator.class.getName());

	private SignatureCreator signatureCreator;

	/*
	 * Init and shutdown
	 */

	@Override
	public void init(MessagingTarget messagingTarget) throws Exception {

		super.init(messagingTarget);

		this.getSignatureCreator().init(messagingTarget, this);
	}

	@Override
	public void shutdown(MessagingTarget messagingTarget) throws Exception {

		super.shutdown(messagingTarget);

		this.getSignatureCreator().shutdown(messagingTarget, this);
	}

	/*
	 * MessageEnvelopeManipulator
	 */

	@Override
	public void manipulate(MessageEnvelope messageEnvelope, ExecutionContext executionContext) throws Xdi2MessagingException {

		for (Message message : messageEnvelope.getMessages()) {

			// look for signature on the message

			Signature<?, ?> signature = message.setSignature(null, 0, null, 0);

			// create signature

			if (log.isDebugEnabled()) log.debug("Creating signature via " + this.getSignatureCreator().getClass().getSimpleName());

			this.getSignatureCreator().createSignature(message, signature);
		}
	}

	/*
	 * Getters and setters
	 */

	public SignatureCreator getSignatureCreator() {

		return this.signatureCreator;
	}

	public void setSignatureCreator(SignatureCreator signatureCreator) {

		this.signatureCreator = signatureCreator;
	}
}