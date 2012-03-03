package xdi2.messaging.target.impl;

import xdi2.core.Literal;
import xdi2.core.Relation;
import xdi2.core.exceptions.Xdi2MessagingException;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import xdi2.messaging.target.ExecutionContext;

/**
 * A ContextHandler can execute an XDI operation against a context of the
 * operation graph.
 * 
 * The ContextMessagingTarget requests ContextHandler implementations for each
 * context in the operation graph.
 * 
 * @author markus
 */
public interface ContextNodeHandler {

	/**
	 * Executes an XDI operation against this context.
	 * @param operation The operation that is being executed.
	 * @param messageResult The message result to fill.
	 * @param executionContext An "execution context" object for the entire XDI message envelope.
	 * @return True, if the operation has been handled.
	 */
	public boolean executeContextNode(Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException;

	/**
	 * Executes an XDI operation against a relation in this context.
	 * @param relation The relation in this context.
	 * @param operation The operation that is being executed.
	 * @param messageResult The message result to fill.
	 * @param executionContext An "execution context" object for the entire XDI message envelope.
	 * @return True, if the operation has been handled.
	 */
	public boolean executeRelation(Relation relation, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException;

	/**
	 * Executes an XDI operation against a literal in this context.
	 * @param literal The literal in this context.
	 * @param operation The operation that is being executed.
	 * @param messageResult The message result to fill.
	 * @param executionContext An "execution context" object for the entire XDI message envelope.
	 * @return True, if the operation has been handled.
	 */
	public boolean executeLiteral(Literal literal, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException;
}
