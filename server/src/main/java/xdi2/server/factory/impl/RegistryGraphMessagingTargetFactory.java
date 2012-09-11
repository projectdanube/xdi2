package xdi2.server.factory.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Literal;
import xdi2.core.features.multiplicity.Multiplicity;
import xdi2.core.features.remoteroots.RemoteRoots;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.core.xri3.impl.XRI3SubSegment;
import xdi2.messaging.target.MessagingTarget;
import xdi2.server.exceptions.Xdi2ServerException;
import xdi2.server.registry.EndpointRegistry;

public class RegistryGraphMessagingTargetFactory extends StandardGraphMessagingTargetFactory {

	private static final XRI3Segment XRI_SECRET_TOKEN = new XRI3Segment("" + Multiplicity.entitySingletonArcXri(new XRI3SubSegment("$secret")) + Multiplicity.attributeSingletonArcXri(new XRI3SubSegment("$token")));

	private static final Logger log = LoggerFactory.getLogger(RegistryGraphMessagingTargetFactory.class);
	
	private Graph registryGraph;

	@Override
	public void mountMessagingTarget(EndpointRegistry endpointRegistry, String messagingTargetFactoryPath, String requestPath) throws Xdi2ServerException {

		// parse owner

		String ownerString = requestPath.substring(messagingTargetFactoryPath.length() + 1);
		if (ownerString.contains("/")) ownerString = ownerString.substring(0, ownerString.indexOf("/"));

		String messagingTargetPath = messagingTargetFactoryPath + "/" + ownerString;

		XRI3Segment owner = new XRI3Segment(ownerString);

		// look into registry

		ContextNode remoteRootContextNode = RemoteRoots.findRemoteRootContextNode(this.getRegistryGraph(), owner, false);
		if (remoteRootContextNode == null) {

			log.warn("Remote root context node for " + owner + " not found in the registry graph. Ignoring.");
			return;
		}

		ContextNode selfRemoteContextNode = RemoteRoots.getSelfRemoteRootContextNode(this.getRegistryGraph());
		if (remoteRootContextNode.equals(selfRemoteContextNode)) {

			log.warn("Remote root context node for " + owner + " is the owner of the registry graph. Ignoring.");
			return;
		}

		XRI3Segment[] ownerSynonyms = new XRI3Segment[0];

		Literal sharedSecretLiteral = this.getRegistryGraph().findLiteral(new XRI3Segment("" + owner + XRI_SECRET_TOKEN));
		String sharedSecret = sharedSecretLiteral == null ? null : sharedSecretLiteral.getLiteralData();

		// create and mount the new messaging target

		super.mountStandardMessagingTarget(endpointRegistry, messagingTargetPath, owner, ownerSynonyms, sharedSecret);
	}

	@Override
	public void updateMessagingTarget(EndpointRegistry endpointRegistry, String messagingTargetFactoryPath, String requestPath, MessagingTarget messagingTarget) throws Xdi2ServerException {

		// parse owner

		String ownerString = requestPath.substring(messagingTargetFactoryPath.length() + 1);
		if (ownerString.contains("/")) ownerString = ownerString.substring(0, ownerString.indexOf("/"));

		XRI3Segment owner = new XRI3Segment(ownerString);

		// look into registry

		ContextNode remoteRootContextNode = RemoteRoots.findRemoteRootContextNode(this.getRegistryGraph(), owner, false);
		if (remoteRootContextNode == null) {

			log.warn("Remote root context node for " + owner + " no longer found in the registry graph. Removing messaging target.");

			try {

				messagingTarget.shutdown();
			} catch (Exception ex) {

				throw new Xdi2ServerException("Cannot shut down messaging target: " + ex.getMessage(), ex);
			}

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
