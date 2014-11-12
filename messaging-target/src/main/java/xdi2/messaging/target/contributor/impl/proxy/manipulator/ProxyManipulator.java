package xdi2.messaging.target.contributor.impl.proxy.manipulator;

import xdi2.core.Graph;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.execution.ExecutionContext;

/**
 * This is used to manipulate message envelopes before they are forwarded
 * by the XdiContributor, and message result after they are received by the
 * XdiContributor.
 * 
 * @author markus
 */
public interface ProxyManipulator {

	/*
	 * Init and shutdown
	 */

	public void init(MessagingTarget messagingTarget) throws Exception;
	public void shutdown(MessagingTarget messagingTarget) throws Exception;

	/**
	 * Manipulate a message envelope.
	 * @param messageEnvelope The message envelope to manipulate.
	 * @param executionContext The current execution context.
	 */
	public void manipulate(MessageEnvelope messageEnvelope, ExecutionContext executionContext) throws Xdi2MessagingException;

	/**
	 * Manipulate a message result.
	 * @param messageResult The message result to manipulate.
	 * @param executionContext The current execution context.
	 */
	public void manipulate(Graph resultGraph, ExecutionContext executionContext) throws Xdi2MessagingException;
}
