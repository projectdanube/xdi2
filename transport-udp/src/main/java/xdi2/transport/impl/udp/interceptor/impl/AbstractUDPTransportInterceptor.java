package xdi2.transport.impl.udp.interceptor.impl;

import java.io.IOException;

import xdi2.messaging.target.MessagingTarget;
import xdi2.transport.exceptions.Xdi2TransportException;
import xdi2.transport.impl.udp.UDPTransport;
import xdi2.transport.impl.udp.UDPTransportRequest;
import xdi2.transport.impl.udp.UDPTransportResponse;
import xdi2.transport.impl.udp.interceptor.UDPTransportInterceptor;
import xdi2.transport.interceptor.impl.AbstractTransportInterceptor;

public abstract class AbstractUDPTransportInterceptor extends AbstractTransportInterceptor implements UDPTransportInterceptor {

	@Override
	public boolean processDatagram(UDPTransport udpTransport, UDPTransportRequest request, UDPTransportResponse response, MessagingTarget messagingTarget) throws Xdi2TransportException, IOException {

		return false;
	}
}
