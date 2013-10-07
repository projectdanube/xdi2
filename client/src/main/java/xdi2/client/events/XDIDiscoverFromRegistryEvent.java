package xdi2.client.events;

import xdi2.core.xri3.XDI3Segment;
import xdi2.discovery.XDIDiscoveryResult;
import xdi2.messaging.MessageEnvelope;

public class XDIDiscoverFromRegistryEvent extends XDIDiscoverEvent {

	private static final long serialVersionUID = -4512281567446646198L;

	private XDI3Segment xri;

	public XDIDiscoverFromRegistryEvent(Object source, MessageEnvelope messageEnvelope, XDIDiscoveryResult discoveryResult, XDI3Segment xri) {

		super(source, messageEnvelope, discoveryResult);

		this.xri = xri;
	}

	public XDI3Segment getXri() {

		return this.xri;
	}
}
