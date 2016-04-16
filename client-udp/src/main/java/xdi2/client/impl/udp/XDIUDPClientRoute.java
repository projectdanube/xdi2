package xdi2.client.impl.udp;

import java.net.URI;

import javax.websocket.Session;

import xdi2.client.XDIClientRoute;
import xdi2.client.impl.XDIAbstractClientRoute;
import xdi2.core.syntax.XDIArc;

public class XDIUDPClientRoute extends XDIAbstractClientRoute<XDIUDPClient> implements XDIClientRoute<XDIUDPClient> {

	private Session session;
	private URI xdiWebSocketEndpointUri;

	public XDIUDPClientRoute(XDIArc toPeerRootXDIArc, Session session, URI xdiWebSocketEndpointUri) {

		super(toPeerRootXDIArc);

		this.session = session;
		this.xdiWebSocketEndpointUri = xdiWebSocketEndpointUri;
	}

	public XDIUDPClientRoute(XDIArc toPeerRootXDIArc, Session session) {

		this(toPeerRootXDIArc, session, null);
	}

	public XDIUDPClientRoute(XDIArc toPeerRootXDIArc, URI xdiWebSocketEndpointUri) {

		this(toPeerRootXDIArc, null, xdiWebSocketEndpointUri);
	}

	public XDIUDPClientRoute(Session session) {

		this(null, session, null);
	}

	public XDIUDPClientRoute(URI xdiWebSocketEndpointUri) {

		this(null, null, xdiWebSocketEndpointUri);
	}

	public XDIUDPClientRoute() {

		this(null, null, null);
	}

	@Override
	public XDIUDPClient constructXDIClientInternal() {

		return new XDIUDPClient(this.getSession(), this.getXdiWebSocketEndpointUri());
	}

	/*
	 * Getters and setters
	 */

	public Session getSession() {

		return this.session;
	}

	public void setSession(Session session) {

		this.session = session;
	}

	public URI getXdiWebSocketEndpointUri() {

		return this.xdiWebSocketEndpointUri;
	}

	public void setXdiWebSocketEndpointUri(URI xdiWebSocketEndpointUri) {

		this.xdiWebSocketEndpointUri = xdiWebSocketEndpointUri;
	}
}
