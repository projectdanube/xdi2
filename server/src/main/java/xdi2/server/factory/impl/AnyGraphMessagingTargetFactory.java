package xdi2.server.factory.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.xri3.XDI3Segment;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.MessagingTarget;
import xdi2.server.exceptions.Xdi2ServerException;
import xdi2.server.registry.HttpEndpointRegistry;

/**
 * This messaging target factory create messaging targets for any path.
 * 
 * @author markus
 */
public class AnyGraphMessagingTargetFactory extends PrototypingMessagingTargetFactory {

	private static final Logger log = LoggerFactory.getLogger(AnyGraphMessagingTargetFactory.class);

	@Override
	public MessagingTarget mountMessagingTarget(HttpEndpointRegistry httpEndpointRegistry, String messagingTargetFactoryPath, String requestPath) throws Xdi2ServerException, Xdi2MessagingException {

		// parse owner

		String ownerString = requestPath.substring(messagingTargetFactoryPath.length() + 1);
		if (ownerString.contains("/")) ownerString = ownerString.substring(0, ownerString.indexOf("/"));

		String messagingTargetPath = messagingTargetFactoryPath + "/" + ownerString;

		XDI3Segment owner;

		try {

			owner = XDI3Segment.create(URLDecoder.decode(ownerString, "UTF-8"));
		} catch (UnsupportedEncodingException ex) { 

			throw new Xdi2ServerException(ex.getMessage(), ex);
		}

		// create and mount the new messaging target

		log.info("Will create messaging target for " + owner);

		return super.mountMessagingTarget(httpEndpointRegistry, messagingTargetPath, owner, null, null);
	}

	@Override
	public MessagingTarget updateMessagingTarget(HttpEndpointRegistry httpEndpointRegistry, String messagingTargetFactoryPath, String requestPath, MessagingTarget messagingTarget) throws Xdi2ServerException {

		return messagingTarget;
	}
}
