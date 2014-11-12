package xdi2.client.events;

import xdi2.discovery.XDIDiscoveryClient;
import xdi2.discovery.XDIDiscoveryResult;
import xdi2.messaging.MessageEnvelope;

public abstract class XDIDiscoverEvent extends XDIEvent {

	private static final long serialVersionUID = -9221664294927183588L;

	private MessageEnvelope messageEnvelope;
	private XDIDiscoveryResult discoveryResult;

	public XDIDiscoverEvent(Object source, MessageEnvelope messageEnvelope, XDIDiscoveryResult discoveryResult) {

		super(source);

		this.messageEnvelope = messageEnvelope;
		this.discoveryResult = discoveryResult;
	}

	@Override
	public XDIDiscoveryClient getSource() {

		return (XDIDiscoveryClient) super.getSource();
	}

	public MessageEnvelope getMessageEnvelope() {

		return this.messageEnvelope;
	}

	public XDIDiscoveryResult getDiscoveryResult() {

		return this.discoveryResult;
	}
}
