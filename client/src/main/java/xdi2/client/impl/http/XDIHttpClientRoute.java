package xdi2.client.impl.http;

import java.net.HttpURLConnection;
import java.net.URI;

import xdi2.client.XDIClientRoute;
import xdi2.client.impl.XDIAbstractClientRoute;
import xdi2.core.syntax.XDIArc;

public class XDIHttpClientRoute extends XDIAbstractClientRoute<XDIHttpClient> implements XDIClientRoute<XDIHttpClient> {

	private HttpURLConnection httpURLConnection;
	private URI xdiEndpointUri;

	public XDIHttpClientRoute(XDIArc toPeerRootXDIArc, HttpURLConnection httpURLConnection, URI xdiEndpointUri) {

		super(toPeerRootXDIArc);

		this.httpURLConnection = httpURLConnection;
		this.xdiEndpointUri = xdiEndpointUri;
	}

	public XDIHttpClientRoute(XDIArc toPeerRootXDIArc, HttpURLConnection httpURLConnection) {

		this(toPeerRootXDIArc, httpURLConnection, null);
	}

	public XDIHttpClientRoute(XDIArc toPeerRootXDIArc, URI xdiEndpointUri) {

		this(toPeerRootXDIArc, null, xdiEndpointUri);
	}

	@Override
	public XDIHttpClient constructXDIClient() {

		return new XDIHttpClient(this.httpURLConnection, this.xdiEndpointUri);
	}
}
