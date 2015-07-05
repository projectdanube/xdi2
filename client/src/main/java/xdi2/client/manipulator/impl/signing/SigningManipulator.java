package xdi2.client.manipulator.impl.signing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.client.manipulator.MessageManipulator;
import xdi2.client.manipulator.impl.AbstractMessageManipulator;
import xdi2.core.features.signatures.Signature;
import xdi2.core.util.iterators.ReadOnlyIterator;
import xdi2.messaging.Message;

public class SigningManipulator extends AbstractMessageManipulator implements MessageManipulator {

	private static Logger log = LoggerFactory.getLogger(SigningManipulator.class.getName());

	private Signer signer;

	/*
	 * ProxyManipulator
	 */

	@Override
	public void manipulate(Message message) throws Xdi2ClientException {

		// check if the message already has a signature

		ReadOnlyIterator<Signature<?, ?>> signatures = message.getSignatures();

		if (signatures.hasNext()) {

			if (log.isWarnEnabled()) log.warn("Message " + message + " already has signature " + signatures.next());

			return;
		}

		// sign the message

		Signature<?, ?> signature = this.getSigner().sign(message);

		if (signature == null) {

			if (log.isWarnEnabled()) log.warn("Could not create signature for message " + message + " via " + this.getSigner().getClass().getSimpleName());

			return;
		}

		if (log.isDebugEnabled()) log.debug("Created signature " + signature + " for message " + message + " via " + this.getSigner().getClass().getSimpleName());
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
