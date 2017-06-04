package xdi2.core.security.signature.create;

import java.security.GeneralSecurityException;
import java.security.interfaces.RSAPrivateKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.Graph;
import xdi2.core.features.keys.Keys;
import xdi2.core.features.nodetypes.XdiCommonRoot;
import xdi2.core.features.nodetypes.XdiEntity;
import xdi2.core.features.nodetypes.XdiPeerRoot;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.util.GraphUtil;
import xdi2.messaging.container.MessagingContainer;
import xdi2.messaging.container.impl.graph.GraphMessagingContainer;
import xdi2.transport.registry.MessagingContainerMount;
import xdi2.transport.registry.MessagingContainerRegistry;

/**
 * This is an RSAPrivateKeySignatureCreator that create an XDI RSASignature by
 * obtaining private keys from a messaging container registry.
 */
public class RSAMessagingContainerRegistryPrivateKeySignatureCreator extends RSAPrivateKeySignatureCreator {

	private static Logger log = LoggerFactory.getLogger(RSAMessagingContainerRegistryPrivateKeySignatureCreator.class.getName());

	private MessagingContainerRegistry messagingContainerRegistry;

	public RSAMessagingContainerRegistryPrivateKeySignatureCreator(String digestAlgorithm, Integer digestLength, MessagingContainerRegistry messagingContainerRegistry) {

		super(digestAlgorithm, digestLength);

		this.messagingContainerRegistry = messagingContainerRegistry;
	}

	public RSAMessagingContainerRegistryPrivateKeySignatureCreator(String digestAlgorithm, Integer digestLength) {

		super(digestAlgorithm, digestLength);

		this.messagingContainerRegistry = null;
	}

	public RSAMessagingContainerRegistryPrivateKeySignatureCreator(MessagingContainerRegistry messagingContainerRegistry) {

		super();

		this.messagingContainerRegistry = messagingContainerRegistry;
	}

	public RSAMessagingContainerRegistryPrivateKeySignatureCreator() {

		super();

		this.messagingContainerRegistry = null;
	}

	@Override
	public RSAPrivateKey getPrivateKey(XDIAddress signerXDIAddress) throws GeneralSecurityException {

		if (signerXDIAddress == null) throw new NullPointerException();

		// graph

		MessagingContainerMount messagingContainerMount;

		try {

			messagingContainerMount = this.getMessagingContainerRegistry().lookup(XdiPeerRoot.createPeerRootXDIArc(signerXDIAddress));
		} catch (Exception ex) {

			throw new GeneralSecurityException("Messaging target not found for " + signerXDIAddress + ": " + ex.getMessage(), ex);
		}

		if (log.isDebugEnabled()) log.debug("Messaging target mount: " + messagingContainerMount);
		if (messagingContainerMount == null) return null;

		MessagingContainer messagingContainer = messagingContainerMount.getMessagingContainer();
		Graph graph = ((GraphMessagingContainer) messagingContainer).getGraph();	// TODO: what if this is another messaging container?

		// signer entity

		XdiEntity signerXdiEntity = XdiCommonRoot.findCommonRoot(graph).getXdiEntity(signerXDIAddress, false);
		signerXdiEntity = signerXdiEntity == null ? null : signerXdiEntity.dereference();

		if (log.isDebugEnabled()) log.debug("Signer entity: " + signerXdiEntity + " in graph " + GraphUtil.getOwnerPeerRootXDIArc(graph));
		if (signerXdiEntity == null) return null;

		// find private key

		RSAPrivateKey privateKey = rsaPrivateKeyFromPrivateKeyString(Keys.getSignaturePrivateKey(signerXdiEntity));

		// done

		return privateKey;
	}

	/*
	 * Getters and setters
	 */

	public MessagingContainerRegistry getMessagingContainerRegistry() {

		return this.messagingContainerRegistry;
	}

	public void setMessagingContainerRegistry(MessagingContainerRegistry messagingContainerRegistry) {

		this.messagingContainerRegistry = messagingContainerRegistry;
	}
}
