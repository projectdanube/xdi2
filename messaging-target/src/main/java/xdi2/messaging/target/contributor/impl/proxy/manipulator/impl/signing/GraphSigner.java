package xdi2.messaging.target.contributor.impl.proxy.manipulator.impl.signing;

import java.security.GeneralSecurityException;
import java.security.PrivateKey;

import xdi2.core.Graph;
import xdi2.core.features.keys.Keys;
import xdi2.core.syntax.XDIAddress;
import xdi2.messaging.Message;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.impl.graph.GraphMessagingTarget;

/**
 * A Signer that can create signatures on an XDI message using a "private key graph",
 * which contains sender addresses and private keys.
 */
public class GraphSigner extends PrivateKeySigner {

	private Graph privateKeyGraph;

	public GraphSigner(Graph privateKeyGraph) {

		super();

		this.privateKeyGraph = privateKeyGraph;
	}

	public GraphSigner() {

		super();

		this.privateKeyGraph = null;
	}

	@Override
	public void init(MessagingTarget messagingTarget, SigningProxyManipulator signatureMessageEnvelopeManipulator) throws Exception {

		super.init(messagingTarget, signatureMessageEnvelopeManipulator);

		if (this.getPrivateKeyGraph() == null && messagingTarget instanceof GraphMessagingTarget) this.setPrivateKeyGraph(((GraphMessagingTarget) messagingTarget).getGraph());
		if (this.getPrivateKeyGraph() == null) throw new Xdi2MessagingException("No private key graph.", null, null);
	}

	@Override
	public PrivateKey getPrivateKey(Message message) throws GeneralSecurityException {

		XDIAddress senderXDIAddress = message.getSenderXDIAddress();
		if (senderXDIAddress == null) return null;

		// find private key

		PrivateKey privateKey = Keys.getSignaturePrivateKey(this.getPrivateKeyGraph(), senderXDIAddress);

		// done

		return privateKey;
	}

	/*
	 * Getters and setters
	 */

	public Graph getPrivateKeyGraph() {
	
		return this.privateKeyGraph;
	}

	public void setPrivateKeyGraph(Graph publicKeyGraph) {
	
		this.privateKeyGraph = publicKeyGraph;
	}
}
