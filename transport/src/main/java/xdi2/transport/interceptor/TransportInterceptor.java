package xdi2.transport.interceptor;

import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;
import xdi2.messaging.context.ExecutionContext;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.interceptor.Interceptor;
import xdi2.transport.Transport;
import xdi2.transport.exceptions.Xdi2TransportException;

/**
 * Interceptor that is executed when a message envelope is executed.
 * 
 * @author markus
 */
public interface TransportInterceptor extends Interceptor<Transport<?, ?>> {

	/**
	 * Run after a message envelope is executed.
	 */
	public boolean before(Transport<?, ?> transport, MessagingTarget messagingTarget, MessageEnvelope messageEnvelope, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2TransportException;

	/**
	 * Run after a message envelope is executed.
	 */
	public boolean after(Transport<?, ?> transport, MessagingTarget messagingTarget, MessageEnvelope messageEnvelope, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2TransportException;

	/**
	 * Run if an exception occurs while a message envelope is executed.
	 */
	public void exception(Transport<?, ?> transport, MessagingTarget messagingTarget, MessageEnvelope messageEnvelope, MessageResult messageResult, ExecutionContext executionContext, Exception ex);
}
