package xdi2.messaging.target.interceptor;

import xdi2.core.Statement;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.messaging.Operation;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.ExecutionContext;

public abstract class AbstractTargetInterceptor implements TargetInterceptor {

	@Override
	public Statement targetStatement(Operation operation, Statement targetStatement, ExecutionContext executionContext) throws Xdi2MessagingException {

		return targetStatement;
	}

	@Override
	public XRI3Segment targetAddress(Operation operation, XRI3Segment targetAddress, ExecutionContext executionContext) throws Xdi2MessagingException {

		return targetAddress;
	}
}
