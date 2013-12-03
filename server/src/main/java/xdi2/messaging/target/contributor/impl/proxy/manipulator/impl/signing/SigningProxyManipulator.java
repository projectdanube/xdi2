package xdi2.messaging.target.contributor.impl.proxy.manipulator.impl.signing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.features.signatures.Signature;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;
import xdi2.messaging.context.ExecutionContext;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.contributor.impl.proxy.manipulator.AbstractProxyManipulator;
import xdi2.messaging.target.contributor.impl.proxy.manipulator.ProxyManipulator;

public class SigningProxyManipulator extends AbstractProxyManipulator implements ProxyManipulator {

	private static Logger log = LoggerFactory.getLogger(SigningProxyManipulator.class.getName());

	private Signer signer;

	/*
	 * Init and shutdown
	 */

	@Override
	public void init(MessagingTarget messagingTarget) throws Exception {

		super.init(messagingTarget);

		this.getSigner().init(messagingTarget, this);
	}

	@Override
	public void shutdown(MessagingTarget messagingTarget) throws Exception {

		super.shutdown(messagingTarget);

		this.getSigner().shutdown(messagingTarget, this);
	}

	/*
	 * ProxyManipulator
	 */

	@Override
	public void manipulate(MessageEnvelope messageEnvelope, ExecutionContext executionContext) throws Xdi2MessagingException {

		for (Message message : messageEnvelope.getMessages()) {

			// check is the message already has a signature

			Signature<?, ?> signature = message.getSignature();

			if (signature != null) {

				if (log.isWarnEnabled()) log.warn("Message " + message + " already has signature " + signature);

				continue;
			}

			// sign the message

			signature = this.getSigner().sign(message);

			if (signature == null) {

				if (log.isWarnEnabled()) log.warn("Could not create signature for message " + message + " via " + this.getSigner().getClass().getSimpleName());

				continue;
			}

			if (log.isDebugEnabled()) log.debug("Created signature " + signature + " for message " + message + " via " + this.getSigner().getClass().getSimpleName());
		}
	}

	@Override
	public void manipulate(MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	/*
	 * Getters and setters
	 */

	public Signer getSigner() {

		return this.signer;
	}

	public void setSigner(Signer signer) {

		this.signer = signer;
	}
}
