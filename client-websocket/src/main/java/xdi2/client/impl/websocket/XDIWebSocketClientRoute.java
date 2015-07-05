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

	@Override
	public XDIWebSocketClient constructXDIClient() {

		return new XDIWebSocketClient(this.session, this.xdiWebSocketEndpointUri);
	}
}
