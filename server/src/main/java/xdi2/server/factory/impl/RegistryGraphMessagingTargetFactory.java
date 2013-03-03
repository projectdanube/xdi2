package xdi2.server.factory.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.features.roots.PeerRoot;
import xdi2.core.features.roots.Roots;
import xdi2.core.xri3.XDI3Segment;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.MessagingTarget;
import xdi2.server.exceptions.Xdi2ServerException;
import xdi2.server.registry.HttpEndpointRegistry;

/**
 * This messaging target factory uses a "registry graph" as a basis to decide what 
 * messaging targets to create.
 * 
 * @author markus
 */
public class RegistryGraphMessagingTargetFactory extends PrototypingMessagingTargetFactory {

	private static final Logger log = LoggerFactory.getLogger(RegistryGraphMessagingTargetFactory.class);

	private Graph registryGraph;

	@Override
	public void mountMessagingTarget(HttpEndpointRegistry httpEndpointRegistry, String messagingTargetFactoryPath, String requestPath) throws Xdi2ServerException, Xdi2MessagingException {

		// parse owner

		String ownerString = requestPath.substring(messagingTargetFactoryPath.length() + 1);
		if (ownerString.contains("/")) ownerString = ownerString.substring(0, ownerString.indexOf("/"));

		String messagingTargetPath = messagingTargetFactoryPath + "/" + ownerString;

		XDI3Segment owner = XDI3Segment.create(ownerString);

		// find the owner's XDI peer root

		PeerRoot ownerPeerRoot = Roots.findLocalRoot(this.getRegistryGraph()).findPeerRoot(owner, false);

		if (ownerPeerRoot == null) {

			log.warn("Peer root for " + owner + " not found in the registry graph. Ignoring.");
			return;
		}

		if (ownerPeerRoot.isSelfPeerRoot()) {

			log.warn("Peer root for " + owner + " is the owner of the registry graph. Ignoring.");
			return;
		}

		// find the owner's context node

		ContextNode ownerContextNode = this.getRegistryGraph().findContextNode(owner, false);

		// create and mount the new messaging target

		log.info("Will create messaging target for " + owner);
		
		super.mountMessagingTarget(httpEndpointRegistry, messagingTargetPath, owner, ownerPeerRoot, ownerContextNode);
	}

	@Override
	public void updateMessagingTarget(HttpEndpointRegistry httpEndpointRegistry, String messagingTargetFactoryPath, String requestPath, MessagingTarget messagingTarget) throws Xdi2ServerException, Xdi2MessagingException {

		// parse owner

		String ownerString = requestPath.substring(messagingTargetFactoryPath.length() + 1);
		if (ownerString.contains("/")) ownerString = ownerString.substring(0, ownerString.indexOf("/"));

		XDI3Segment owner = XDI3Segment.create(ownerString);

		// find the owner's peer root context node

		PeerRoot ownerPeerRoot = Roots.findLocalRoot(this.getRegistryGraph()).findPeerRoot(owner, false);

		if (ownerPeerRoot == null) {

			log.warn("Peer root for " + owner + " no longer found in the registry graph. Removing messaging target.");

			try {

				messagingTarget.shutdown();
			} catch (Exception ex) {

				throw new Xdi2ServerException("Cannot shut down messaging target: " + ex.getMessage(), ex);
			}

			// unmount the messaging target

			httpEndpointRegistry.unmountMessagingTarget(messagingTarget);
		}
	}

	public Graph getRegistryGraph() {

		return this.registryGraph;
	}

	public void setRegistryGraph(Graph registryGraph) {

		this.registryGraph = registryGraph;
	}
}
