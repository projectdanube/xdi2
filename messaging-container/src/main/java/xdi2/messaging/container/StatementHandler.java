package xdi2.messaging.container;

import xdi2.core.Graph;
import xdi2.core.syntax.XDIStatement;
import xdi2.messaging.container.exceptions.Xdi2MessagingException;
import xdi2.messaging.container.execution.ExecutionContext;
import xdi2.messaging.operations.Operation;

/**
 * A StatementHandler can execute an XDI operation against a statement given in a cross-reference.
 * 
 * The AbstractMessagingContainer requests StatementHandler implementations for each
 * statement given as part of an operation.
 * 
 * @author markus
 */
public interface StatementHandler {

	/**
	 * Executes an XDI operation on a statement.
	 * @param targetStatement The target statement.
	 * @param operation The operation that is being executed.
	 * @param operationResultGraph The result graph.
	 * @param executionContext An "execution context" object for the entire XDI message envelope.
	 */
	public void executeOnStatement(XDIStatement targetStatement, Operation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException;
}
