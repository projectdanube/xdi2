package xdi2.messaging.target.interceptor.impl;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Relation;
import xdi2.core.constants.XDIConstants;
import xdi2.core.constants.XDIDictionaryConstants;
import xdi2.core.features.equivalence.Equivalence;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIStatement;
import xdi2.core.util.CopyUtil;
import xdi2.core.util.VariableUtil;
import xdi2.core.util.XDIAddressUtil;
import xdi2.core.util.iterators.IteratorListMaker;
import xdi2.messaging.GetOperation;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import xdi2.messaging.context.ExecutionContext;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.Prototype;
import xdi2.messaging.target.impl.AbstractMessagingTarget;
import xdi2.messaging.target.interceptor.AbstractInterceptor;
import xdi2.messaging.target.interceptor.InterceptorResult;
import xdi2.messaging.target.interceptor.MessageEnvelopeInterceptor;
import xdi2.messaging.target.interceptor.OperationInterceptor;
import xdi2.messaging.target.interceptor.TargetInterceptor;
import xdi2.messaging.target.interceptor.impl.linkcontract.LinkContractInterceptor;

/**
 * This interceptor handles $ref and $rep relations.
 * 
 * @author markus
 */
public class RefInterceptor extends AbstractInterceptor<MessagingTarget> implements MessageEnvelopeInterceptor, OperationInterceptor, TargetInterceptor, Prototype<RefInterceptor> {

	private static final Logger log = LoggerFactory.getLogger(RefInterceptor.class);

	/*
	 * Prototype
	 */

	@Override
	public RefInterceptor instanceFor(PrototypingContext prototypingContext) {

		// done

		return this;
	}

	/*
	 * Init and shutdown
	 */

	@Override
	public void init(MessagingTarget messagingTarget) throws Exception {

		super.init(messagingTarget);

		if (! (messagingTarget instanceof AbstractMessagingTarget)) throw new Xdi2MessagingException("Can only add this interceptor to an AbstractMessagingTarget", null, null);
	}

	/*
	 * MessageEnvelopeInterceptor
	 */

	@Override
	public InterceptorResult before(MessageEnvelope messageEnvelope, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		resetRefRepRelationsPerMessageEnvelope(executionContext);
		resetCompletedAddresses(executionContext);

		return InterceptorResult.DEFAULT;
	}

	@Override
	public InterceptorResult after(MessageEnvelope messageEnvelope, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return InterceptorResult.DEFAULT;
	}

	@Override
	public void exception(MessageEnvelope messageEnvelope, MessageResult messageResult, ExecutionContext executionContext, Exception ex) {

	}

	/*
	 * OperationInterceptor
	 */

	@Override
	public InterceptorResult before(Operation operation, MessageResult operationMessageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		resetRefRepRelationsPerOperation(executionContext);

		return InterceptorResult.DEFAULT;
	}

	@Override
	public InterceptorResult after(Operation operation, MessageResult operationMessageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// look through the message result to process result $ref/$rep relations

		if (operation instanceof GetOperation && operation.getTargetXDIAddress() != null) {

			List<Relation> refRepRelations = new IteratorListMaker<Relation> (Equivalence.getAllReferenceAndReplacementRelations(operationMessageResult.getGraph().getRootContextNode(true))).list();

			for (Relation refRepRelation : refRepRelations) {

				if (log.isDebugEnabled()) log.debug("In message result: Found $ref/$rep relation: " + refRepRelation);

				// don't follow $ref/$rep relations to target we covered already

				boolean skip = false;

				for (XDIAddress completedAddress : getCompletedAddresses(executionContext)) {

					if (refRepRelation.getContextNode().getXDIAddress().equals(completedAddress)) {

						if (log.isDebugEnabled()) log.debug("In message result: Skipping $ref/$rep relation " + refRepRelation + " because of already completed address " + completedAddress);

						// delete the $rep relation

						if (XDIDictionaryConstants.XDI_ADD_REP.equals(refRepRelation.getXDIAddress())) {

							refRepRelation.delete();
						}

						// don't follow this $ref/$rep relation

						skip = true;
						break;
					}
				}

				if (skip) continue;

				// delete the $ref/$rep relation

				ContextNode refRepContextNode = refRepRelation.getContextNode();

				if (XDIDictionaryConstants.XDI_ADD_REF.equals(refRepRelation.getXDIAddress()) || XDIDictionaryConstants.XDI_ADD_REP.equals(refRepRelation.getXDIAddress())) { 

					ContextNode refRepTargetContextNode = refRepRelation.follow();
					refRepRelation.delete();
					deleteWhileEmptyAndNoIncomingRelations(refRepTargetContextNode);
				}

				// $get feedback on the source of the $ref/$rep relation

				MessageResult feedbackMessageResult = feedbackGetSourceOfRefRepRelation(refRepContextNode, operation, executionContext);

				// merge the message result

				CopyUtil.copyGraph(feedbackMessageResult.getGraph(), operationMessageResult.getGraph(), null);

				// done with this $ref/$rep relation

				if (log.isDebugEnabled()) log.debug("In message result: After $get feedback on $ref/$rep relation " + refRepRelation + " we now have: " + operationMessageResult);
			}
		}

		// look through the message result to process followed $ref/$rep relations

		Relation refRepRelation;

		while ((refRepRelation = popRefRepRelationPerOperation(executionContext)) != null) {

			// check what to do with this $ref/$rep relation

			ContextNode refRepContextNode = refRepRelation.getContextNode();
			XDIAddress XDIaddress = refRepRelation.getXDIAddress();
			XDIAddress targetContextNodeXDIAddress = refRepRelation.getTargetContextNodeXDIAddress();

			boolean doReplaceRefRepRelations = XDIDictionaryConstants.XDI_ADD_REP.equals(XDIaddress) || (XDIDictionaryConstants.XDI_ADD_REF.equals(XDIaddress) && Boolean.TRUE.equals(operation.getParameterBoolean(GetOperation.XDI_ADD_PARAMETER_DEREF)));
			boolean doIncludeRefRelations = (XDIDictionaryConstants.XDI_ADD_REF.equals(XDIaddress) && ! Boolean.TRUE.equals(operation.getParameterBoolean(GetOperation.XDI_ADD_PARAMETER_DEREF)));

			// replace $ref/$rep relations?

			if (doReplaceRefRepRelations) {

				ContextNode refRepTargetContextNode = operationMessageResult.getGraph().getDeepContextNode(targetContextNodeXDIAddress, true);

				if (refRepTargetContextNode != null && ! operationMessageResult.getGraph().isEmpty()) {

					if (log.isDebugEnabled()) log.debug("In message result: Replacing $ref/$rep relation: " + refRepRelation);

					Graph tempGraph = MemoryGraphFactory.getInstance().openGraph();
					ContextNode tempContextNode = tempGraph.setDeepContextNode(refRepContextNode.getXDIAddress());
					CopyUtil.copyContextNodeContents(refRepTargetContextNode, tempContextNode, null);

					refRepTargetContextNode.clear();
					deleteWhileEmptyAndNoIncomingRelations(refRepTargetContextNode);

					CopyUtil.copyGraph(tempGraph, operationMessageResult.getGraph(), null);

					tempGraph.close();
				} else {

					if (log.isDebugEnabled()) log.debug("In message result: Not replacing $ref/$rep relation: " + refRepRelation);
				}
			}

			// include $ref relations?

			if (doIncludeRefRelations) {

				if (operationMessageResult.getGraph().containsStatement(refRepRelation.getStatement().getStatement())) {

					if (log.isDebugEnabled()) log.debug("In message result: Not including duplicate $ref relation: " + refRepRelation);
				} else {

					if (log.isDebugEnabled()) log.debug("In message result: Including $ref relation: " + refRepRelation);

					CopyUtil.copyStatement(refRepRelation.getStatement(), operationMessageResult.getGraph(), null);
				}
			}

			// done with this $ref/$rep relation

			if (log.isDebugEnabled()) log.debug("In message result: We now have: " + operationMessageResult);
		}

		// done

		return InterceptorResult.DEFAULT;
	}

	/*
	 * TargetInterceptor
	 */

	@Override
	public XDIAddress targetAddress(XDIAddress targetAddress, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// remember that we completed this target

		if (operation instanceof GetOperation) {

			XDIAddress contextNodeXDIAddress = targetAddress;

			addCompletedAddress(executionContext, contextNodeXDIAddress);
		}

		// follow any $ref and $rep arcs

		XDIAddress followedTargetAddress = followAllRefRepRelations(targetAddress, operation, executionContext);

		if (followedTargetAddress != targetAddress) {

			targetAddress = followedTargetAddress;
		}

		// done

		return targetAddress;
	}

	@Override
	public XDIStatement targetStatement(XDIStatement targetStatement, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// remember that we completed this target

		/*if (operation instanceof GetOperation) {

			XDIAddress contextNodeXDIAddress;

			if (targetStatement.isContextNodeStatement()) {

				contextNodeXDIAddress = targetStatement.gettargetContextNodeXDIAddress();

				addCompletedAddress(executionContext, contextNodeXDIAddress);
			}

			if (targetStatement.isRelationStatement() &&
					( XDIDictionaryConstants.XDI_ADD_REF.equals(targetStatement.getrelationAddress()) ||
							XDIDictionaryConstants.XDI_ADD_REP.equals(targetStatement.getrelationAddress()))) {

				contextNodeXDIAddress = targetStatement.getContextNodeXDIAddress();

				addCompletedAddress(executionContext, contextNodeXDIAddress);
			}
		}*/

		// follow any $ref and $rep arcs

		boolean doFollowTargetSubject = true;
		if (XDIDictionaryConstants.XDI_ADD_REF.equals(targetStatement.getRelationXDIAddress())) doFollowTargetSubject = false;
		if (XDIDictionaryConstants.XDI_ADD_REP.equals(targetStatement.getRelationXDIAddress())) doFollowTargetSubject = false;
		if (XDIDictionaryConstants.XDI_ADD_IS_REF.equals(targetStatement.getRelationXDIAddress())) doFollowTargetSubject = false;
		if (XDIDictionaryConstants.XDI_ADD_IS_REP.equals(targetStatement.getRelationXDIAddress())) doFollowTargetSubject = false;

		boolean doFollowTargetObject = true;
		if (XDIDictionaryConstants.XDI_ADD_REF.equals(targetStatement.getRelationXDIAddress())) doFollowTargetObject = false;
		if (XDIDictionaryConstants.XDI_ADD_REP.equals(targetStatement.getRelationXDIAddress())) doFollowTargetObject = false;
		if (XDIDictionaryConstants.XDI_ADD_IS_REF.equals(targetStatement.getRelationXDIAddress())) doFollowTargetObject = false;
		if (XDIDictionaryConstants.XDI_ADD_IS_REP.equals(targetStatement.getRelationXDIAddress())) doFollowTargetObject = false;
		if (! targetStatement.isRelationStatement()) doFollowTargetObject = false;
		if (targetStatement.isRelationStatement() && VariableUtil.isVariable(targetStatement.getTargetContextNodeXDIAddress())) doFollowTargetObject = false;

		XDIAddress followedTargetSubject = doFollowTargetSubject ? followAllRefRepRelations(targetStatement.getSubject(), operation, executionContext) : targetStatement.getSubject();
		Object followedTargetObject = doFollowTargetObject ? followAllRefRepRelations((XDIAddress) targetStatement.getObject(), operation, executionContext) : targetStatement.getObject();

		if (followedTargetSubject != targetStatement.getSubject() || followedTargetObject != targetStatement.getObject()) {

			targetStatement = XDIStatement.fromComponents(followedTargetSubject, targetStatement.getPredicate(), followedTargetObject);
		}

		// done

		return targetStatement;
	}

	private static XDIAddress followAllRefRepRelations(XDIAddress contextNodeXDIAddress, Operation operation, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDIAddress followedcontextNodeXDIAddress = contextNodeXDIAddress;

		XDIAddress tempcontextNodeXDIAddress;

		while (true) { 

			tempcontextNodeXDIAddress = followedcontextNodeXDIAddress;
			followedcontextNodeXDIAddress = followRefRepRelations(tempcontextNodeXDIAddress, operation, executionContext);

			if (followedcontextNodeXDIAddress == tempcontextNodeXDIAddress) break;

			if (log.isDebugEnabled()) log.debug("In message envelope: Followed " + tempcontextNodeXDIAddress + " to " + followedcontextNodeXDIAddress);
		}

		return followedcontextNodeXDIAddress;
	}

	private static XDIAddress followRefRepRelations(XDIAddress contextNodeXDIAddress, Operation operation, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDIAddress originalcontextNodeXDIAddress = contextNodeXDIAddress;

		XDIAddress localAddress = XDIConstants.XDI_ADD_ROOT;

		while (! XDIConstants.XDI_ADD_ROOT.equals(contextNodeXDIAddress)) {

			// look up $ref/$rep relations

			Relation[] refRepRelation = getRefRepRelationPerMessageEnvelope(executionContext, contextNodeXDIAddress);
			Relation refRelation;
			Relation repRelation;

			// if necessary, $get feedback to find $ref/$rep relations in context

			if (refRepRelation == null) {

				MessageResult feedbackMessageResult = feedbackFindRefRepRelationsInContext(contextNodeXDIAddress, operation, executionContext);

				// check for $ref/$rep relations in this context

				ContextNode contextNode = feedbackMessageResult.getGraph().getDeepContextNode(contextNodeXDIAddress, false);
				refRelation = contextNode == null ? null : Equivalence.getReferenceRelation(contextNode);
				repRelation = contextNode == null ? null : Equivalence.getReplacementRelation(contextNode);

				// remember $ref/$rep relations in this context

				refRepRelation = new Relation[2];
				refRepRelation[0] = refRelation;
				refRepRelation[1] = repRelation;

				setRefRepRelationPerMessageEnvelope(executionContext, contextNodeXDIAddress, refRepRelation);
			} else {

				refRelation = refRepRelation[0];
				repRelation = refRepRelation[1];
			}

			// follow $ref/$rep relations

			if (refRelation != null) {

				ContextNode referenceContextNode = refRelation.follow();
				if (referenceContextNode.equals(refRelation.getContextNode())) break;

				pushRefRepRelationPerOperation(executionContext, refRelation);

				return XDIAddressUtil.concatXDIAddresses(referenceContextNode.getXDIAddress(), localAddress);
			}

			if (repRelation != null) {

				ContextNode replacementContextNode  = repRelation.follow();
				if (replacementContextNode.equals(repRelation.getContextNode())) break;

				pushRefRepRelationPerOperation(executionContext, repRelation);

				return XDIAddressUtil.concatXDIAddresses(replacementContextNode.getXDIAddress(), localAddress);
			}

			// continue with parent context node XRI

			localAddress = XDIAddressUtil.concatXDIAddresses(XDIAddressUtil.localXDIAddress(contextNodeXDIAddress, 1), localAddress);
			contextNodeXDIAddress = XDIAddressUtil.parentXDIAddress(contextNodeXDIAddress, -1);
			if (contextNodeXDIAddress == null) contextNodeXDIAddress = XDIConstants.XDI_ADD_ROOT;
		}

		// done

		return originalcontextNodeXDIAddress;
	}

	/*
	 * Feedback methods
	 */

	private static MessageResult feedbackGetSourceOfRefRepRelation(ContextNode refRepContextNode, Operation operation, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (log.isDebugEnabled()) log.debug("Initiating $get feedback to get source of $ref/$rep relation: " + refRepContextNode);

		// prepare messaging target

		AbstractMessagingTarget messagingTarget = (AbstractMessagingTarget) executionContext.getCurrentMessagingTarget();

		// prepare feedback message result

		MessageResult feedbackMessageResult = new MessageResult();

		// prepare feedback message

		Message feedbackMessage = new MessageEnvelope().createMessage(operation.getSenderXDIAddress());
		feedbackMessage.setToPeerRootXDIArc(operation.getMessage().getToPeerRootXDIArc());

		Operation feedbackOperation = feedbackMessage.createGetOperation(refRepContextNode.getXDIAddress());
		if (Boolean.TRUE.equals(operation.getParameterBoolean(GetOperation.XDI_ADD_PARAMETER_DEREF))) feedbackOperation.setParameter(GetOperation.XDI_ADD_PARAMETER_DEREF, Boolean.TRUE);

		// feedback

		Map<String, Object> messageAttributes = null;
		Map<String, Object> operationAttributes = null;

		try {

			// before feedback: tweak the execution context and messaging target

			LinkContractInterceptor linkContractInterceptor = messagingTarget.getInterceptors().getInterceptor(LinkContractInterceptor.class);
			if (linkContractInterceptor != null) linkContractInterceptor.setDisabledForMessage(feedbackMessage);

			messageAttributes = executionContext.getMessageAttributes();
			operationAttributes = executionContext.getOperationAttributes();

			// execute feedback messages

			messagingTarget.execute(feedbackMessage, feedbackMessageResult, executionContext);
		} finally {

			// after feedback: restore the execution context and messaging target

			if (messageAttributes != null) executionContext.setMessageAttributes(messageAttributes);
			if (operationAttributes != null) executionContext.setOperationAttributes(operationAttributes);
		}

		// done

		if (log.isDebugEnabled()) log.debug("Completed $get feedback on source of $ref/$rep relation: " + refRepContextNode + ", message result: " + feedbackMessageResult);

		return feedbackMessageResult;
	}

	private static MessageResult feedbackFindRefRepRelationsInContext(XDIAddress contextNodeXDIAddress, Operation operation, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (log.isDebugEnabled()) log.debug("Initiating $get feedback to find $ref/$rep relations in context: " + contextNodeXDIAddress);

		// prepare messaging target

		AbstractMessagingTarget messagingTarget = (AbstractMessagingTarget) executionContext.getCurrentMessagingTarget();

		// prepare feedback message result

		MessageResult feedbackMessageResult = new MessageResult();

		// prepare feedback messages

		Message feedbackMessageRef = new MessageEnvelope().createMessage(operation.getSenderXDIAddress());
		Message feedbackMessageRep = new MessageEnvelope().createMessage(operation.getSenderXDIAddress());
		feedbackMessageRef.setToPeerRootXDIArc(operation.getMessage().getToPeerRootXDIArc());
		feedbackMessageRep.setToPeerRootXDIArc(operation.getMessage().getToPeerRootXDIArc());

		feedbackMessageRef.createGetOperation(XDIStatement.fromRelationComponents(contextNodeXDIAddress, XDIDictionaryConstants.XDI_ADD_REF, XDIConstants.XDI_ADD_VARIABLE));
		feedbackMessageRep.createGetOperation(XDIStatement.fromRelationComponents(contextNodeXDIAddress, XDIDictionaryConstants.XDI_ADD_REP, XDIConstants.XDI_ADD_VARIABLE));

		// feedback

		Map<String, Object> messageAttributes = null;
		Map<String, Object> operationAttributes = null;

		try {

			// before feedback: tweak the execution context and messaging target

			RefInterceptor refInterceptor = messagingTarget.getInterceptors().getInterceptor(RefInterceptor.class);
			if (refInterceptor != null) refInterceptor.setDisabledForMessage(feedbackMessageRef);
			if (refInterceptor != null) refInterceptor.setDisabledForMessage(feedbackMessageRep);

			LinkContractInterceptor linkContractInterceptor = messagingTarget.getInterceptors().getInterceptor(LinkContractInterceptor.class);
			if (linkContractInterceptor != null) linkContractInterceptor.setDisabledForMessage(feedbackMessageRef);
			if (linkContractInterceptor != null) linkContractInterceptor.setDisabledForMessage(feedbackMessageRep);

			MessagePolicyInterceptor messagePolicyInterceptor = messagingTarget.getInterceptors().getInterceptor(MessagePolicyInterceptor.class);
			if (messagePolicyInterceptor != null) messagePolicyInterceptor.setDisabledForMessage(feedbackMessageRef);
			if (messagePolicyInterceptor != null) messagePolicyInterceptor.setDisabledForMessage(feedbackMessageRep);

			messageAttributes = executionContext.getMessageAttributes();
			operationAttributes = executionContext.getOperationAttributes();

			// execute feedback messages

			messagingTarget.execute(feedbackMessageRef, feedbackMessageResult, executionContext);
			messagingTarget.execute(feedbackMessageRep, feedbackMessageResult, executionContext);
		} finally {

			// after feedback: restore the execution context and messaging target

			if (messageAttributes != null) executionContext.setMessageAttributes(messageAttributes);
			if (operationAttributes != null) executionContext.setOperationAttributes(operationAttributes);
		}

		// done

		if (log.isDebugEnabled()) log.debug("Completed $get feedback to find $ref/$rep relations in context: " + contextNodeXDIAddress + ", message result: " + feedbackMessageResult);

		return feedbackMessageResult;
	}

	/*
	 * Helper methods
	 */

	private static void deleteWhileEmptyAndNoIncomingRelations(ContextNode contextNode) {

		ContextNode currentContextNode = contextNode;
		ContextNode parentContextNode;

		while (currentContextNode.isEmpty() && (! currentContextNode.containsIncomingRelations()) && (! currentContextNode.isRootContextNode())) {

			parentContextNode = currentContextNode.getContextNode();
			currentContextNode.delete();
			currentContextNode = parentContextNode;
		}
	}

	/*
	 * ExecutionContext helper methods
	 */

	private static final String EXECUTIONCONTEXT_KEY_REFREPRELATIONS_PER_MESSAGEENVELOPE = RefInterceptor.class.getCanonicalName() + "#refreprelationspermessageenvelope";
	private static final String EXECUTIONCONTEXT_KEY_REFREPRELATIONS_PER_OPERATION = RefInterceptor.class.getCanonicalName() + "#refreprelationsperoperation";
	private static final String EXECUTIONCONTEXT_KEY_COMPLETEDADDRESSES_PER_MESSAGEENVELOPE = RefInterceptor.class.getCanonicalName() + "#completedaddressespermessageenvelope";

	@SuppressWarnings("unchecked")
	private static Map<XDIAddress, Relation[]> getRefRepRelationsPerMessageEnvelope(ExecutionContext executionContext) {

		return (Map<XDIAddress, Relation[]>) executionContext.getMessageEnvelopeAttribute(EXECUTIONCONTEXT_KEY_REFREPRELATIONS_PER_MESSAGEENVELOPE);
	}

	private static Relation[] getRefRepRelationPerMessageEnvelope(ExecutionContext executionContext, XDIAddress contextNodeXDIAddress) {

		Map<XDIAddress, Relation[]> refRepRelations = getRefRepRelationsPerMessageEnvelope(executionContext);

		Relation[] refRepRelation = refRepRelations.get(contextNodeXDIAddress);

		if (log.isDebugEnabled()) log.debug("Get $ref/$rep relation for " + contextNodeXDIAddress + ": " + refRepRelation);

		return refRepRelation;
	}

	private static void setRefRepRelationPerMessageEnvelope(ExecutionContext executionContext, XDIAddress contextNodeXDIAddress, Relation[] refRepRelation) {

		Map<XDIAddress, Relation[]> refRepRelations = getRefRepRelationsPerMessageEnvelope(executionContext);

		refRepRelations.put(contextNodeXDIAddress, refRepRelation);

		if (log.isDebugEnabled()) log.debug("Set $ref/$rep relation for " + contextNodeXDIAddress + ": " + refRepRelation);
	}

	private static void resetRefRepRelationsPerMessageEnvelope(ExecutionContext executionContext) {

		executionContext.putMessageEnvelopeAttribute(EXECUTIONCONTEXT_KEY_REFREPRELATIONS_PER_MESSAGEENVELOPE, new HashMap<XDIAddress, Relation[]> ());
	}

	@SuppressWarnings("unchecked")
	private static Deque<Relation> getRefRepRelationsPerOperation(ExecutionContext executionContext) {

		return (Deque<Relation>) executionContext.getOperationAttribute(EXECUTIONCONTEXT_KEY_REFREPRELATIONS_PER_OPERATION);
	}

	private static Relation popRefRepRelationPerOperation(ExecutionContext executionContext) {

		Deque<Relation> refRepRelations = getRefRepRelationsPerOperation(executionContext);
		if (refRepRelations.isEmpty()) return null;

		Relation refRepRelation = refRepRelations.pop();

		if (log.isDebugEnabled()) log.debug("Popping $ref/$rep relation: " + refRepRelation);

		return refRepRelation;
	}

	private static void pushRefRepRelationPerOperation(ExecutionContext executionContext, Relation refRepRelation) {

		getRefRepRelationsPerOperation(executionContext).push(refRepRelation);

		if (log.isDebugEnabled()) log.debug("Pushing $ref/$rep relation: " + refRepRelation);
	}

	private static void resetRefRepRelationsPerOperation(ExecutionContext executionContext) {

		executionContext.putOperationAttribute(EXECUTIONCONTEXT_KEY_REFREPRELATIONS_PER_OPERATION, new ArrayDeque<Relation> ());
	}

	@SuppressWarnings("unchecked")
	private static Set<XDIAddress> getCompletedAddresses(ExecutionContext executionContext) {

		return (Set<XDIAddress>) executionContext.getMessageEnvelopeAttribute(EXECUTIONCONTEXT_KEY_COMPLETEDADDRESSES_PER_MESSAGEENVELOPE);
	}

	private static void addCompletedAddress(ExecutionContext executionContext, XDIAddress contextNodeXDIAddress) {

		getCompletedAddresses(executionContext).add(contextNodeXDIAddress);

		if (log.isDebugEnabled()) log.debug("Added completed address: " + contextNodeXDIAddress);
	}

	private static void resetCompletedAddresses(ExecutionContext executionContext) {

		executionContext.putMessageEnvelopeAttribute(EXECUTIONCONTEXT_KEY_COMPLETEDADDRESSES_PER_MESSAGEENVELOPE, new HashSet<XDIAddress> ());
	}
}
