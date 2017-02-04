package xdi2.messaging.container.interceptor.impl;

import xdi2.messaging.container.MessagingContainer;
import xdi2.messaging.container.exceptions.Xdi2MessagingException;
import xdi2.messaging.container.execution.ExecutionContext;
import xdi2.messaging.container.execution.ExecutionResult;
import xdi2.messaging.container.interceptor.ExecutionResultInterceptor;

public abstract class AbstractExecutionResultInterceptor extends AbstractInterceptor<MessagingContainer> implements ExecutionResultInterceptor {

	public AbstractExecutionResultInterceptor(int initPriority, int shutdownPriority) {

		super(initPriority, shutdownPriority);
	}

	public AbstractExecutionResultInterceptor() {

		super();
	}

	@Override
	public void finish(MessagingContainer messagingContainer, ExecutionContext executionContext, ExecutionResult executionResult) throws Xdi2MessagingException {

	}
}
