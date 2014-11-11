package xdi2.transport.impl;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.messaging.context.ExecutionContext;
import xdi2.messaging.request.MessagingRequest;
import xdi2.messaging.response.MessagingResponse;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.interceptor.InterceptorList;
import xdi2.transport.Transport;
import xdi2.transport.TransportRequest;
import xdi2.transport.TransportResponse;
import xdi2.transport.exceptions.Xdi2TransportException;
import xdi2.transport.interceptor.TransportInterceptor;

public class InterceptorExecutor {

	private static final Logger log = LoggerFactory.getLogger(InterceptorExecutor.class);

	private InterceptorExecutor() {

	}

	/*
	 * Methods for executing interceptors
	 */

	public static boolean executeTransportInterceptorsBefore(InterceptorList<Transport<?, ?>> interceptorList, Transport<?, ?> transport, TransportRequest request, TransportResponse response, MessagingTarget messagingTarget, MessagingRequest messagingRequest, ExecutionContext executionContext) throws Xdi2TransportException {

		for (Iterator<TransportInterceptor> transportInterceptors = findTransportInterceptors(interceptorList); transportInterceptors.hasNext(); ) {

			TransportInterceptor transportInterceptor = transportInterceptors.next();

			if (log.isDebugEnabled()) log.debug("Executing transport interceptor " + transportInterceptor.getClass().getSimpleName() + " (before).");

			if (transportInterceptor.before(transport, request, response, messagingTarget, messagingRequest, executionContext)) {

				if (log.isDebugEnabled()) log.debug("Request has been fully handled by interceptor " + transportInterceptor.getClass().getSimpleName() + ".");
				return true;
			}
		}

		return false;
	}

	public static boolean executeTransportInterceptorsAfter(InterceptorList<Transport<?, ?>> interceptorList, Transport<?, ?> transport, TransportRequest request, TransportResponse response, MessagingTarget messagingTarget, MessagingRequest messagingRequest, MessagingResponse messagingResponse, ExecutionContext executionContext) throws Xdi2TransportException {

		for (Iterator<TransportInterceptor> transportInterceptors = findTransportInterceptors(interceptorList); transportInterceptors.hasNext(); ) {

			TransportInterceptor transportInterceptor = transportInterceptors.next();

			if (log.isDebugEnabled()) log.debug("Executing transport interceptor " + transportInterceptor.getClass().getSimpleName() + " (after).");

			if (transportInterceptor.after(transport, request, response, messagingTarget, messagingRequest, messagingResponse, executionContext)) {

				if (log.isDebugEnabled()) log.debug("Request has been fully handled by interceptor " + transportInterceptor.getClass().getSimpleName() + ".");
				return true;
			}
		}

		return false;
	}

	/*
	 * Methods for finding interceptors
	 */

	public static Iterator<TransportInterceptor> findTransportInterceptors(InterceptorList<Transport<?, ?>> interceptorList) {

		return interceptorList.findInterceptors(TransportInterceptor.class);
	}
}
