package xdi2.messaging.target.interceptor.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.Relation;
import xdi2.core.Statement;
import xdi2.core.Statement.RelationStatement;
import xdi2.core.constants.XDIDictionaryConstants;
import xdi2.core.features.remoteroots.RemoteRoots;
import xdi2.core.util.iterators.IteratorListMaker;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.messaging.GetOperation;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.ExecutionContext;
import xdi2.messaging.target.Prototype;
import xdi2.messaging.target.interceptor.AbstractInterceptor;
import xdi2.messaging.target.interceptor.MessageEnvelopeInterceptor;
import xdi2.messaging.target.interceptor.OperationInterceptor;
import xdi2.messaging.util.MessagingCloneUtil;

/**
 * This interceptor checks for $is relational statements in the result of an operation.
 * For each such statement, it creates a $get operation and feeds it into a "child" processing loop.
 * 
 * @author markus
 */
public class ExpandDollarIsInterceptor extends AbstractInterceptor implements MessageEnvelopeInterceptor, OperationInterceptor, Prototype<ExpandDollarIsInterceptor> {

	private static final Logger log = LoggerFactory.getLogger(ExpandDollarIsInterceptor.class);

	/*
	 * Prototype
	 */

	@Override
	public ExpandDollarIsInterceptor instanceFor(PrototypingContext prototypingContext) {

		// done

		return this;
	}

	/*
	 * MessageEnvelopeInterceptor
	 */

	@Override
	public boolean before(MessageEnvelope messageEnvelope, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		resetExpandedAddresses(executionContext);

		return false;
	}

	@Override
	public boolean after(MessageEnvelope messageEnvelope, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	@Override
	public void exception(MessageEnvelope messageEnvelope, MessageResult messageResult, ExecutionContext executionContext, Exception ex) {

	}

	/*
	 * OperationInterceptor
	 */

	@Override
	public boolean before(Operation operation, MessageResult operationMessageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// consider $get targets of the operations to be already expanded

		if (operation instanceof GetOperation) {

			XRI3Segment targetAddress = operation.getTargetAddress();

			if (targetAddress != null) {

				addExpandedAddress(executionContext, targetAddress);
			}
		}

		// done

		return false;
	}

	@Override
	public boolean after(Operation operation, MessageResult operationMessageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// look through the message result for $is statements

		List<Statement> statements = new IteratorListMaker<Statement> (operationMessageResult.getGraph().getRootContextNode().getAllStatements()).list();
		
		for (Statement statement : statements) {

			if (! (statement instanceof RelationStatement)) continue;

			Relation relation = ((RelationStatement) statement).getRelation();

			if (relation.getArcXri().equals(XDIDictionaryConstants.XRI_S_IS)) {

				XRI3Segment targetContextNodeXri = relation.getTargetContextNodeXri();

				if (isExpandedAddress(executionContext, targetContextNodeXri)) {

					log.debug("Already expanded: " + targetContextNodeXri);

					return false;
				} else {

					log.debug("Expanding: " + targetContextNodeXri);

					addExpandedAddress(executionContext, targetContextNodeXri);
				}

				ContextNode targetContextNode = relation.follow();

				if (targetContextNode.isRootContextNode()) continue;
				if (RemoteRoots.isRemoteRootContextNode(targetContextNode)) continue;

				Message message = MessagingCloneUtil.cloneMessage(operation.getMessage());
				message.deleteOperations();
				message.createGetOperation(relation.getTargetContextNodeXri());

				this.feedback(message, operationMessageResult, executionContext);
			}
		}

		// done

		return false;
	}

	/*
	 * ExecutionContext helper methods
	 */

	private static final String EXECUTIONCONTEXT_KEY_EXPANDEDADDRESSES_PER_MESSAGEENVELOPE = ExpandDollarIsInterceptor.class.getCanonicalName() + "#expandedaddressespermessageenvelope";

	@SuppressWarnings("unchecked")
	private static Set<XRI3Segment> getExpandedAddresses(ExecutionContext executionContext) {

		return (Set<XRI3Segment>) executionContext.getMessageEnvelopeAttribute(EXECUTIONCONTEXT_KEY_EXPANDEDADDRESSES_PER_MESSAGEENVELOPE);
	}

	private static boolean isExpandedAddress(ExecutionContext executionContext, XRI3Segment contextNodeXri) {

		return getExpandedAddresses(executionContext).contains(contextNodeXri);
	}

	private static void addExpandedAddress(ExecutionContext executionContext, XRI3Segment contextNodeXri) {

		getExpandedAddresses(executionContext).add(contextNodeXri);
	}

	private static void resetExpandedAddresses(ExecutionContext executionContext) {

		executionContext.putMessageEnvelopeAttribute(EXECUTIONCONTEXT_KEY_EXPANDEDADDRESSES_PER_MESSAGEENVELOPE, new HashSet<XRI3Segment> ());
	}
}
