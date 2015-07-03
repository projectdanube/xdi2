package xdi2.agent.routing.impl;

import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.agent.routing.XDIAgentRouter;
import xdi2.client.exceptions.Xdi2AgentException;
import xdi2.client.impl.http.XDIHttpClient;
import xdi2.client.impl.http.XDIHttpClientRoute;
import xdi2.core.syntax.CloudNumber;
import xdi2.core.syntax.XDIArc;

public class XDIHttpAgentRouter implements XDIAgentRouter<XDIHttpClientRoute, XDIHttpClient> {

	private static final Logger log = LoggerFactory.getLogger(XDIHttpAgentRouter.class);

	private XDIArc targetPeerRootXDIArc;
	private CloudNumber cloudNumber;
	private URL xdiEndpointUrl;

	public XDIHttpAgentRouter(XDIArc targetPeerRootXDIArc, CloudNumber cloudNumber, URL xdiEndpointUrl) {

		this.targetPeerRootXDIArc = targetPeerRootXDIArc;
		this.cloudNumber = cloudNumber;
		this.xdiEndpointUrl = xdiEndpointUrl;
	}

	public XDIHttpAgentRouter() {

		this.targetPeerRootXDIArc = null;
		this.cloudNumber = null;
		this.xdiEndpointUrl = null;
	}

	@Override
	public XDIHttpClientRoute route(XDIArc targetPeerRootXDIArc) throws Xdi2AgentException {

		// check if we can provide the target peer root

		if (! this.targetPeerRootXDIArc.equals(targetPeerRootXDIArc)) {

			log.debug("HTTP URL does not have target peer root " + targetPeerRootXDIArc + ". Skipping.");
			return null;
		}

		// construct the route

		XDIHttpClientRoute route = new XDIHttpClientRoute(this.cloudNumber, this.xdiEndpointUrl);

		// done

		return route;
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
