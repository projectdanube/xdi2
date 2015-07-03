package xdi2.client.agent.target.impl;

import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.client.agent.target.AgentRoute;
import xdi2.client.agent.target.AgentTarget;
import xdi2.client.exceptions.Xdi2AgentException;
import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.core.features.nodetypes.XdiPeerRoot;
import xdi2.core.syntax.CloudNumber;
import xdi2.core.syntax.XDIArc;
import xdi2.discovery.XDIDiscoveryClient;
import xdi2.discovery.XDIDiscoveryResult;

public class DiscoveryAgentTarget implements AgentTarget {

	private static final Logger log = LoggerFactory.getLogger(DiscoveryAgentTarget.class);

	private XDIDiscoveryClient xdiDiscoveryClient;

	public DiscoveryAgentTarget(XDIDiscoveryClient xdiDiscoveryClient) {

		this.xdiDiscoveryClient = xdiDiscoveryClient;
	}

	public DiscoveryAgentTarget() {

		this.xdiDiscoveryClient = null;
	}

	@Override
	public AgentRoute route(XDIArc targetPeerRootXDIArc) throws Xdi2AgentException {

		// check if we can provide the target peer root

		XDIDiscoveryResult xdiDiscoveryResult;

		try {

			xdiDiscoveryResult = this.getXdiDiscoveryClient().discoverFromRegistry(XdiPeerRoot.getXDIAddressOfPeerRootXDIArc(targetPeerRootXDIArc), null);
		} catch (Xdi2ClientException ex) {

			throw new Xdi2AgentException("Discovery problem: " + ex.getMessage(), ex);
		}

		if (xdiDiscoveryResult == null) {

			log.debug("Unable to discover target peer root " + targetPeerRootXDIArc + " using discovery client " + this.getXdiDiscoveryClient() + ". Skipping.");
			return null;
		}

		CloudNumber cloudNumber = xdiDiscoveryResult.getCloudNumber();
		URL xdiEndpointUrl = xdiDiscoveryResult.getXdiEndpointUrl();

		if (cloudNumber == null) {

			log.debug("Unable to discover cloud number for peer root " + targetPeerRootXDIArc + " and discovery client " + this.getXdiDiscoveryClient() + ". Skipping.");
			return null;
		}

		if (xdiEndpointUrl == null) {

			log.debug("Unable to discover XDI endpoint URI for peer root " + targetPeerRootXDIArc + " and discovery client " + this.getXdiDiscoveryClient() + ". Skipping.");
			return null;
		}

		// construct the route

		AgentRoute route = new HTTPAgentRoute(cloudNumber, xdiEndpointUrl);

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
