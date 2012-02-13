package xdi2.messaging.target.impl;

import xdi2.exceptions.Xdi2MessagingException;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;

/**
 * An OperationHandler can execute a whole XDI operation in a single step.
 * 
 * The OperationMessagingTarget tries to find a matching OperationHandler for an
 * incoming XDI operation.
 * 
 * @author markus
 */
public interface OperationHandler {

	/**
	 * Executes an XDI operation.
	 * @param operation The operation to execute.
	 * @param messageResult The message result to fill.
	 * @param executionContext An "execution context" object that is created when
	 * execution of the message envelope begins and that will be passed into every 
	 * single execute() method. Override AbstractMessagingTarget.newExecutionContext() 
	 * if you need this.
	 * @return True, if the operation has been handled.
	 */
	public boolean execute(Operation operation, MessageResult messageResult, Object executionContext) throws Xdi2MessagingException;
}
