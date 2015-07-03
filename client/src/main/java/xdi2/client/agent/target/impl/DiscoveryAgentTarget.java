package xdi2.client.agent.target.impl;

import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.client.agent.target.AgentConnection;
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


	@Override
	public AgentConnection connect(XDIArc targetPeerRootXDIArc) throws Xdi2AgentException, Xdi2ClientException {

		// check if we can provide the target peer root

		XDIDiscoveryResult xdiDiscoveryResult = this.getXdiDiscoveryClient().discoverFromRegistry(XdiPeerRoot.getXDIAddressOfPeerRootXDIArc(targetPeerRootXDIArc), null);

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

		// construct the connection

		AgentConnection connection = new HTTPAgentConnection(cloudNumber, xdiEndpointUrl);

		// done

		return connection;
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
