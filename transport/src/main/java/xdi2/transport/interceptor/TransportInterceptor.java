package xdi2.transport.interceptor;

import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;
import xdi2.messaging.context.ExecutionContext;
import xdi2.messaging.error.ErrorMessageResult;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.interceptor.Interceptor;
import xdi2.transport.Request;
import xdi2.transport.Response;
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
	public boolean before(Transport<?, ?> transport, Request request, Response response, MessagingTarget messagingTarget, MessageEnvelope messageEnvelope, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2TransportException;

	/**
	 * Run after a message envelope is executed.
	 */
	public boolean after(Transport<?, ?> transport, Request request, Response response, MessagingTarget messagingTarget, MessageEnvelope messageEnvelope, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2TransportException;

	/**
	 * Run if an exception occurs while a message envelope is executed.
	 */
	public void exception(Transport<?, ?> transport, Request request, Response response, MessagingTarget messagingTarget, MessageEnvelope messageEnvelope, ErrorMessageResult errorMessageResult, ExecutionContext executionContext, Exception ex);
}
