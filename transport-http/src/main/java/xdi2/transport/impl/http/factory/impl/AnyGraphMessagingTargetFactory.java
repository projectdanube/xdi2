package xdi2.transport.impl.http.factory.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.core.features.nodetypes.XdiPeerRoot;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.exceptions.Xdi2MessagingException;
import xdi2.transport.exceptions.Xdi2TransportException;
import xdi2.transport.impl.http.registry.HttpMessagingTargetRegistry;

/**
 * This messaging target factory create messaging targets for any path.
 * 
 * @author markus
 */
public class AnyGraphMessagingTargetFactory extends PrototypingMessagingTargetFactory {

	private static final Logger log = LoggerFactory.getLogger(AnyGraphMessagingTargetFactory.class);

	@Override
	public MessagingTarget mountMessagingTarget(HttpMessagingTargetRegistry httpMessagingTargetRegistry, String messagingTargetFactoryPath, String requestPath) throws Xdi2TransportException, Xdi2MessagingException {

		// parse owner

		String ownerString = requestPath.substring(messagingTargetFactoryPath.length());
		if (ownerString.startsWith("/")) ownerString = ownerString.substring(1);
		if (ownerString.contains("/")) ownerString = ownerString.substring(0, ownerString.indexOf("/"));

		XDIAddress ownerXDIAddress = XDIAddress.create(ownerString);

		// create and mount the new messaging target

		String messagingTargetPath = messagingTargetFactoryPath + "/" + ownerXDIAddress.toString();

		log.info("Will create messaging target for " + ownerXDIAddress + " at " + messagingTargetPath);

		return super.mountMessagingTarget(httpMessagingTargetRegistry, messagingTargetPath, ownerXDIAddress, null, null);
	}

	@Override
	public MessagingTarget updateMessagingTarget(HttpMessagingTargetRegistry httpMessagingTargetRegistry, String messagingTargetFactoryPath, String requestPath, MessagingTarget messagingTarget) throws Xdi2TransportException {

		return messagingTarget;
	}

	@Override
	public String getRequestPath(String messagingTargetFactoryPath, XDIArc ownerPeerRootXDIArc) {

		XDIAddress ownerXDIAddress = XdiPeerRoot.getXDIAddressOfPeerRootXDIArc(ownerPeerRootXDIArc);

		String ownerString;

		try {

			ownerString = URLEncoder.encode(ownerXDIAddress.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) { 

			throw new Xdi2RuntimeException(ex.getMessage(), ex);
		}

		return messagingTargetFactoryPath + "/" + ownerString;
	}
}
