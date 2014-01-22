package xdi2.messaging.target.interceptor;

import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import xdi2.messaging.context.ExecutionContext;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.MessagingTarget;

public abstract class AbstractOperationInterceptor extends AbstractInterceptor<MessagingTarget> implements OperationInterceptor {

	@Override
	public InterceptorResult before(Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return InterceptorResult.DEFAULT;
	}

	@Override
	public InterceptorResult after(Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return InterceptorResult.DEFAULT;
	}
}
