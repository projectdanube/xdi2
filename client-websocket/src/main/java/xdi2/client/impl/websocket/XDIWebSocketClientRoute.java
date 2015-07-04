package xdi2.client.impl.websocket;

import java.net.URL;

import xdi2.client.XDIClientRoute;
import xdi2.client.impl.XDIAbstractClientRoute;
import xdi2.core.syntax.XDIArc;

public class XDIWebSocketClientRoute extends XDIAbstractClientRoute<XDIWebSocketClient> implements XDIClientRoute<XDIWebSocketClient> {

	private URL xdiWebSocketEndpointUrl;

	public XDIWebSocketClientRoute(XDIArc toPeerRootXDIArc, URL xdiWebSocketEndpointUrl) {

		super(toPeerRootXDIArc);

		this.xdiWebSocketEndpointUrl = xdiWebSocketEndpointUrl;
	}

	@Override
	public XDIWebSocketClient constructXDIClient() {

		return new XDIWebSocketClient(this.xdiWebSocketEndpointUrl);
	}
}
