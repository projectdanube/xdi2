package xdi2.messaging.target.interceptor;

import xdi2.core.ContextNode;
import xdi2.core.exceptions.Xdi2MessagingException;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import xdi2.messaging.target.ExecutionContext;

public interface ContextNodeInterceptor {

	/**
	 * Run before an operation is executed on a context node.
	 * @return true, if the context node has been fully handled.
	 */
	public boolean before(ContextNode contextNode, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException;

	/**
	 * Run after an operation is executed on a context node.
	 * @return true, if the context node has been fully handled.
	 */
	public boolean after(ContextNode contextNode, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException;
}
