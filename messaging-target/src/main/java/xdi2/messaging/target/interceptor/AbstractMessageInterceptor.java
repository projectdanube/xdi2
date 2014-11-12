package xdi2.messaging.target.interceptor;

import xdi2.messaging.Message;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.execution.ExecutionContext;
import xdi2.messaging.target.execution.ExecutionResult;

public abstract class AbstractMessageInterceptor extends AbstractInterceptor<MessagingTarget> implements MessageInterceptor {

	@Override
	public InterceptorResult before(Message message, ExecutionResult executionResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return InterceptorResult.DEFAULT;
	}

	@Override
	public InterceptorResult after(Message message, ExecutionResult executionResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return InterceptorResult.DEFAULT;
	}
}
