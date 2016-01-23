package xdi2.messaging.target.interceptor.impl;

import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.client.impl.ManipulationContext;
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
	public InterceptorResult before(MessageEnvelope messageEnvelope, ExecutionContext executionContext, ExecutionResult executionResult) throws Xdi2MessagingException {

		// create manipulation context

		ManipulationContext manipulationContext = this.createManipulationContext(executionContext);
		putManipulationContext(executionContext, manipulationContext);

		// manipulate

		try {

			ManipulatorExecutor.executeMessageEnvelopeManipulators(this.getManipulators(), messageEnvelope, manipulationContext);
		} catch (Xdi2ClientException ex) {

			throw new Xdi2MessagingException("Unable to manipulate message envelope: " + ex.getMessage(), ex, executionContext);
		}

		// done

		return InterceptorResult.DEFAULT;
	}

	@Override
	public InterceptorResult after(MessageEnvelope messageEnvelope, ExecutionContext executionContext, ExecutionResult executionResult) throws Xdi2MessagingException {

		return InterceptorResult.DEFAULT;
	}

	@Override
	public void exception(MessageEnvelope messageEnvelope, ExecutionContext executionContext, ExecutionResult executionResult, Exception ex) {

	}

	/*
	 * MessageInterceptor
	 */

	@Override
	public InterceptorResult before(Message message, ExecutionContext executionContext, ExecutionResult executionResult) throws Xdi2MessagingException {

		// get manipulation context

		ManipulationContext manipulationContext = getManipulationContext(executionContext);

		// manipulate

		try {

			ManipulatorExecutor.executeMessageManipulators(this.getManipulators(), message, manipulationContext);
		} catch (Xdi2ClientException ex) {

			throw new Xdi2MessagingException("Unable to manipulate message: " + ex.getMessage(), ex, executionContext);
		}

		// done

		return InterceptorResult.DEFAULT;
	}

	@Override
	public InterceptorResult after(Message message, ExecutionContext executionContext, ExecutionResult executionResult) throws Xdi2MessagingException {

		return InterceptorResult.DEFAULT;
	}

	/*
	 * Helper methods
	 */

	public ManipulationContext createManipulationContext(ExecutionContext executionContext) {

		ManipulationContext manipulationContext = ManipulationContext.createManipulationContext();

		putManipulatingInterceptor(manipulationContext, this);
		putExecutionContext(manipulationContext, executionContext);

		return manipulationContext;
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

	/*
	 * ExecutionContext and ManipulationContext helper methods
	 */

	private static final String EXECUTIONCONTEXT_KEY_MANIPULATIONCONTEXT_PER_MESSAGEENVELOPE = ManipulatingInterceptor.class.getCanonicalName() + "#manipulationcontextpermessageenvelope";
	private static final String MANIPULATIONCONTEXT_KEY_MANIPULATINGINTERCEPTOR = ManipulatingInterceptor.class.getCanonicalName() + "#manipulatinginterceptor";
	private static final String MANIPULATIONCONTEXT_KEY_EXECUTIONCONTEXT = ManipulatingInterceptor.class.getCanonicalName() + "#executioncontext";

	public static ManipulationContext getManipulationContext(ExecutionContext executionContext) {

		return (ManipulationContext) executionContext.getMessageEnvelopeAttribute(EXECUTIONCONTEXT_KEY_MANIPULATIONCONTEXT_PER_MESSAGEENVELOPE);
	}

	public static void putManipulationContext(ExecutionContext executionContext, ManipulationContext manipulationContext) {

		executionContext.putMessageAttribute(EXECUTIONCONTEXT_KEY_MANIPULATIONCONTEXT_PER_MESSAGEENVELOPE, manipulationContext);
	}

	public static ManipulatingInterceptor getManipulatingInterceptor(ManipulationContext manipulationContext) {

		return (ManipulatingInterceptor) manipulationContext.getManipulationContextAttribute(MANIPULATIONCONTEXT_KEY_MANIPULATINGINTERCEPTOR);
	}

	public static void putManipulatingInterceptor(ManipulationContext manipulationContext, ManipulatingInterceptor manipulatingInterceptor) {

		manipulationContext.putManipulationContextAttribute(MANIPULATIONCONTEXT_KEY_MANIPULATINGINTERCEPTOR, manipulatingInterceptor);
	}

	public static ExecutionContext getExecutionContext(ManipulationContext manipulationContext) {

		return (ExecutionContext) manipulationContext.getManipulationContextAttribute(MANIPULATIONCONTEXT_KEY_EXECUTIONCONTEXT);
	}

	public static void putExecutionContext(ManipulationContext manipulationContext, ExecutionContext executionContext) {

		manipulationContext.putManipulationContextAttribute(MANIPULATIONCONTEXT_KEY_EXECUTIONCONTEXT, executionContext);
	}
}
