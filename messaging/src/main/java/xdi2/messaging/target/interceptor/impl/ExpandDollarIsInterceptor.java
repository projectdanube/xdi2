package xdi2.messaging.target.interceptor.impl;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.Relation;
import xdi2.core.Statement;
import xdi2.core.Statement.RelationStatement;
import xdi2.core.constants.XDIDictionaryConstants;
import xdi2.core.features.remoteroots.RemoteRoots;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.ExecutionContext;
import xdi2.messaging.target.interceptor.AbstractOperationInterceptor;
import xdi2.messaging.util.MessagingCloneUtil;

/**
 * This interceptor checks for $is relational statements in the result of an operation.
 * For each such statement, it creates a $get operation and feeds it into a "child" processing loop.
 * 
 * @author markus
 */
public class ExpandDollarIsInterceptor extends AbstractOperationInterceptor {

	@Override
	public boolean after(Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		for (Iterator<Statement> statements = messageResult.getGraph().getRootContextNode().getAllStatements(); statements.hasNext(); ) {

			Statement statement = statements.next();

			if (! (statement instanceof RelationStatement)) continue;

			Relation relation = ((RelationStatement) statement).getRelation();

			if (relation.getArcXri().equals(XDIDictionaryConstants.XRI_S_IS)) {

				ContextNode targetContextNode = relation.follow();

				if (targetContextNode.isRootContextNode()) continue;
				if (RemoteRoots.isRemoteRootContextNode(targetContextNode)) continue;

				Message message = MessagingCloneUtil.cloneMessage(operation.getMessage());
				message.deleteOperations();
				message.createGetOperation(relation.getTargetContextNodeXri());

				MessageEnvelope messageEnvelope = message.getMessageEnvelope();
				this.feedback(messageEnvelope, messageResult, executionContext);
			}
		}

		return false;
	}
}
