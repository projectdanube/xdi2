package xdi2.client.impl.websocket;

import java.net.URL;

import javax.websocket.Session;

import xdi2.client.XDIClientRoute;
import xdi2.client.impl.XDIAbstractClientRoute;
import xdi2.core.syntax.XDIArc;

public class XDIWebSocketClientRoute extends XDIAbstractClientRoute<XDIWebSocketClient> implements XDIClientRoute<XDIWebSocketClient> {

	private Session session;
	private URL xdiWebSocketEndpointUrl;

	public XDIWebSocketClientRoute(XDIArc toPeerRootXDIArc, Session session, URL xdiWebSocketEndpointUrl) {

		super(toPeerRootXDIArc);

		this.session = session;
		this.xdiWebSocketEndpointUrl = xdiWebSocketEndpointUrl;
	}

	@Override
	public XDIWebSocketClient constructXDIClient() {

		return new XDIWebSocketClient(this.session, this.xdiWebSocketEndpointUrl);
	}
}
