package xdi2.messaging.target.interceptor;

import xdi2.core.Graph;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIStatement;
import xdi2.messaging.operations.Operation;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.execution.ExecutionContext;

public abstract class AbstractTargetInterceptor extends AbstractInterceptor<MessagingTarget> implements TargetInterceptor {

	@Override
	public XDIStatement targetStatement(XDIStatement targetStatement, Operation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		return targetStatement;
	}

	@Override
	public XDIAddress targetAddress(XDIAddress targetAddress, Operation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		return targetAddress;
	}
}
