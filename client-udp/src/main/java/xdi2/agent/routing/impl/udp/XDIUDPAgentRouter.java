package xdi2.agent.routing.impl.udp;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.agent.routing.XDIAgentRouter;
import xdi2.agent.routing.impl.XDIAbstractAgentRouter;
import xdi2.client.exceptions.Xdi2AgentException;
import xdi2.client.impl.udp.XDIUDPClient;
import xdi2.client.impl.udp.XDIUDPClientRoute;
import xdi2.core.syntax.XDIArc;

public class XDIUDPAgentRouter extends XDIAbstractAgentRouter<XDIUDPClientRoute, XDIUDPClient> implements XDIAgentRouter<XDIUDPClientRoute, XDIUDPClient> {

	private static final Logger log = LoggerFactory.getLogger(XDIUDPAgentRouter.class);

	private XDIArc toPeerRootXDIArc;
	private URI xdiWebSocketEndpointUri;

	public XDIUDPAgentRouter(XDIArc toPeerRootXDIArc, URI xdiWebSocketEndpointUri) {

		this.toPeerRootXDIArc = toPeerRootXDIArc;
		this.xdiWebSocketEndpointUri = xdiWebSocketEndpointUri;
	}

	public XDIUDPAgentRouter() {

		this.toPeerRootXDIArc = null;
		this.xdiWebSocketEndpointUri = null;
	}

	@Override
	protected XDIUDPClientRoute routeInternal(XDIArc toPeerRootXDIArc) throws Xdi2AgentException {

		// check if we can provide the TO peer root

		if (toPeerRootXDIArc == null) {

			if (log.isDebugEnabled()) log.debug("Cannot route to unknown peer root. Skipping.");
			return null;
		}

		if (this.getToPeerRootXDIArc() == null) {

			throw new Xdi2AgentException("Invalid route has no associated peer root. Aborting.");
		}

		if (! toPeerRootXDIArc.equals(this.getToPeerRootXDIArc())) {

			if (log.isDebugEnabled()) log.debug("XDI WebSocket endpoint " + this.getXdiWebSocketEndpointUri() + " is no route to peer root " + toPeerRootXDIArc + " (" + this.getToPeerRootXDIArc() + "). Skipping.");
			return null;
		}

		// construct the route

		XDIUDPClientRoute route = new XDIUDPClientRoute(toPeerRootXDIArc, null, this.getXdiWebSocketEndpointUri());

		// done

		return route;
	}

	/*
	 * Getters and setters
	 */

	public XDIArc getToPeerRootXDIArc() {

		return this.toPeerRootXDIArc;
	}

	public void setToPeerRootXDIArc(XDIArc toPeerRootXDIArc) {

		this.toPeerRootXDIArc = toPeerRootXDIArc;
	}

	public URI getXdiWebSocketEndpointUri() {

		return this.xdiWebSocketEndpointUri;
	}

	public void setXdiWebSocketEndpointUri(URI xdiWebSocketEndpointUri) {

		this.xdiWebSocketEndpointUri = xdiWebSocketEndpointUri;
	}
}
