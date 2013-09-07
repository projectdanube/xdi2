package xdi2.server.factory.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.core.features.nodetypes.XdiLocalRoot;
import xdi2.core.features.nodetypes.XdiPeerRoot;
import xdi2.core.util.iterators.SelectingMappingIterator;
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

	public Iterator<String> getRequestPaths(final String messagingTargetFactoryPath) {

		Iterator<XdiPeerRoot> ownerPeerRoots = XdiLocalRoot.findLocalRoot(this.getRegistryGraph()).getPeerRoots();

		return new SelectingMappingIterator<XdiPeerRoot, String> (ownerPeerRoots) {

			@Override
			public boolean select(XdiPeerRoot ownerPeerRoot) {

				return ! ownerPeerRoot.isSelfPeerRoot();
			}

			@Override
			public String map(XdiPeerRoot ownerPeerRoot) {

				XDI3Segment owner = ownerPeerRoot.getXriOfPeerRoot();

				String ownerString;

				try {

					ownerString = URLEncoder.encode(owner.toString(), "UTF-8");
				} catch (UnsupportedEncodingException ex) { 

					throw new Xdi2RuntimeException(ex.getMessage(), ex);
				}

				return messagingTargetFactoryPath + "/" + ownerString;
			}
		};
	}

	@Override
	public MessagingTarget mountMessagingTarget(HttpEndpointRegistry httpEndpointRegistry, String messagingTargetFactoryPath, String requestPath) throws Xdi2ServerException, Xdi2MessagingException {

		// parse owner

		String ownerString = requestPath.substring(messagingTargetFactoryPath.length());
		if (ownerString.startsWith("/")) ownerString = ownerString.substring(1);
		if (ownerString.contains("/")) ownerString = ownerString.substring(0, ownerString.indexOf("/"));

		XDI3Segment owner;

		try {

			owner = XDI3Segment.create(URLDecoder.decode(ownerString, "UTF-8"));
		} catch (UnsupportedEncodingException ex) { 

			throw new Xdi2RuntimeException(ex.getMessage(), ex);
		}

		// find the owner's XDI peer root

		XdiPeerRoot ownerPeerRoot = XdiLocalRoot.findLocalRoot(this.getRegistryGraph()).findPeerRoot(owner, false);

		if (ownerPeerRoot == null) {

			log.warn("Peer root for " + owner + " not found in the registry graph. Ignoring.");
			return null;
		}

		if (ownerPeerRoot.isSelfPeerRoot()) {

			log.warn("Peer root for " + owner + " is the owner of the registry graph. Ignoring.");
			return null;
		}

		// find the owner's context node

		ContextNode ownerContextNode = this.getRegistryGraph().getDeepContextNode(owner);

		// create and mount the new messaging target

		String messagingTargetPath = messagingTargetFactoryPath + "/" + ownerString;

		log.info("Will create messaging target for " + owner + " at " + messagingTargetPath);

		return super.mountMessagingTarget(httpEndpointRegistry, messagingTargetPath, owner, ownerPeerRoot, ownerContextNode);
	}

	@Override
	public MessagingTarget updateMessagingTarget(HttpEndpointRegistry httpEndpointRegistry, String messagingTargetFactoryPath, String requestPath, MessagingTarget messagingTarget) throws Xdi2ServerException, Xdi2MessagingException {

		// parse owner

		String ownerString = requestPath.substring(messagingTargetFactoryPath.length());
		if (ownerString.startsWith("/")) ownerString = ownerString.substring(1);
		if (ownerString.contains("/")) ownerString = ownerString.substring(0, ownerString.indexOf("/"));

		XDI3Segment owner;

		try {

			owner = XDI3Segment.create(URLDecoder.decode(ownerString, "UTF-8"));
		} catch (UnsupportedEncodingException ex) { 

			throw new Xdi2ServerException(ex.getMessage(), ex);
		}

		// find the owner's peer root context node

		XdiPeerRoot ownerPeerRoot = XdiLocalRoot.findLocalRoot(this.getRegistryGraph()).findPeerRoot(owner, false);

		if (ownerPeerRoot == null) {

			log.warn("Peer root for " + owner + " no longer found in the registry graph. Removing messaging target.");

			try {

				messagingTarget.shutdown();
			} catch (Exception ex) {

				throw new Xdi2ServerException("Cannot shut down messaging target: " + ex.getMessage(), ex);
			}

			// unmount the messaging target

			httpEndpointRegistry.unmountMessagingTarget(messagingTarget);

			return null;
		} else {

			return messagingTarget;
		}
	}

	public Graph getRegistryGraph() {

		return this.registryGraph;
	}

	public void setRegistryGraph(Graph registryGraph) {

		this.registryGraph = registryGraph;
	}
}
