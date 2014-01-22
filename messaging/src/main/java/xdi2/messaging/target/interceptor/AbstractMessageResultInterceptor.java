package xdi2.messaging.target.interceptor;

import xdi2.messaging.MessageResult;
import xdi2.messaging.context.ExecutionContext;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.MessagingTarget;

public abstract class AbstractMessageResultInterceptor extends AbstractInterceptor<MessagingTarget> implements MessageResultInterceptor {

	@Override
	public void finish(MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}
}
