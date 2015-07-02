package xdi2.messaging.target.interceptor;

import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.execution.ExecutionContext;
import xdi2.messaging.target.execution.ExecutionResult;

public abstract class AbstractMessageEnvelopeInterceptor extends AbstractInterceptor<MessagingTarget> implements MessageEnvelopeInterceptor {

	@Override
	public InterceptorResult before(MessageEnvelope messageEnvelope, ExecutionResult executionResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return InterceptorResult.DEFAULT;
	}

	@Override
	public InterceptorResult after(MessageEnvelope messageEnvelope, ExecutionResult executionResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return InterceptorResult.DEFAULT;
	}

	@Override
	public void exception(MessageEnvelope messageEnvelope, ExecutionResult executionResult, ExecutionContext executionContext, Exception ex) {

	}
}
