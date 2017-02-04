package xdi2.messaging.container.interceptor.impl;

import xdi2.core.Graph;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIStatement;
import xdi2.messaging.container.MessagingContainer;
import xdi2.messaging.container.exceptions.Xdi2MessagingException;
import xdi2.messaging.container.execution.ExecutionContext;
import xdi2.messaging.container.interceptor.TargetInterceptor;
import xdi2.messaging.operations.Operation;

public abstract class AbstractTargetInterceptor extends AbstractInterceptor<MessagingContainer> implements TargetInterceptor {

	public AbstractTargetInterceptor(int initPriority, int shutdownPriority) {

		super(initPriority, shutdownPriority);
	}

	public AbstractTargetInterceptor() {

		super();
	}

	@Override
	public XDIStatement targetStatement(XDIStatement targetStatement, Operation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		return targetStatement;
	}

	@Override
	public XDIAddress targetAddress(XDIAddress targetAddress, Operation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		return targetAddress;
	}
}
