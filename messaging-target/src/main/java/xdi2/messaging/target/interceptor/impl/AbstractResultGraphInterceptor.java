package xdi2.messaging.target.interceptor.impl;

import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.execution.ExecutionContext;
import xdi2.messaging.target.execution.ExecutionResult;
import xdi2.messaging.target.interceptor.ResultGraphInterceptor;

public abstract class AbstractResultGraphInterceptor extends AbstractInterceptor<MessagingTarget> implements ResultGraphInterceptor {

	@Override
	public void finish(ExecutionResult executionResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}
}
