package xdi2.messaging.container.interceptor.impl;

import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.messaging.Message;
import xdi2.messaging.container.MessagingContainer;
import xdi2.messaging.container.Prototype;
import xdi2.messaging.container.exceptions.Xdi2MessagingException;
import xdi2.messaging.container.execution.ExecutionContext;
import xdi2.messaging.container.execution.ExecutionResult;
import xdi2.messaging.container.interceptor.InterceptorResult;
import xdi2.messaging.container.interceptor.MessageInterceptor;

/**
 * This interceptor checks if the source peer root of a message matches the sender of the message.
 * 
 * @author markus
 */
public class FromInterceptor extends AbstractInterceptor<MessagingContainer> implements MessageInterceptor, Prototype<FromInterceptor> {

	/*
	 * Prototype
	 */

	@Override
	public FromInterceptor instanceFor(PrototypingContext prototypingContext) {

		// done

		return this;
	}

	/*
	 * MessageInterceptor
	 */

	@Override
	public InterceptorResult before(Message message, ExecutionContext executionContext, ExecutionResult executionResult) throws Xdi2MessagingException {

		throw new Xdi2RuntimeException("Not implemented.");
	}

	@Override
	public InterceptorResult after(Message message, ExecutionContext executionContext, ExecutionResult executionResult) throws Xdi2MessagingException {

		return InterceptorResult.DEFAULT;
	}
}
