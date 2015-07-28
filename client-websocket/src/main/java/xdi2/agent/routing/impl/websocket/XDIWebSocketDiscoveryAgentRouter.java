package xdi2.agent.routing.impl.websocket;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.agent.routing.XDIAgentRouter;
import xdi2.agent.routing.impl.XDIAbstractAgentRouter;
import xdi2.client.constants.XDIClientConstants;
import xdi2.client.exceptions.Xdi2AgentException;
import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.client.impl.websocket.XDIWebSocketClient;
import xdi2.client.impl.websocket.XDIWebSocketClientRoute;
import xdi2.core.features.nodetypes.XdiPeerRoot;
import xdi2.core.syntax.CloudNumber;
import xdi2.core.syntax.XDIArc;
import xdi2.discovery.XDIDiscoveryClient;
import xdi2.discovery.XDIDiscoveryResult;

public class XDIWebSocketDiscoveryAgentRouter extends XDIAbstractAgentRouter<XDIWebSocketClientRoute, XDIWebSocketClient> implements XDIAgentRouter<XDIWebSocketClientRoute, XDIWebSocketClient> {

	private static final Logger log = LoggerFactory.getLogger(XDIWebSocketDiscoveryAgentRouter.class);

	private XDIDiscoveryClient xdiDiscoveryClient;

	public XDIWebSocketDiscoveryAgentRouter(XDIDiscoveryClient xdiDiscoveryClient) {

		this.xdiDiscoveryClient = xdiDiscoveryClient;
	}

	public XDIWebSocketDiscoveryAgentRouter() {

		this.xdiDiscoveryClient = null;
	}

	@Override
	protected XDIWebSocketClientRoute routeInternal(XDIArc toPeerRootXDIArc) throws Xdi2AgentException {

		// check if we can provide the TO peer root

		if (toPeerRootXDIArc == null) {

			if (log.isDebugEnabled()) log.debug("Cannot route to unknown peer root. Skipping.");
			return null;
		}

		XDIDiscoveryResult xdiDiscoveryResult;

		try {

			xdiDiscoveryResult = this.getXdiDiscoveryClient().discoverFromRegistry(XdiPeerRoot.getXDIAddressOfPeerRootXDIArc(toPeerRootXDIArc), XDIClientConstants.WEBSOCKET_ENDPOINT_URI_TYPE);
		} catch (Xdi2ClientException ex) {

			throw new Xdi2AgentException("Discovery problem: " + ex.getMessage(), ex);
		}

		if (xdiDiscoveryResult == null) {

			log.debug("Unable to discover TO peer root " + toPeerRootXDIArc + " using discovery client " + this.getXdiDiscoveryClient() + ". Skipping.");
			return null;
		}

		CloudNumber cloudNumber = xdiDiscoveryResult.getCloudNumber();
		URI xdiWebSocketEndpointUri = xdiDiscoveryResult.getXdiWebSocketEndpointUri();

		if (cloudNumber == null) {

			log.debug("Unable to discover cloud number for peer root " + toPeerRootXDIArc + " and discovery client " + this.getXdiDiscoveryClient() + ". Skipping.");
			return null;
		}

		if (xdiWebSocketEndpointUri == null) {

			log.debug("Unable to discover XDI WebSocket endpoint URI for peer root " + toPeerRootXDIArc + " and discovery client " + this.getXdiDiscoveryClient() + ". Skipping.");
			return null;
		}

		// construct the route

		XDIWebSocketClientRoute route = new XDIWebSocketClientRoute(cloudNumber.getPeerRootXDIArc(), null, xdiWebSocketEndpointUri);

		// done

		return route;
	}

	/*
	 * Getters and setters
	 */

	public XDIDiscoveryClient getXdiDiscoveryClient() {

		return this.xdiDiscoveryClient;
	}

	public void setXdiDiscoveryClient(XDIDiscoveryClient xdiDiscoveryClient) {

		this.xdiDiscoveryClient = xdiDiscoveryClient;
	}
}
