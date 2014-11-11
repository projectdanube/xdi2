package xdi2.messaging.target.interceptor;

import xdi2.core.Graph;
import xdi2.messaging.context.ExecutionContext;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.request.RequestMessage;
import xdi2.messaging.target.MessagingTarget;

public abstract class AbstractMessageInterceptor extends AbstractInterceptor<MessagingTarget> implements MessageInterceptor {

	@Override
	public InterceptorResult before(RequestMessage message, Graph resultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		return InterceptorResult.DEFAULT;
	}

	@Override
	public InterceptorResult after(RequestMessage message, Graph resultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		return InterceptorResult.DEFAULT;
	}
}
