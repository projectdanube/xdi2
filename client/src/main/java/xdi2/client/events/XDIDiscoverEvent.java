package xdi2.client.events;

import xdi2.discovery.XDIDiscoveryClient;
import xdi2.discovery.XDIDiscoveryResult;
import xdi2.messaging.request.RequestMessageEnvelope;

public abstract class XDIDiscoverEvent extends XDIEvent {

	private static final long serialVersionUID = -9221664294927183588L;

	private RequestMessageEnvelope messageEnvelope;
	private XDIDiscoveryResult discoveryResult;

	public XDIDiscoverEvent(Object source, RequestMessageEnvelope messageEnvelope, XDIDiscoveryResult discoveryResult) {

		super(source);

		this.messageEnvelope = messageEnvelope;
		this.discoveryResult = discoveryResult;
	}

	@Override
	public XDIDiscoveryClient getSource() {

		return (XDIDiscoveryClient) super.getSource();
	}

	public RequestMessageEnvelope getMessageEnvelope() {

		return this.messageEnvelope;
	}

	public XDIDiscoveryResult getDiscoveryResult() {

		return this.discoveryResult;
	}
}
