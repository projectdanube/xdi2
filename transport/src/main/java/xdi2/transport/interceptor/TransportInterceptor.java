package xdi2.transport.interceptor;

import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.response.MessagingResponse;
import xdi2.messaging.container.MessagingContainer;
import xdi2.messaging.container.execution.ExecutionContext;
import xdi2.messaging.container.interceptor.Interceptor;
import xdi2.transport.Transport;
import xdi2.transport.TransportRequest;
import xdi2.transport.TransportResponse;
import xdi2.transport.exceptions.Xdi2TransportException;

/**
 * Interceptor that is executed when a message envelope is executed.
 * 
 * @author markus
 */
public interface TransportInterceptor extends Interceptor<Transport<?, ?>> {

	/**
	 * Run before a message envelope is executed.
	 */
	public boolean before(Transport<?, ?> transport, TransportRequest request, TransportResponse response, MessagingContainer messagingContainer, MessageEnvelope messageEnvelope, ExecutionContext executionContext) throws Xdi2TransportException;

	/**
	 * Run after a message envelope is executed.
	 */
	public boolean after(Transport<?, ?> transport, TransportRequest request, TransportResponse response, MessagingContainer messagingContainer, MessageEnvelope messageEnvelope, MessagingResponse messagingResponse, ExecutionContext executionContext) throws Xdi2TransportException;

	/**
	 * Run if an exception occurs while a message envelope is executed.
	 */
	public void exception(Transport<?, ?> transport, TransportRequest request, TransportResponse response, MessagingContainer messagingContainer, MessageEnvelope messageEnvelope, MessagingResponse messagingResponse, Exception ex, ExecutionContext executionContext);
}
