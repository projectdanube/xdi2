package xdi2.transport.impl.uri;

import xdi2.transport.TransportRequest;
import xdi2.transport.TransportResponse;
import xdi2.transport.impl.AbstractTransport;
import xdi2.transport.registry.impl.uri.UriMessagingTargetRegistry;

public abstract class UriTransport <REQUEST extends TransportRequest, RESPONSE extends TransportResponse> extends AbstractTransport<REQUEST, RESPONSE> {

	public abstract UriMessagingTargetRegistry getUriMessagingTargetRegistry();
}
