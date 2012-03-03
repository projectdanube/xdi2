package xdi2.messaging.target.interceptor;

import xdi2.core.ContextNode;
import xdi2.core.exceptions.Xdi2MessagingException;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import xdi2.messaging.target.ExecutionContext;

public abstract class AbstractContextNodeInterceptor implements ContextNodeInterceptor {

	@Override
	public boolean before(ContextNode contextNode, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	@Override
	public boolean after(ContextNode contextNode, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}
}
