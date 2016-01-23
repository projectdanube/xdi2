package xdi2.client.events;

import java.net.URI;

import xdi2.discovery.XDIDiscoveryResult;
import xdi2.messaging.MessageEnvelope;

public class XDIDiscoverFromAuthorityEvent extends XDIDiscoverEvent {

	private static final long serialVersionUID = 1161787510790828490L;

	private URI xdiEndpointUri;

	public XDIDiscoverFromAuthorityEvent(Object source, MessageEnvelope messageEnvelope, XDIDiscoveryResult discoveryResult, URI xdiEndpointUri) {

		super(source, messageEnvelope, discoveryResult);

		this.xdiEndpointUri = xdiEndpointUri;
	}

	public URI getXdiEndpointUri() {

		return this.xdiEndpointUri;
	}
}
