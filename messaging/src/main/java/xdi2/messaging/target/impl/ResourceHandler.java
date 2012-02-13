package xdi2.messaging.target.impl;

import xdi2.exceptions.Xdi2MessagingException;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import xdi2.messaging.target.ExecutionContext;

/**
 * A ResourceHandler can execute an XDI operation against a resource identified by
 * a single statement in the operation graph.
 * 
 * The ResourceMessagingTarget requests ResourceHandler implementations for each
 * statement in the operation graph.
 * 
 * @author markus
 */
public interface ResourceHandler {

	/**
	 * Executes an XDI operation against this resource.
	 * @param operation The operation that is being executed.
	 * @param messageResult The message result to fill.
	 * @param executionContext An "execution context" object that is created when
	 * execution of the message envelope begins and that will be passed into every 
	 * single execute() method. Override AbstractMessagingTarget.newExecutionContext() 
	 * if you need this.
	 * @return True, if the message envelope has been handled.
	 */
	public boolean execute(Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException;
}
