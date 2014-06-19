package xdi2.client.events;

import xdi2.discovery.XDIDiscoveryResult;
import xdi2.messaging.MessageEnvelope;

public class XDIDiscoverFromAuthorityEvent extends XDIDiscoverEvent {

	private static final long serialVersionUID = 1161787510790828490L;

	private String xdiEndpointUri;

	public XDIDiscoverFromAuthorityEvent(Object source, MessageEnvelope messageEnvelope, XDIDiscoveryResult discoveryResult, String xdiEndpointUri) {

		super(source, messageEnvelope, discoveryResult);

		this.xdiEndpointUri = xdiEndpointUri;
	}

	public String getXdiEndpointUri() {

		return this.xdiEndpointUri;
	}
}
