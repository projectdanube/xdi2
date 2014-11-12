package xdi2.messaging.target.interceptor;

import xdi2.core.Graph;
import xdi2.messaging.Message;
import xdi2.messaging.context.ExecutionContext;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.MessagingTarget;

public abstract class AbstractMessageInterceptor extends AbstractInterceptor<MessagingTarget> implements MessageInterceptor {

	@Override
	public InterceptorResult before(Message message, Graph resultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		return InterceptorResult.DEFAULT;
	}

	@Override
	public InterceptorResult after(Message message, Graph resultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		return InterceptorResult.DEFAULT;
	}
}
