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

	public XDIHttpClientRoute(HttpURLConnection httpURLConnection) {

		this(null, httpURLConnection, null);
	}

	public XDIHttpClientRoute(URI xdiEndpointUri) {

		this(null, null, xdiEndpointUri);
	}

	public XDIHttpClientRoute() {

		this(null, null, null);
	}

	@Override
	protected XDIHttpClient constructXDIClientInternal() {

		return new XDIHttpClient(this.httpURLConnection, this.xdiEndpointUri);
	}

	/*
	 * Getters and setters
	 */

	public HttpURLConnection getHttpURLConnection() {

		return this.httpURLConnection;
	}

	public void setHttpURLConnection(HttpURLConnection httpURLConnection) {

		this.httpURLConnection = httpURLConnection;
	}

	public URI getXdiEndpointUri() {

		return this.xdiEndpointUri;
	}

	public void setXdiEndpointUri(URI xdiEndpointUri) {

		this.xdiEndpointUri = xdiEndpointUri;
	}
}
