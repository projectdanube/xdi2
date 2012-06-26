package xdi2.messaging.target.interceptor;

import xdi2.core.exceptions.Xdi2MessagingException;
import xdi2.messaging.Message;
import xdi2.messaging.MessageResult;
import xdi2.messaging.target.ExecutionContext;

public interface MessageInterceptor extends Interceptor {

	/**
	 * Run before a message is executed.
	 * @param message The message to process.
	 * @param messageResult The message result.
	 * @param executionContext The current execution context.
	 * @return true, if the message has been fully handled.
	 */
	public boolean before(Message message, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException;

	/**
	 * Run after a message is executed.
	 * @param message The message to process.
	 * @param messageResult The message result.
	 * @param executionContext The current execution context.
	 * @return true, if the message has been fully handled.
	 */
	public boolean after(Message message, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException;
}
