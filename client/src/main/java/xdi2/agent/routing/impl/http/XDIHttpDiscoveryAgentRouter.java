package xdi2.agent.routing.impl.http;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.agent.routing.XDIAgentRouter;
import xdi2.agent.routing.impl.XDIAbstractAgentRouter;
import xdi2.client.exceptions.Xdi2AgentException;
import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.client.impl.http.XDIHttpClient;
import xdi2.client.impl.http.XDIHttpClientRoute;
import xdi2.core.features.nodetypes.XdiPeerRoot;
import xdi2.core.syntax.CloudNumber;
import xdi2.core.syntax.XDIArc;
import xdi2.discovery.XDIDiscoveryClient;
import xdi2.discovery.XDIDiscoveryResult;

public class XDIHttpDiscoveryAgentRouter extends XDIAbstractAgentRouter<XDIHttpClientRoute, XDIHttpClient> implements XDIAgentRouter<XDIHttpClientRoute, XDIHttpClient> {

	private static final Logger log = LoggerFactory.getLogger(XDIHttpDiscoveryAgentRouter.class);

	private XDIDiscoveryClient xdiDiscoveryClient;

	public XDIHttpDiscoveryAgentRouter(XDIDiscoveryClient xdiDiscoveryClient) {

		this.xdiDiscoveryClient = xdiDiscoveryClient;
	}

	public XDIHttpDiscoveryAgentRouter() {

		this.xdiDiscoveryClient = XDIDiscoveryClient.DEFAULT_DISCOVERY_CLIENT;
	}

	@Override
	protected XDIHttpClientRoute routeInternal(XDIArc toPeerRootXDIArc) throws Xdi2AgentException {

		// check if we can provide the TO peer root

		if (toPeerRootXDIArc == null) {

			if (log.isDebugEnabled()) log.debug("Cannot route to unknown peer root. Skipping.");
			return null;
		}

		XDIDiscoveryResult xdiDiscoveryResult;

		try {

			xdiDiscoveryResult = this.getXdiDiscoveryClient().discoverFromRegistry(XdiPeerRoot.getXDIAddressOfPeerRootXDIArc(toPeerRootXDIArc));
		} catch (Xdi2ClientException ex) {

			throw new Xdi2AgentException("Discovery problem: " + ex.getMessage(), ex);
		}

		if (xdiDiscoveryResult == null) {

			log.debug("Unable to discover TO peer root " + toPeerRootXDIArc + " using discovery client " + this.getXdiDiscoveryClient() + ". Skipping.");
			return null;
		}

		CloudNumber cloudNumber = xdiDiscoveryResult.getCloudNumber();
		URI xdiEndpointUri = xdiDiscoveryResult.getXdiEndpointUri();

		if (cloudNumber == null) {

			log.debug("Unable to discover cloud number for peer root " + toPeerRootXDIArc + " and discovery client " + this.getXdiDiscoveryClient() + ". Skipping.");
			return null;
		}

		if (xdiEndpointUri == null) {

			log.debug("Unable to discover XDI endpoint URI for peer root " + toPeerRootXDIArc + " and discovery client " + this.getXdiDiscoveryClient() + ". Skipping.");
			return null;
		}

		// construct the route

		XDIHttpClientRoute route = new XDIHttpClientRoute(cloudNumber.getPeerRootXDIArc(), xdiEndpointUri);

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
