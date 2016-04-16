package xdi2.transport.impl.udp;

import java.io.IOException;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.interceptor.InterceptorList;
import xdi2.transport.Transport;
import xdi2.transport.exceptions.Xdi2TransportException;
import xdi2.transport.impl.udp.interceptor.UDPTransportInterceptor;

public class InterceptorExecutor {

	private static final Logger log = LoggerFactory.getLogger(InterceptorExecutor.class);

	private InterceptorExecutor() {

	}

	/*
	 * Methods for executing interceptors
	 */

	public static boolean executeUDPTransportInterceptorsDatagram(InterceptorList<? extends Transport<?, ?>> interceptorList, UDPTransport udpTransport, UDPTransportRequest request, UDPTransportResponse response, MessagingTarget messagingTarget) throws Xdi2TransportException, IOException {

		for (Iterator<UDPTransportInterceptor> udpTransportInterceptors = findUDPTransportInterceptors(interceptorList); udpTransportInterceptors.hasNext(); ) {

			UDPTransportInterceptor udpTransportInterceptor = udpTransportInterceptors.next();

			if (udpTransportInterceptor.skip(null)) {

				if (log.isDebugEnabled()) log.debug("Skipping UDP transport interceptor " + udpTransportInterceptor.getClass().getSimpleName() + " (MESSAGE).");
				continue;
			}

			if (log.isDebugEnabled()) log.debug("Executing UDP transport interceptor " + udpTransportInterceptor.getClass().getSimpleName() + " (MESSAGE).");

			if (udpTransportInterceptor.processDatagram(udpTransport, request, response, messagingTarget)) {

				if (log.isDebugEnabled()) log.debug("DATAGRAM request has been fully handled by interceptor " + udpTransportInterceptor.getClass().getSimpleName() + ".");
				return true;
			}
		}

		return false;
	}

	/*
	 * Methods for finding interceptors
	 */

	public static Iterator<UDPTransportInterceptor> findUDPTransportInterceptors(InterceptorList<? extends Transport<?, ?>> interceptorList) {

		return interceptorList.findInterceptors(UDPTransportInterceptor.class);
	}
}
