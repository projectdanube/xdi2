package xdi2.server.factory.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.features.remoteroots.RemoteRoots;
import xdi2.core.xri3.XDI3Segment;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.MessagingTarget;
import xdi2.server.exceptions.Xdi2ServerException;
import xdi2.server.registry.EndpointRegistry;

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
	public void mountMessagingTarget(EndpointRegistry endpointRegistry, String messagingTargetFactoryPath, String requestPath) throws Xdi2ServerException, Xdi2MessagingException {

		// parse owner

		String ownerString = requestPath.substring(messagingTargetFactoryPath.length() + 1);
		if (ownerString.contains("/")) ownerString = ownerString.substring(0, ownerString.indexOf("/"));

		String messagingTargetPath = messagingTargetFactoryPath + "/" + ownerString;

		XDI3Segment owner = XDI3Segment.create(ownerString);

		// find the owner's remote root context node

		ContextNode ownerRemoteRootContextNode = RemoteRoots.findRemoteRootContextNode(this.getRegistryGraph(), owner, false);

		if (ownerRemoteRootContextNode == null) {

			log.warn("Remote root context node for " + owner + " not found in the registry graph. Ignoring.");
			return;
		}

		if (RemoteRoots.isSelfRemoteRootContextNode(ownerRemoteRootContextNode)) {

			log.warn("Remote root context node for " + owner + " is the owner of the registry graph. Ignoring.");
			return;
		}

		// find the owner's context node

		ContextNode ownerContextNode = this.getRegistryGraph().findContextNode(owner, false);

		// create and mount the new messaging target

		log.info("Will create messaging target for " + owner);
		
		super.mountMessagingTarget(endpointRegistry, messagingTargetPath, owner, ownerRemoteRootContextNode, ownerContextNode);
	}

	@Override
	public void updateMessagingTarget(EndpointRegistry endpointRegistry, String messagingTargetFactoryPath, String requestPath, MessagingTarget messagingTarget) throws Xdi2ServerException, Xdi2MessagingException {

		// parse owner

		String ownerString = requestPath.substring(messagingTargetFactoryPath.length() + 1);
		if (ownerString.contains("/")) ownerString = ownerString.substring(0, ownerString.indexOf("/"));

		XDI3Segment owner = XDI3Segment.create(ownerString);

		// find the owner's remote root context node

		ContextNode remoteRootContextNode = RemoteRoots.findRemoteRootContextNode(this.getRegistryGraph(), owner, false);
		if (remoteRootContextNode == null) {

			log.warn("Remote root context node for " + owner + " no longer found in the registry graph. Removing messaging target.");

			try {

				messagingTarget.shutdown();
			} catch (Exception ex) {

				throw new Xdi2ServerException("Cannot shut down messaging target: " + ex.getMessage(), ex);
			}

			// unmount the messaging target

			endpointRegistry.unmountMessagingTarget(messagingTarget);
		}
	}

	public Graph getRegistryGraph() {

		return this.registryGraph;
	}

	public void setRegistryGraph(Graph registryGraph) {

		this.registryGraph = registryGraph;
	}
}
