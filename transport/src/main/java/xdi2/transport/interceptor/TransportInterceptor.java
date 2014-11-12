package xdi2.transport.interceptor;

import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.response.MessagingResponse;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.execution.ExecutionContext;
import xdi2.messaging.target.interceptor.Interceptor;
import xdi2.transport.Transport;
import xdi2.transport.TransportRequest;
import xdi2.transport.TransportResponse;
import xdi2.transport.exceptions.Xdi2TransportException;

/**
 * Interceptor that is executed when a messaging request is executed.
 * 
 * @author markus
 */
public interface TransportInterceptor extends Interceptor<Transport<?, ?>> {

	/**
	 * Run before a messaging request is executed.
	 */
	public boolean before(Transport<?, ?> transport, TransportRequest request, TransportResponse response, MessagingTarget messagingTarget, MessageEnvelope messageEnvelope, ExecutionContext executionContext) throws Xdi2TransportException;

	/**
	 * Run after a messaging request is executed.
	 */
	public boolean after(Transport<?, ?> transport, TransportRequest request, TransportResponse response, MessagingTarget messagingTarget, MessageEnvelope messageEnvelope, MessagingResponse messagingResponse, ExecutionContext executionContext) throws Xdi2TransportException;
}
