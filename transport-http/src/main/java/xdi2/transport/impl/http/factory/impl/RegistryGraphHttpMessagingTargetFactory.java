package xdi2.transport.impl.http.factory.impl;

import java.util.Date;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.LiteralNode;
import xdi2.core.constants.XDITimestampsConstants;
import xdi2.core.features.nodetypes.XdiAttribute;
import xdi2.core.features.nodetypes.XdiCommonRoot;
import xdi2.core.features.nodetypes.XdiPeerRoot;
import xdi2.core.features.nodetypes.XdiRoot;
import xdi2.core.features.timestamps.Timestamps;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.util.iterators.SelectingMappingIterator;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.exceptions.Xdi2MessagingException;
import xdi2.transport.exceptions.Xdi2TransportException;
import xdi2.transport.impl.http.registry.HttpMessagingTargetRegistry;

/**
 * This messaging target factory uses a "registry graph" as a basis to decide what 
 * messaging targets to create.
 * 
 * @author markus
 */
public class RegistryGraphHttpMessagingTargetFactory extends PrototypingHttpMessagingTargetFactory {

	private static final Logger log = LoggerFactory.getLogger(RegistryGraphHttpMessagingTargetFactory.class);

	public static final XDIAddress XDI_ADD_ENABLED = XDIAddress.create("<#enabled>");

	private Graph registryGraph;
	private boolean defaultDisabled;
	private String disabledError;
	private String expiredError;

	public RegistryGraphHttpMessagingTargetFactory(Graph registryGraph, boolean defaultDisabled, String disabledError, String expiredError) {

		super();

		this.registryGraph = registryGraph;
		this.defaultDisabled = defaultDisabled;
		this.disabledError = disabledError;
		this.expiredError = expiredError;
	}

	public RegistryGraphHttpMessagingTargetFactory() {

		this(null, false, null, null);
	}

	@Override
	public MessagingTarget mountMessagingTarget(HttpMessagingTargetRegistry httpMessagingTargetRegistry, String messagingTargetFactoryPath, String requestPath, boolean checkDisabled, boolean checkExpired) throws Xdi2TransportException, Xdi2MessagingException {

		// parse owner

		String ownerString = requestPath.substring(messagingTargetFactoryPath.length());
		if (ownerString.startsWith("/")) ownerString = ownerString.substring(1);
		if (ownerString.contains("/")) ownerString = ownerString.substring(0, ownerString.indexOf("/"));

		XDIAddress ownerXDIAddress = XDIAddress.create(ownerString);

		// find the owner's XDI peer root

		XdiPeerRoot ownerXdiPeerRoot = XdiCommonRoot.findCommonRoot(this.getRegistryGraph()).getPeerRoot(ownerXDIAddress, false);

		if (ownerXdiPeerRoot == null) {

			log.warn("Peer root " + ownerXdiPeerRoot + " not found in the registry graph. Ignoring.");
			return null;
		}

		XdiRoot dereferencedOwnerPeerRoot = ownerXdiPeerRoot.dereference();
		if (dereferencedOwnerPeerRoot instanceof XdiPeerRoot) ownerXdiPeerRoot = (XdiPeerRoot) dereferencedOwnerPeerRoot;

		if (ownerXdiPeerRoot.isSelfPeerRoot()) {

			log.warn("Peer root " + ownerXdiPeerRoot + " is the owner of the registry graph. Ignoring.");
			return null;
		}

		// disabled?

		if (checkDisabled && ! this.checkEnabled(ownerXdiPeerRoot)) {

			log.warn("Peer root " + ownerXdiPeerRoot + " is disabled. Ignoring.");
			if (this.getDisabledError() != null) throw new Xdi2TransportException(this.getDisabledError());
			return null;
		}

		// expired?

		if (checkExpired && this.checkExpired(ownerXdiPeerRoot)) {

			log.warn("Peer root " + ownerXdiPeerRoot + " is expired. Ignoring.");
			if (this.getExpiredError() != null) throw new Xdi2TransportException(this.getExpiredError());
			return null;
		}

		// update the owner

		ownerXDIAddress = ownerXdiPeerRoot.getXDIAddressOfPeerRoot();

		// find the owner's context node

		ContextNode ownerContextNode = this.getRegistryGraph().getDeepContextNode(ownerXDIAddress, true);

		// create and mount the new messaging target

		String messagingTargetPath = messagingTargetFactoryPath + "/" + ownerXDIAddress.toString();

		log.info("Going to mount new messaging target for " + ownerXDIAddress + " at " + messagingTargetPath);

		return super.mountMessagingTarget(httpMessagingTargetRegistry, messagingTargetPath, ownerXDIAddress, ownerXdiPeerRoot, ownerContextNode);
	}

	@Override
	public MessagingTarget updateMessagingTarget(HttpMessagingTargetRegistry httpMessagingTargetRegistry, String messagingTargetFactoryPath, String requestPath, boolean checkDisabled, boolean checkExpired, MessagingTarget messagingTarget) throws Xdi2TransportException, Xdi2MessagingException {

		// parse owner

		String ownerString = requestPath.substring(messagingTargetFactoryPath.length());
		if (ownerString.startsWith("/")) ownerString = ownerString.substring(1);
		if (ownerString.contains("/")) ownerString = ownerString.substring(0, ownerString.indexOf("/"));

		XDIAddress ownerXDIAddress = XDIAddress.create(ownerString);

		// find the owner's XDI peer root

		XdiPeerRoot ownerXdiPeerRoot = XdiCommonRoot.findCommonRoot(this.getRegistryGraph()).getPeerRoot(ownerXDIAddress, false);

		if (ownerXdiPeerRoot == null) {

			log.warn("Peer root " + ownerXdiPeerRoot + " no longer found in the registry graph. Going to unmount messaging target.");

			// unmount the messaging target

			httpMessagingTargetRegistry.unmountMessagingTarget(messagingTarget);
			return null;
		}

		// disabled?

		if (checkDisabled && ! this.checkEnabled(ownerXdiPeerRoot)) {

			log.warn("Peer root " + ownerXdiPeerRoot + " is disabled. Going to unmount messaging target.");

			// unmount the messaging target

			httpMessagingTargetRegistry.unmountMessagingTarget(messagingTarget);
			if (this.getDisabledError() != null) throw new Xdi2TransportException(this.getDisabledError());
			return null;
		}

		// expired?

		if (checkExpired && this.checkExpired(ownerXdiPeerRoot)) {

			log.warn("Peer root " + ownerXdiPeerRoot + " is expired. Going to unmount messaging target.");

			// unmount the messaging target

			httpMessagingTargetRegistry.unmountMessagingTarget(messagingTarget);
			if (this.getDisabledError() != null) throw new Xdi2TransportException(this.getDisabledError());
			return null;
		}

		// done

		return messagingTarget;
	}

	@Override
	public Iterator<XDIArc> getOwnerPeerRootXDIArcs() {

		Iterator<XdiPeerRoot> ownerPeerRoots = XdiCommonRoot.findCommonRoot(this.getRegistryGraph()).getPeerRoots();

		return new SelectingMappingIterator<XdiPeerRoot, XDIArc> (ownerPeerRoots) {

			@Override
			public boolean select(XdiPeerRoot ownerPeerRoot) {

				if (ownerPeerRoot.isSelfPeerRoot()) return false;
				if (ownerPeerRoot.dereference() != ownerPeerRoot) return false;

				return true;
			}

			@Override
			public XDIArc map(XdiPeerRoot ownerPeerRoot) {

				return ownerPeerRoot.getXDIArc();
			}
		};
	}

	@Override
	public String getRequestPath(String messagingTargetFactoryPath, XDIArc ownerPeerRootXDIArc) {

		XDIAddress ownerXDIAddress = XdiPeerRoot.getXDIAddressOfPeerRootXDIArc(ownerPeerRootXDIArc);

		XdiPeerRoot ownerPeerRoot = XdiCommonRoot.findCommonRoot(this.getRegistryGraph()).getPeerRoot(ownerXDIAddress, false);
		if (ownerPeerRoot == null) return null;

		String requestPath = messagingTargetFactoryPath + "/" + ownerXDIAddress.toString();

		if (log.isDebugEnabled()) log.debug("requestPath for ownerPeerRootXDIArc " + ownerPeerRootXDIArc + " is " + requestPath);

		return requestPath;
	}

	private boolean checkEnabled(XdiPeerRoot ownerPeerRoot) throws Xdi2TransportException {

		// enabled or disabled?

		XdiAttribute enabledXdiAttribute = ownerPeerRoot.getXdiAttribute(XDI_ADD_ENABLED, false);
		LiteralNode enabledLiteralNode = enabledXdiAttribute == null ? null : enabledXdiAttribute.getLiteralNode();

		Boolean enabled = enabledLiteralNode == null ? null : enabledLiteralNode.getLiteralDataBoolean();

		if (Boolean.TRUE.equals(enabled)) {

			return true;
		} else if (enabled == null && ! this.isDefaultDisabled()) {

			return true;
		}

		return false;
	}

	private boolean checkExpired(XdiPeerRoot ownerPeerRoot) throws Xdi2TransportException {

		// expired?

		Date expirationDate = Timestamps.getTimestamp(ownerPeerRoot, XDITimestampsConstants.XDI_ADD_AS_EXPIRATION);
		if (expirationDate == null) return false;

		return expirationDate.before(new Date());
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

	public boolean isDefaultDisabled() {

		return this.defaultDisabled;
	}

	public void setDefaultDisabled(boolean defaultDisabled) {

		this.defaultDisabled = defaultDisabled;
	}

	public String getDisabledError() {

		return this.disabledError;
	}

	public void setDisabledError(String disabledError) {

		this.disabledError = disabledError;
	}

	public String getExpiredError() {

		return this.expiredError;
	}

	public void setExpiredError(String expiredError) {

		this.expiredError = expiredError;
	}
}
