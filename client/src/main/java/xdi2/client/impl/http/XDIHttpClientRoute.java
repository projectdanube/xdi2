package xdi2.client.impl.http;

import java.net.URL;

import xdi2.client.XDIClientRoute;
import xdi2.client.impl.XDIAbstractClientRoute;
import xdi2.core.syntax.XDIArc;

public class XDIHttpClientRoute extends XDIAbstractClientRoute<XDIHttpClient> implements XDIClientRoute<XDIHttpClient> {

	private URL xdiEndpointUrl;

	public XDIHttpClientRoute(XDIArc toPeerRootXDIArc, URL xdiEndpointUrl) {

		super(toPeerRootXDIArc);

		this.xdiEndpointUrl = xdiEndpointUrl;
	}

	@Override
	public XDIHttpClient constructXDIClient() {

		return new XDIHttpClient(this.xdiEndpointUrl);
	}
}
