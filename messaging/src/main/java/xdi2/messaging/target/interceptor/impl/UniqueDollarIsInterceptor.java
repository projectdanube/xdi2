package xdi2.messaging.target.interceptor.impl;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Statement;
import xdi2.core.Statement.RelationStatement;
import xdi2.core.constants.XDIDictionaryConstants;
import xdi2.messaging.AddOperation;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.ExecutionContext;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.Prototype;
import xdi2.messaging.target.impl.graph.GraphMessagingTarget;
import xdi2.messaging.target.interceptor.AbstractInterceptor;
import xdi2.messaging.target.interceptor.OperationInterceptor;

/**
 * This interceptor make sure only one outgoing $is arc can exist on any context.
 * 
 * @author markus
 */
public class UniqueDollarIsInterceptor extends AbstractInterceptor implements OperationInterceptor, Prototype<UniqueDollarIsInterceptor> {

	/*
	 * Prototype
	 */

	@Override
	public UniqueDollarIsInterceptor instanceFor(PrototypingContext prototypingContext) {

		// done

		return this;
	}

	/*
	 * OperationInterceptor
	 */

	@Override
	public boolean before(Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// check if this a $add operation on a $is relational statement

		if (! (operation instanceof AddOperation)) return false;

		Statement targetStatement = operation.getTargetStatement();

		if (! (targetStatement instanceof RelationStatement)) return false;
		if (! XDIDictionaryConstants.XRI_S_IS.equals(((RelationStatement) targetStatement).getPredicate())) return false;

		// check if the target context already has a $is arc

		MessagingTarget currentMessagingTarget = executionContext.getCurrentMessagingTarget();
		if (! (currentMessagingTarget instanceof GraphMessagingTarget)) return false;

		Graph graph = ((GraphMessagingTarget) currentMessagingTarget).getGraph();

		ContextNode contextNode = graph.findContextNode(targetStatement.getContextNodeXri(), false);
		if (contextNode == null) return false;

		if (contextNode.containsRelations(XDIDictionaryConstants.XRI_S_IS)) {

			throw new Xdi2MessagingException("Context node " + contextNode.getXri() + " may contain only one outgoing $is arc.", null, executionContext);
		}

		// done

		return false;
	}

	@Override
	public boolean after(Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}
}
