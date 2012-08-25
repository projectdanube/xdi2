package xdi2.messaging.target.interceptor;

import xdi2.core.Statement;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.ExecutionContext;

public abstract class AbstractOperationInterceptor extends AbstractInterceptor implements OperationInterceptor {

	@Override
	public boolean before(Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	@Override
	public boolean after(Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	@Override
	public MessageEnvelope feedback(Operation operation, Statement statement, ExecutionContext executionContext) throws Xdi2MessagingException {

		return null;
	}
}
