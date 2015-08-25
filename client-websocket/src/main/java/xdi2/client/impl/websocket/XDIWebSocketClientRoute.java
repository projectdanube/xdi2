package xdi2.client.impl.websocket;

import java.net.URI;

import javax.websocket.Session;

import xdi2.client.XDIClientRoute;
import xdi2.client.impl.XDIAbstractClientRoute;
import xdi2.core.syntax.XDIArc;

public class XDIWebSocketClientRoute extends XDIAbstractClientRoute<XDIWebSocketClient> implements XDIClientRoute<XDIWebSocketClient> {

	private Session session;
	private URI xdiWebSocketEndpointUri;

	public XDIWebSocketClientRoute(XDIArc toPeerRootXDIArc, Session session, URI xdiWebSocketEndpointUri) {

		super(toPeerRootXDIArc);

		this.session = session;
		this.xdiWebSocketEndpointUri = xdiWebSocketEndpointUri;
	}

	public XDIWebSocketClientRoute(XDIArc toPeerRootXDIArc, Session session) {

		this(toPeerRootXDIArc, session, null);
	}

	public XDIWebSocketClientRoute(XDIArc toPeerRootXDIArc, URI xdiWebSocketEndpointUri) {

		this(toPeerRootXDIArc, null, xdiWebSocketEndpointUri);
	}

	public XDIWebSocketClientRoute(Session session) {

		this(null, session, null);
	}

	public XDIWebSocketClientRoute(URI xdiWebSocketEndpointUri) {

		this(null, null, xdiWebSocketEndpointUri);
	}

	public XDIWebSocketClientRoute() {

		this(null, null, null);
	}

	@Override
	public XDIWebSocketClient constructXDIClientInternal() {

		return new XDIWebSocketClient(this.getSession(), this.getXdiWebSocketEndpointUri());
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
