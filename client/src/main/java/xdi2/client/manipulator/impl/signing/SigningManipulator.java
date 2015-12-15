package xdi2.client.manipulator.impl.signing;

import java.security.GeneralSecurityException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.client.manipulator.MessageManipulator;
import xdi2.client.manipulator.impl.AbstractMessageManipulator;
import xdi2.core.Graph;
import xdi2.core.features.signatures.Signature;
import xdi2.core.security.signature.create.SignatureCreator;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.util.GraphAware;
import xdi2.core.util.iterators.ReadOnlyIterator;
import xdi2.messaging.Message;

public class SigningManipulator extends AbstractMessageManipulator implements MessageManipulator, GraphAware {

	private static Logger log = LoggerFactory.getLogger(SigningManipulator.class.getName());

	private SignatureCreator<? extends Signature> signatureCreator;

	/*
	 * GraphAware
	 */

	@Override
	public void setGraph(Graph graph) {

		if (this.getSignatureCreator() instanceof GraphAware) ((GraphAware) this.getSignatureCreator()).setGraph(graph);
	}

	/*
	 * MessageManipulator
	 */

	@Override
	public void manipulate(Message message) throws Xdi2ClientException {

		// check if the message already has a signature

		ReadOnlyIterator<Signature> signatures = message.getSignatures();

		if (signatures.hasNext()) {

			if (log.isWarnEnabled()) log.warn("Message " + message + " already has signature " + signatures.next());

			return;
		}

		// sign the message

		XDIAddress signerXDIAddress = message.getSenderXDIAddress();

		Signature signature;

		try {

			signature = this.getSignatureCreator().createSignature(message.getContextNode(), signerXDIAddress);
		} catch (GeneralSecurityException ex) {

			throw new Xdi2ClientException("Could not create signature for message " + message + " via " + this.getSignatureCreator().getClass().getSimpleName() + ": " + ex.getMessage(), ex);
		}

		if (log.isDebugEnabled()) log.debug("Created signature " + signature + " for message " + message + " via " + this.getSignatureCreator().getClass().getSimpleName());
	}

	/*
	 * Getters and setters
	 */

	public SignatureCreator<? extends Signature> getSignatureCreator() {

		return this.signatureCreator;
	}

	public void setSignatureCreator(SignatureCreator<? extends Signature> signatureCreator) {

		this.signatureCreator = signatureCreator;
	}
}
