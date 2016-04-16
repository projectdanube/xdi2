package xdi2.transport.impl.udp.interceptor;

import java.io.IOException;

import xdi2.messaging.target.MessagingTarget;
import xdi2.transport.exceptions.Xdi2TransportException;
import xdi2.transport.impl.udp.UDPTransport;
import xdi2.transport.impl.udp.UDPTransportRequest;
import xdi2.transport.impl.udp.UDPTransportResponse;
import xdi2.transport.interceptor.TransportInterceptor;

/**
 * Interceptor that is executed when it receives an incoming message.
 * 
 * @author markus
 */
public interface UDPTransportInterceptor extends TransportInterceptor {

	/**
	 * Run when the UDP transport receives a message.
	 * @return True, if the request has been fully handled.
	 */
	public boolean processDatagram(UDPTransport udpTransport, UDPTransportRequest request, UDPTransportResponse response, MessagingTarget messagingTarget) throws Xdi2TransportException, IOException;
}
