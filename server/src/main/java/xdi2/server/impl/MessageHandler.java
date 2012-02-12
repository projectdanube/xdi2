package xdi2.server.impl;

import xdi2.exceptions.Xdi2MessagingException;
import xdi2.messaging.Message;
import xdi2.messaging.MessageResult;

/**
 * A MessageHandler can execute a whole XDI message in a single step.
 * 
 * The MessageMessagingTarget tries to find a matching MessageHandler for an
 * incoming XDI message.
 * 
 * @author markus
 */
public interface MessageHandler {

	/**
	 * Executes an XDI message.
	 * @param message The message to execute.
	 * @param messageResult The message result to fill.
	 * @param executionContext An "execution context" object that is created when
	 * execution of the message envelope begins and that will be passed into every 
	 * single execute() method. Override AbstractMessagingTarget.newExecutionContext() 
	 * if you need this.
	 * @return True, if the message has been handled.
	 */
	public boolean execute(Message message, MessageResult messageResult, Object executionContext) throws Xdi2MessagingException;
}
