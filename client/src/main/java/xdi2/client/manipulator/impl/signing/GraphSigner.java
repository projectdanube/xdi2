package xdi2.client.manipulator.impl.signing;

import java.security.GeneralSecurityException;
import java.security.PrivateKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.Graph;
import xdi2.core.features.keys.Keys;
import xdi2.core.features.nodetypes.XdiCommonRoot;
import xdi2.core.features.nodetypes.XdiEntity;
import xdi2.core.syntax.XDIAddress;
import xdi2.messaging.Message;

/**
 * A Signer that can authenticate an XDI message using a "private key graph",
 * which contains sender addresses and private keys.
 */
public class GraphSigner extends PrivateKeySigner {

	private static Logger log = LoggerFactory.getLogger(GraphSigner.class.getName());

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
	public PrivateKey getPrivateKey(Message message) {

		// sender
		
		XDIAddress senderXDIAddress = message.getSenderXDIAddress();
		if (senderXDIAddress == null) return null;

		// sender entity

		XdiEntity senderXdiEntity = XdiCommonRoot.findCommonRoot(this.getPrivateKeyGraph()).getXdiEntity(senderXDIAddress, false);
		senderXdiEntity = senderXdiEntity == null ? null : senderXdiEntity.dereference();

		if (log.isDebugEnabled()) log.debug("Sender entity: " + senderXdiEntity);

		if (senderXdiEntity == null) return null;

		// find signature private key

		PrivateKey privateKey;

		try {

			privateKey = Keys.getSignaturePrivateKey(senderXdiEntity);
		} catch (GeneralSecurityException ex) {

			if (log.isWarnEnabled()) log.warn("Invalid signature private key: " + ex.getMessage(), ex);

			return null;
		}

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
