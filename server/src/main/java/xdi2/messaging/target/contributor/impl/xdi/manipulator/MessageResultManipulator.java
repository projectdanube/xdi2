package xdi2.messaging.target.contributor.impl.xdi.manipulator;

import xdi2.messaging.MessageResult;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.ExecutionContext;
import xdi2.messaging.target.MessagingTarget;

/**
 * This is used to manipulate message results after they are received
 * from another XDI endpoint by the XdiContributor.
 * 
 * @author markus
 */
public interface MessageResultManipulator {

	/*
	 * Init and shutdown
	 */

	public void init(MessagingTarget messagingTarget) throws Exception;
	public void shutdown(MessagingTarget messagingTarget) throws Exception;
	
	/**
	 * Manipulate a message result.
	 * @param messageResult The message result to manipulate.
	 * @param executionContext The current execution context.
	 */
	public void manipulate(MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException;
}
