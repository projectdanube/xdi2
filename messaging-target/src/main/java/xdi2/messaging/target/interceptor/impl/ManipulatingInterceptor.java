package xdi2.messaging.target.interceptor.impl;

import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.client.impl.ManipulatorExecutor;
import xdi2.client.impl.ManipulatorList;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.Prototype;
import xdi2.messaging.target.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.execution.ExecutionContext;
import xdi2.messaging.target.execution.ExecutionResult;
import xdi2.messaging.target.interceptor.InterceptorResult;
import xdi2.messaging.target.interceptor.MessageEnvelopeInterceptor;
import xdi2.messaging.target.interceptor.MessageInterceptor;

/**
 * This uses a list of manipulators that can manipulate an incoming message envelope or message.
 * 
 * @author markus
 */
public class ManipulatingInterceptor extends AbstractInterceptor<MessagingTarget> implements MessageEnvelopeInterceptor, MessageInterceptor, Prototype<ManipulatingInterceptor> {

	private ManipulatorList manipulators;

	public ManipulatingInterceptor() {

	}

	/*
	 * Prototype
	 */

	@Override
	public ManipulatingInterceptor instanceFor(PrototypingContext prototypingContext) {

		// done

		return this;
	}

	/*
	 * MessageEnvelopeInterceptor
	 */

	@Override
	public InterceptorResult before(MessageEnvelope messageEnvelope, ExecutionResult executionResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		try {

			ManipulatorExecutor.executeMessageEnvelopeManipulators(this.getManipulators(), messageEnvelope);
		} catch (Xdi2ClientException ex) {

			throw new Xdi2MessagingException("Unable to manipulate message envelope: " + ex.getMessage(), ex, executionContext);
		}

		return InterceptorResult.DEFAULT;
	}

	@Override
	public InterceptorResult after(MessageEnvelope messageEnvelope, ExecutionResult executionResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return InterceptorResult.DEFAULT;
	}

	@Override
	public void exception(MessageEnvelope messageEnvelope, ExecutionResult executionResult, ExecutionContext executionContext, Exception ex) {

	}

	/*
	 * MessageInterceptor
	 */

	@Override
	public InterceptorResult before(Message message, ExecutionResult executionResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		try {

			ManipulatorExecutor.executeMessageManipulators(this.getManipulators(), message);
		} catch (Xdi2ClientException ex) {

			throw new Xdi2MessagingException("Unable to manipulate message: " + ex.getMessage(), ex, executionContext);
		}

		return InterceptorResult.DEFAULT;
	}

	@Override
	public InterceptorResult after(Message message, ExecutionResult executionResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return InterceptorResult.DEFAULT;
	}

	/*
	 * Getters and setters
	 */

	public ManipulatorList getManipulators() {

		return this.manipulators;
	}

	public void setManipulators(ManipulatorList manipulators) {

		this.manipulators = manipulators;
	}
}
