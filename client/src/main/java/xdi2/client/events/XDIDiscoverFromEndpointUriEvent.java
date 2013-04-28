package xdi2.client.events;

import xdi2.discovery.XDIDiscoveryResult;
import xdi2.messaging.MessageEnvelope;

public class XDIDiscoverFromEndpointUriEvent extends XDIDiscoverEvent {

	private static final long serialVersionUID = 1161787510790828490L;

	private String endpointUri;

	public XDIDiscoverFromEndpointUriEvent(Object source, MessageEnvelope messageEnvelope, XDIDiscoveryResult discoveryResult, String endpointUri) {

		super(source, messageEnvelope, discoveryResult);

		this.endpointUri = endpointUri;
	}

	public String getEndpointUri() {

		return this.endpointUri;
	}
}
