package xdi2.messaging.target.interceptor;

import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import xdi2.messaging.context.ExecutionContext;
import xdi2.messaging.exceptions.Xdi2MessagingException;

public abstract class AbstractOperationInterceptor extends AbstractInterceptor implements OperationInterceptor {

	@Override
	public boolean before(Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	@Override
	public boolean after(Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}
}
