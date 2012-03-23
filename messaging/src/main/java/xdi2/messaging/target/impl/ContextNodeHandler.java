package xdi2.messaging.target.impl;

import xdi2.core.ContextNode;
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
	 * Executes an XDI operation on a context node.
	 * @param operationContextNode The context node.
	 * @param operation The operation that is being executed.
	 * @param messageResult The message result to fill.
	 * @param executionContext An "execution context" object for the entire XDI message envelope.
	 * @return True, if the operation has been handled.
	 */
	public boolean executeOnContextNode(ContextNode contextNode, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException;

	/**
	 * Executes an XDI operation on a relation of a context node.
	 * @param operationContextNode The context node.
	 * @param operationRelation The relation of the context node.
	 * @param operation The operation that is being executed.
	 * @param messageResult The message result to fill.
	 * @param executionContext An "execution context" object for the entire XDI message envelope.
	 * @return True, if the operation has been handled.
	 */
	public boolean executeOnRelation(ContextNode operationContextNode, Relation operationRelation, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException;

	/**
	 * Executes an XDI operation on a literal of a context node.
	 * @param operationContextNode The context node.
	 * @param operationLiteral The literal of the context node.
	 * @param operation The operation that is being executed.
	 * @param messageResult The message result to fill.
	 * @param executionContext An "execution context" object for the entire XDI message envelope.
	 * @return True, if the operation has been handled.
	 */
	public boolean executeOnLiteral(ContextNode operationContextNode, Literal operationLiteral, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException;
}
