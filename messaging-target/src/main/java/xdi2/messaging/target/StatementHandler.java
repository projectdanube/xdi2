package xdi2.messaging.target;

import xdi2.core.Graph;
import xdi2.core.syntax.XDIStatement;
import xdi2.messaging.operations.Operation;
import xdi2.messaging.target.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.execution.ExecutionContext;

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
	 * @param targetStatement The target statement.
	 * @param operation The operation that is being executed.
	 * @param resultGraph The result graph.
	 * @param executionContext An "execution context" object for the entire XDI message envelope.
	 */
	public void executeOnStatement(XDIStatement targetStatement, Operation operation, Graph resultGraph, ExecutionContext executionContext) throws Xdi2MessagingException;
}
