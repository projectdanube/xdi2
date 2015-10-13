package xdi2.core.security.signature.create;

import java.security.GeneralSecurityException;
import java.security.PrivateKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.Graph;
import xdi2.core.features.keys.Keys;
import xdi2.core.features.nodetypes.XdiCommonRoot;
import xdi2.core.features.nodetypes.XdiEntity;
import xdi2.core.features.nodetypes.XdiPeerRoot;
import xdi2.core.syntax.XDIAddress;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.impl.graph.GraphMessagingTarget;
import xdi2.transport.exceptions.Xdi2TransportException;
import xdi2.transport.registry.MessagingTargetMount;
import xdi2.transport.registry.MessagingTargetRegistry;

/**
 * This is an RSAPrivateKeySignatureCreator that create an XDI RSASignature by
 * obtaining private keys from a messaging target registry.
 */
public class RSAMessagingTargetRegistryPrivateKeySignatureCreator extends RSAPrivateKeySignatureCreator {

	private static Logger log = LoggerFactory.getLogger(RSAMessagingTargetRegistryPrivateKeySignatureCreator.class.getName());

	private MessagingTargetRegistry messagingTargetRegistry;

	public RSAMessagingTargetRegistryPrivateKeySignatureCreator(String digestAlgorithm, Integer digestLength, MessagingTargetRegistry messagingTargetRegistry) {

		super(digestAlgorithm, digestLength);

		this.messagingTargetRegistry = messagingTargetRegistry;
	}

	public RSAMessagingTargetRegistryPrivateKeySignatureCreator(String digestAlgorithm, Integer digestLength) {

		super(digestAlgorithm, digestLength);

		this.messagingTargetRegistry = null;
	}

	public RSAMessagingTargetRegistryPrivateKeySignatureCreator(MessagingTargetRegistry messagingTargetRegistry) {

		super();

		this.messagingTargetRegistry = messagingTargetRegistry;
	}

	public RSAMessagingTargetRegistryPrivateKeySignatureCreator() {

		super();

		this.messagingTargetRegistry = null;
	}

	@Override
	public PrivateKey getPrivateKey(XDIAddress signerXDIAddress) throws GeneralSecurityException {

		if (signerXDIAddress == null) throw new NullPointerException();

		// graph

		MessagingTargetMount messagingTargetMount;

		try {

			messagingTargetMount = this.getMessagingTargetRegistry().lookup(XdiPeerRoot.createPeerRootXDIArc(signerXDIAddress));
		} catch (Xdi2TransportException | Xdi2MessagingException ex) {

			throw new GeneralSecurityException("Messaging target not found for " + signerXDIAddress);
		}

		MessagingTarget messagingTarget = messagingTargetMount.getMessagingTarget();
		Graph graph = ((GraphMessagingTarget) messagingTarget).getGraph();	// TODO: what if this is another messaging target?

		// signer entity

		XdiEntity signerXdiEntity = XdiCommonRoot.findCommonRoot(graph).getXdiEntity(signerXDIAddress, false);
		signerXdiEntity = signerXdiEntity == null ? null : signerXdiEntity.dereference();

		if (log.isDebugEnabled()) log.debug("Signer entity: " + signerXdiEntity);

		if (signerXdiEntity == null) return null;

		// find private key

		PrivateKey privateKey = Keys.getSignaturePrivateKey(signerXdiEntity);

		// done

		return privateKey;
	}

	/*
	 * Getters and setters
	 */

	public MessagingTargetRegistry getMessagingTargetRegistry() {

		return this.messagingTargetRegistry;
	}

	public void setMessagingTargetRegistry(MessagingTargetRegistry messagingTargetRegistry) {

		this.messagingTargetRegistry = messagingTargetRegistry;
	}
}
