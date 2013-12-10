package xdi2.server.factory.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.core.features.nodetypes.XdiPeerRoot;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3SubSegment;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.MessagingTarget;
import xdi2.server.exceptions.Xdi2ServerException;
import xdi2.server.registry.HttpMessagingTargetRegistry;

/**
 * This messaging target factory create messaging targets for any path.
 * 
 * @author markus
 */
public class AnyGraphMessagingTargetFactory extends PrototypingMessagingTargetFactory {

	private static final Logger log = LoggerFactory.getLogger(AnyGraphMessagingTargetFactory.class);

	@Override
	public MessagingTarget mountMessagingTarget(HttpMessagingTargetRegistry httpMessagingTargetRegistry, String messagingTargetFactoryPath, String requestPath) throws Xdi2ServerException, Xdi2MessagingException {

		// parse owner

		String ownerString = requestPath.substring(messagingTargetFactoryPath.length());
		if (ownerString.startsWith("/")) ownerString = ownerString.substring(1);
		if (ownerString.contains("/")) ownerString = ownerString.substring(0, ownerString.indexOf("/"));

		XDI3Segment ownerXri = XDI3Segment.create(ownerString);

		// create and mount the new messaging target

		String messagingTargetPath = messagingTargetFactoryPath + "/" + ownerXri.toString();

		log.info("Will create messaging target for " + ownerXri + " at " + messagingTargetPath);

		return super.mountMessagingTarget(httpMessagingTargetRegistry, messagingTargetPath, ownerXri, null, null);
	}

	@Override
	public MessagingTarget updateMessagingTarget(HttpMessagingTargetRegistry httpMessagingTargetRegistry, String messagingTargetFactoryPath, String requestPath, MessagingTarget messagingTarget) throws Xdi2ServerException {

		return messagingTarget;
	}

	@Override
	public String getRequestPath(String messagingTargetFactoryPath, XDI3SubSegment ownerPeerRootXri) {

		XDI3Segment ownerXri = XdiPeerRoot.getXriOfPeerRootArcXri(ownerPeerRootXri);

		String ownerString;

		try {

			ownerString = URLEncoder.encode(ownerXri.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) { 

			throw new Xdi2RuntimeException(ex.getMessage(), ex);
		}

		return messagingTargetFactoryPath + "/" + ownerString;
	}
}
