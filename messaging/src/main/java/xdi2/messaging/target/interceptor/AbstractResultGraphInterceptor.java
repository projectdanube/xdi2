package xdi2.messaging.target.interceptor;

import xdi2.core.Graph;
import xdi2.messaging.context.ExecutionContext;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.MessagingTarget;

public abstract class AbstractResultGraphInterceptor extends AbstractInterceptor<MessagingTarget> implements ResultGraphInterceptor {

	@Override
	public void finish(Graph resultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

	}
}
