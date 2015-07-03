package xdi2.client.agent.target.impl;

import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.client.agent.target.AgentConnection;
import xdi2.client.agent.target.AgentTarget;
import xdi2.client.exceptions.Xdi2AgentException;
import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.core.syntax.CloudNumber;
import xdi2.core.syntax.XDIArc;

public class HTTPAgentTarget implements AgentTarget {

	private static final Logger log = LoggerFactory.getLogger(HTTPAgentTarget.class);

	private XDIArc targetPeerRootXDIArc;
	private CloudNumber cloudNumber;
	private URL xdiEndpointUrl;

	public HTTPAgentTarget(XDIArc targetPeerRootXDIArc, CloudNumber cloudNumber, URL xdiEndpointUrl) {

		this.targetPeerRootXDIArc = targetPeerRootXDIArc;
		this.cloudNumber = cloudNumber;
		this.xdiEndpointUrl = xdiEndpointUrl;
	}

	@Override
	public AgentConnection connect(XDIArc targetPeerRootXDIArc) throws Xdi2AgentException, Xdi2ClientException {

		// check if we can provide the target peer root

		if (! this.targetPeerRootXDIArc.equals(targetPeerRootXDIArc)) {

			log.debug("HTTP URL does not have target peer root " + targetPeerRootXDIArc + ". Skipping.");
			return null;
		}

		// construct the connection

		AgentConnection connection = new HTTPAgentConnection(this.cloudNumber, this.xdiEndpointUrl);

		// done

		return connection;
	}

	/*
	 * Getters and setters
	 */

	public XDIArc getTargetPeerRootXDIArc() {

		return this.targetPeerRootXDIArc;
	}

	public void setTargetPeerRootXDIArc(XDIArc targetPeerRootXDIArc) {
		
		this.targetPeerRootXDIArc = targetPeerRootXDIArc;
	}

	public CloudNumber getCloudNumber() {
		
		return this.cloudNumber;
	}

	public void setCloudNumber(CloudNumber cloudNumber) {
		
		this.cloudNumber = cloudNumber;
	}

	public URL getXdiEndpointUrl() {
		
		return this.xdiEndpointUrl;
	}

	public void setXdiEndpointUrl(URL xdiEndpointUrl) {
		
		this.xdiEndpointUrl = xdiEndpointUrl;
	}
}
