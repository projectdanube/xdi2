package xdi2.transport.interceptor.impl;

import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;
import xdi2.messaging.context.ExecutionContext;
import xdi2.messaging.error.ErrorMessageResult;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.interceptor.AbstractInterceptor;
import xdi2.transport.Request;
import xdi2.transport.Response;
import xdi2.transport.Transport;
import xdi2.transport.exceptions.Xdi2TransportException;
import xdi2.transport.interceptor.TransportInterceptor;

public abstract class AbstractTransportInterceptor extends AbstractInterceptor<Transport<?, ?>> implements TransportInterceptor {

	@Override
	public boolean before(Transport<?, ?> transport, Request request, Response response, MessagingTarget messagingTarget, MessageEnvelope messageEnvelope, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2TransportException {

		return false;
	}

	@Override
	public boolean after(Transport<?, ?> transport, Request request, Response response, MessagingTarget messagingTarget, MessageEnvelope messageEnvelope, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2TransportException {

		return false;
	}

	@Override
	public void exception(Transport<?, ?> transport, Request request, Response response, MessagingTarget messagingTarget, MessageEnvelope messageEnvelope, ErrorMessageResult errorMessageResult, ExecutionContext executionContext, Exception ex) {

	}
}
