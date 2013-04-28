package xdi2.client.events;

import xdi2.discovery.XDIDiscovery;
import xdi2.discovery.XDIDiscoveryResult;
import xdi2.messaging.MessageEnvelope;

public class XDIDiscoveryEvent extends XDIEvent {

	private static final long serialVersionUID = -9221664294927183588L;

	private String xri;
	private MessageEnvelope messageEnvelope;
	private XDIDiscoveryResult discoveryResult;

	public XDIDiscoveryEvent(Object source, String xri, MessageEnvelope messageEnvelope, XDIDiscoveryResult discoveryResult) {

		super(source);

		this.xri = xri;
		this.messageEnvelope = messageEnvelope;
		this.discoveryResult = discoveryResult;
	}

	@Override
	public XDIDiscovery getSource() {

		return (XDIDiscovery) super.getSource();
	}

	public String getXri() {

		return this.xri;
	}

	public MessageEnvelope getMessageEnvelope() {

		return this.messageEnvelope;
	}

	public XDIDiscoveryResult getDiscoveryResult() {

		return this.discoveryResult;
	}
}
