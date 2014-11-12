package xdi2.messaging.target.interceptor.impl.authentication.signature;

import java.security.GeneralSecurityException;
import java.security.PublicKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.Graph;
import xdi2.core.features.keys.Keys;
import xdi2.core.features.nodetypes.XdiCommonRoot;
import xdi2.core.features.nodetypes.XdiEntity;
import xdi2.core.syntax.XDIAddress;
import xdi2.messaging.Message;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.impl.graph.GraphMessagingTarget;

/**
 * A SignatureAuthenticator that can authenticate an XDI message using a "public key graph",
 * which contains sender addresses and public keys.
 */
public class GraphSignatureAuthenticator extends PublicKeySignatureAuthenticator {

	private static Logger log = LoggerFactory.getLogger(GraphSignatureAuthenticator.class.getName());

	private Graph publicKeyGraph;

	public GraphSignatureAuthenticator(Graph publicKeyGraph) {

		super();

		this.publicKeyGraph = publicKeyGraph;
	}

	public GraphSignatureAuthenticator() {

		super();

		this.publicKeyGraph = null;
	}

	@Override
	public void init(MessagingTarget messagingTarget, AuthenticationSignatureInterceptor authenticationSignatureInterceptor) throws Exception {

		super.init(messagingTarget, authenticationSignatureInterceptor);

		if (this.getPublicKeyGraph() == null && messagingTarget instanceof GraphMessagingTarget) this.setPublicKeyGraph(((GraphMessagingTarget) messagingTarget).getGraph());
		if (this.getPublicKeyGraph() == null) throw new Xdi2MessagingException("No public key graph.", null, null);
	}

	@Override
	public PublicKey getPublicKey(Message message) {

		// sender

		XDIAddress senderXDIAddress = message.getSenderXDIAddress();
		if (senderXDIAddress == null) return null;

		// sender entity

		XdiEntity senderXdiEntity = XdiCommonRoot.findCommonRoot(this.getPublicKeyGraph()).getXdiEntity(senderXDIAddress, false);
		senderXdiEntity = senderXdiEntity == null ? null : senderXdiEntity.dereference();

		if (log.isDebugEnabled()) log.debug("Sender entity: " + senderXdiEntity);

		if (senderXdiEntity == null) return null;

		// find signature public key

		PublicKey publicKey;

		try {

			publicKey = Keys.getSignaturePublicKey(senderXdiEntity);
		} catch (GeneralSecurityException ex) {

			if (log.isWarnEnabled()) log.warn("Invalid signature public key: " + ex.getMessage(), ex);

			return null;
		}

		// done

		return publicKey;
	}

	/*
	 * Getters and setters
	 */

	public Graph getPublicKeyGraph() {

		return this.publicKeyGraph;
	}

	public void setPublicKeyGraph(Graph publicKeyGraph) {

		this.publicKeyGraph = publicKeyGraph;
	}
}
