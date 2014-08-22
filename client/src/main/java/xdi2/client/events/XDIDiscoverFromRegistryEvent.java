package xdi2.client.events;

import xdi2.core.syntax.XDIAddress;
import xdi2.discovery.XDIDiscoveryResult;
import xdi2.messaging.MessageEnvelope;

public class XDIDiscoverFromRegistryEvent extends XDIDiscoverEvent {

	private static final long serialVersionUID = -4512281567446646198L;

	private XDIAddress query;

	public XDIDiscoverFromRegistryEvent(Object source, MessageEnvelope messageEnvelope, XDIDiscoveryResult discoveryResult, XDIAddress query) {

		super(source, messageEnvelope, discoveryResult);

		this.query = query;
	}

	public XDIAddress getQuery() {

		return this.query;
	}
}
