package xdi2.transport.impl.http.factory.impl;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.features.nodetypes.XdiLocalRoot;
import xdi2.core.features.nodetypes.XdiPeerRoot;
import xdi2.core.features.nodetypes.XdiRoot;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.util.iterators.SelectingMappingIterator;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.MessagingTarget;
import xdi2.transport.exceptions.Xdi2TransportException;
import xdi2.transport.impl.http.registry.HttpMessagingTargetRegistry;

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
	public MessagingTarget mountMessagingTarget(HttpMessagingTargetRegistry httpMessagingTargetRegistry, String messagingTargetFactoryPath, String requestPath) throws Xdi2TransportException, Xdi2MessagingException {

		// parse owner

		String ownerString = requestPath.substring(messagingTargetFactoryPath.length());
		if (ownerString.startsWith("/")) ownerString = ownerString.substring(1);
		if (ownerString.contains("/")) ownerString = ownerString.substring(0, ownerString.indexOf("/"));

		XDIAddress ownerXri = XDIAddress.create(ownerString);

		// find the owner's XDI peer root

		XdiPeerRoot ownerPeerRoot = XdiLocalRoot.findLocalRoot(this.getRegistryGraph()).getPeerRoot(ownerXri, false);

		if (ownerPeerRoot == null) {

			log.warn("Peer root for " + ownerPeerRoot + " not found in the registry graph. Ignoring.");
			return null;
		}

		XdiRoot dereferencedOwnerPeerRoot = ownerPeerRoot.dereference();
		if (dereferencedOwnerPeerRoot instanceof XdiPeerRoot) ownerPeerRoot = (XdiPeerRoot) dereferencedOwnerPeerRoot;

		if (ownerPeerRoot.isSelfPeerRoot()) {

			log.warn("Peer root for " + ownerPeerRoot + " is the owner of the registry graph. Ignoring.");
			return null;
		}

		// update the owner

		ownerXri = ownerPeerRoot.getAddressOfPeerRoot();

		// find the owner's context node

		ContextNode ownerContextNode = this.getRegistryGraph().getDeepContextNode(ownerXri, true);

		// create and mount the new messaging target

		String messagingTargetPath = messagingTargetFactoryPath + "/" + ownerXri.toString();

		log.info("Going to mount new messaging target for " + ownerXri + " at " + messagingTargetPath);

		return super.mountMessagingTarget(httpMessagingTargetRegistry, messagingTargetPath, ownerXri, ownerPeerRoot, ownerContextNode);
	}

	@Override
	public MessagingTarget updateMessagingTarget(HttpMessagingTargetRegistry httpMessagingTargetRegistry, String messagingTargetFactoryPath, String requestPath, MessagingTarget messagingTarget) throws Xdi2TransportException, Xdi2MessagingException {

		// parse owner

		String ownerString = requestPath.substring(messagingTargetFactoryPath.length());
		if (ownerString.startsWith("/")) ownerString = ownerString.substring(1);
		if (ownerString.contains("/")) ownerString = ownerString.substring(0, ownerString.indexOf("/"));

		XDIAddress ownerXri = XDIAddress.create(ownerString);

		// find the owner's XDI peer root

		XdiPeerRoot ownerPeerRoot = XdiLocalRoot.findLocalRoot(this.getRegistryGraph()).getPeerRoot(ownerXri, false);

		if (ownerPeerRoot == null) {

			log.warn("Peer root for " + ownerXri + " no longer found in the registry graph. Going to unmount messaging target.");

			// unmount the messaging target

			httpMessagingTargetRegistry.unmountMessagingTarget(messagingTarget);

			return null;
		} else {

			return messagingTarget;
		}
	}

	@Override
	public Iterator<XDIArc> getOwnerPeerRootXris() {

		Iterator<XdiPeerRoot> ownerPeerRoots = XdiLocalRoot.findLocalRoot(this.getRegistryGraph()).getPeerRoots();

		return new SelectingMappingIterator<XdiPeerRoot, XDIArc> (ownerPeerRoots) {

			@Override
			public boolean select(XdiPeerRoot ownerPeerRoot) {

				if (ownerPeerRoot.isSelfPeerRoot()) return false;
				if (ownerPeerRoot.dereference() != ownerPeerRoot) return false;

				return true;
			}

			@Override
			public XDIArc map(XdiPeerRoot ownerPeerRoot) {

				return ownerPeerRoot.getArc();
			}
		};
	}

	@Override
	public String getRequestPath(String messagingTargetFactoryPath, XDIArc ownerPeerRootXri) {

		XDIAddress ownerXri = XdiPeerRoot.getAddressOfPeerRootArc(ownerPeerRootXri);

		XdiPeerRoot ownerPeerRoot = XdiLocalRoot.findLocalRoot(this.getRegistryGraph()).getPeerRoot(ownerXri, false);
		if (ownerPeerRoot == null) return null;

		String requestPath = messagingTargetFactoryPath + "/" + ownerXri.toString();

		if (log.isDebugEnabled()) log.debug("requestPath for ownerPeerRootXri " + ownerPeerRootXri + " is " + requestPath);

		return requestPath;
	}

	/*
	 * Getters and setters
	 */

	public Graph getRegistryGraph() {

		return this.registryGraph;
	}

	public void setRegistryGraph(Graph registryGraph) {

		this.registryGraph = registryGraph;
	}
}
