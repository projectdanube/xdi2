package xdi2.messaging.target.interceptor.impl;

import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.execution.ExecutionContext;
import xdi2.messaging.target.execution.ExecutionResult;
import xdi2.messaging.target.interceptor.InterceptorResult;
import xdi2.messaging.target.interceptor.MessageEnvelopeInterceptor;

public abstract class AbstractMessageEnvelopeInterceptor extends AbstractInterceptor<MessagingTarget> implements MessageEnvelopeInterceptor {

	public AbstractMessageEnvelopeInterceptor(int initPriority, int shutdownPriority) {

		super(initPriority, shutdownPriority);
	}

	public AbstractMessageEnvelopeInterceptor() {

		super();
	}

	@Override
	public InterceptorResult before(MessageEnvelope messageEnvelope, ExecutionContext executionContext, ExecutionResult executionResult) throws Xdi2MessagingException {

		return InterceptorResult.DEFAULT;
	}

	@Override
	public InterceptorResult after(MessageEnvelope messageEnvelope, ExecutionContext executionContext, ExecutionResult executionResult) throws Xdi2MessagingException {

		return InterceptorResult.DEFAULT;
	}

	@Override
	public void exception(MessageEnvelope messageEnvelope, ExecutionContext executionContext, ExecutionResult executionResult, Exception ex) {

	}
}
