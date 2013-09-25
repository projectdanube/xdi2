package xdi2.messaging.target.interceptor.impl;

import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.messaging.Message;
import xdi2.messaging.MessageResult;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.ExecutionContext;
import xdi2.messaging.target.Prototype;
import xdi2.messaging.target.interceptor.AbstractInterceptor;
import xdi2.messaging.target.interceptor.MessageInterceptor;

/**
 * This interceptor checks if the source peer root address of a message matches the sender of the message.
 * 
 * @author markus
 */
public class FromInterceptor extends AbstractInterceptor implements MessageInterceptor, Prototype<FromInterceptor> {

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
	public boolean before(Message message, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		throw new Xdi2RuntimeException("FromInterceptor not implemented yet.");
	}

	@Override
	public boolean after(Message message, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}
}
