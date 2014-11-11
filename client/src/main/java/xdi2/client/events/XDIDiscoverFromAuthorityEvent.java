package xdi2.client.events;

import java.net.URL;

import xdi2.discovery.XDIDiscoveryResult;
import xdi2.messaging.request.RequestMessageEnvelope;

public class XDIDiscoverFromAuthorityEvent extends XDIDiscoverEvent {

	private static final long serialVersionUID = 1161787510790828490L;

	private URL xdiEndpointUri;

	public XDIDiscoverFromAuthorityEvent(Object source, RequestMessageEnvelope messageEnvelope, XDIDiscoveryResult discoveryResult, URL xdiEndpointUri) {

		super(source, messageEnvelope, discoveryResult);

		this.xdiEndpointUri = xdiEndpointUri;
	}

	public URL getXdiEndpointUri() {

		return this.xdiEndpointUri;
	}
}
