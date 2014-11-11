package xdi2.messaging.target.interceptor;

import xdi2.core.Graph;
import xdi2.messaging.context.ExecutionContext;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.request.RequestMessageEnvelope;
import xdi2.messaging.target.MessagingTarget;

public abstract class AbstractMessageEnvelopeInterceptor extends AbstractInterceptor<MessagingTarget> implements MessageEnvelopeInterceptor {

	@Override
	public InterceptorResult before(RequestMessageEnvelope messageEnvelope, Graph resultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		return InterceptorResult.DEFAULT;
	}

	@Override
	public InterceptorResult after(RequestMessageEnvelope messageEnvelope, Graph resultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		return InterceptorResult.DEFAULT;
	}

	@Override
	public void exception(RequestMessageEnvelope messageEnvelope, Graph resultGraph, ExecutionContext executionContext, Exception ex) {

	}
}
