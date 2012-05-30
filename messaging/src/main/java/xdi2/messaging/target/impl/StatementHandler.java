package xdi2.messaging.target.impl;

import xdi2.core.Statement;
import xdi2.core.exceptions.Xdi2MessagingException;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import xdi2.messaging.target.ExecutionContext;

/**
 * A StatementHandler can execute an XDI operation against a statement given in a cross-reference.
 * 
 * The AbstractMessagingTarget requests StatementHandler implementations for each
 * statement given as part of an operation.
 * 
 * @author markus
 */
public interface StatementHandler {

	/**
	 * Executes an XDI operation on a statement.
	 * @param targetContextNode The context node.
	 * @param operation The operation that is being executed.
	 * @param messageResult The message result to fill.
	 * @param executionContext An "execution context" object for the entire XDI message envelope.
	 * @return True, if the operation has been handled.
	 */
	public boolean executeOnStatement(Statement statement, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException;
}
