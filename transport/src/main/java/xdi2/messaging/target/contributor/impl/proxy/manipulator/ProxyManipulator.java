package xdi2.messaging.target.contributor.impl.proxy.manipulator;

import xdi2.core.Graph;
import xdi2.messaging.context.ExecutionContext;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.request.RequestMessageEnvelope;
import xdi2.messaging.target.MessagingTarget;

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
	public void manipulate(RequestMessageEnvelope messageEnvelope, ExecutionContext executionContext) throws Xdi2MessagingException;

	/**
	 * Manipulate a message result.
	 * @param messageResult The message result to manipulate.
	 * @param executionContext The current execution context.
	 */
	public void manipulate(Graph resultGraph, ExecutionContext executionContext) throws Xdi2MessagingException;
}
