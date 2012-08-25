package xdi2.messaging.target.interceptor.impl;

import xdi2.core.ContextNode;
import xdi2.core.Relation;
import xdi2.core.Statement;
import xdi2.core.Statement.RelationStatement;
import xdi2.core.constants.XDIDictionaryConstants;
import xdi2.core.features.remoteroots.RemoteRoots;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.Operation;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.ExecutionContext;
import xdi2.messaging.target.interceptor.AbstractOperationInterceptor;
import xdi2.messaging.util.MessagingCloneUtil;

public class ExpandDollarIsInterceptor extends AbstractOperationInterceptor {

	@Override
	public MessageEnvelope feedback(Operation operation, Statement statement, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (! (statement instanceof RelationStatement)) return null;

		Relation relation = ((RelationStatement) statement).getRelation();

		if (relation.getArcXri().equals(XDIDictionaryConstants.XRI_S_IS)) {

			ContextNode targetContextNode = relation.follow();

			if (targetContextNode.isRootContextNode()) return null;
			if (RemoteRoots.isRemoteRootContextNode(targetContextNode)) return null;

			Message message = MessagingCloneUtil.cloneMessage(operation.getMessage());

			message.deleteOperations();
			message.createGetOperation(relation.getTargetContextNodeXri());

			return message.getMessageEnvelope();
		}

		return null;
	}
}
