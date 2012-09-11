package xdi2.server.factory.impl;

import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.messaging.target.MessagingTarget;
import xdi2.server.exceptions.Xdi2ServerException;
import xdi2.server.registry.EndpointRegistry;

public class AnyGraphMessagingTargetFactory extends StandardGraphMessagingTargetFactory {

	@Override
	public void mountMessagingTarget(EndpointRegistry endpointRegistry, String messagingTargetFactoryPath, String requestPath) throws Xdi2ServerException {

		// parse owner

		String ownerString = requestPath.substring(messagingTargetFactoryPath.length() + 1);
		if (ownerString.contains("/")) ownerString = ownerString.substring(0, ownerString.indexOf("/"));

		String messagingTargetPath = messagingTargetFactoryPath + "/" + ownerString;

		XRI3Segment owner = new XRI3Segment(ownerString);
		XRI3Segment[] ownerSynonyms = new XRI3Segment[0];
		String sharedSecret = null;

		// create and mount the new messaging target

		super.mountStandardMessagingTarget(endpointRegistry, messagingTargetPath, owner, ownerSynonyms, sharedSecret);
	}

	@Override
	public void updateMessagingTarget(EndpointRegistry endpointRegistry, String messagingTargetFactoryPath, String requestPath, MessagingTarget messagingTarget) throws Xdi2ServerException {

	}
}
