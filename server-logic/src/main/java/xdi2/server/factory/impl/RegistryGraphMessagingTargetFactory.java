package xdi2.server.factory.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Literal;
import xdi2.core.features.remoteroots.RemoteRoots;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.server.registry.EndpointRegistry;

public class RegistryGraphMessagingTargetFactory extends StandardGraphMessagingTargetFactory {

	private static final Logger log = LoggerFactory.getLogger(RegistryGraphMessagingTargetFactory.class);
	
	private Graph registryGraph;

	@Override
	public void mountMessagingTarget(EndpointRegistry endpointRegistry, String messagingTargetFactoryPath, String requestPath) throws Xdi2MessagingException {

		// parse owner

		String ownerString = requestPath.substring(messagingTargetFactoryPath.length() + 1);
		if (ownerString.contains("/")) ownerString = ownerString.substring(0, ownerString.indexOf("/"));

		String messagingTargetPath = messagingTargetFactoryPath + "/" + ownerString;

		XRI3Segment owner = new XRI3Segment(ownerString);

		// look into registry

		ContextNode remoteRootContextNode = RemoteRoots.findRemoteRootContextNode(this.getRegistryGraph(), owner, false);
		if (remoteRootContextNode == null) {
			
			log.warn("Remote root context node for " + owner + " not found in registry");
			return;
		}

		XRI3Segment[] ownerSynonyms = new XRI3Segment[0];

		Literal sharedSecretLiteral = this.getRegistryGraph().findLiteral(new XRI3Segment(owner.toString() + "$secret$!($token)"));
		String sharedSecret = sharedSecretLiteral == null ? null : sharedSecretLiteral.getLiteralData();

		// create and mount the new messaging target

		super.mountStandardMessagingTarget(endpointRegistry, messagingTargetPath, owner, ownerSynonyms, sharedSecret);
	}

	public Graph getRegistryGraph() {

		return this.registryGraph;
	}

	public void setRegistryGraph(Graph registryGraph) {

		this.registryGraph = registryGraph;
	}
}
