package xdi2.messaging.container.interceptor.impl;

import java.util.ArrayDeque;
import java.util.Arrays;
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
import xdi2.core.util.XDIAddressUtil;
import xdi2.core.util.iterators.IteratorListMaker;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.constants.XDIMessagingConstants;
import xdi2.messaging.container.MessagingContainer;
import xdi2.messaging.container.Prototype;
import xdi2.messaging.container.exceptions.Xdi2MessagingException;
import xdi2.messaging.container.execution.ExecutionContext;
import xdi2.messaging.container.execution.ExecutionResult;
import xdi2.messaging.container.impl.AbstractMessagingContainer;
import xdi2.messaging.container.interceptor.InterceptorResult;
import xdi2.messaging.container.interceptor.MessageInterceptor;
import xdi2.messaging.container.interceptor.OperationInterceptor;
import xdi2.messaging.container.interceptor.TargetInterceptor;
import xdi2.messaging.container.interceptor.impl.linkcontract.LinkContractInterceptor;
import xdi2.messaging.operations.GetOperation;
import xdi2.messaging.operations.Operation;

/**
 * This interceptor handles $ref and $rep relations.
 * 
 * @author markus
 */
public class RefInterceptor extends AbstractInterceptor<MessagingContainer> implements MessageInterceptor, OperationInterceptor, TargetInterceptor, Prototype<RefInterceptor> {

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
	public void init(MessagingContainer messagingContainer) throws Exception {

		super.init(messagingContainer);

		if (! (messagingContainer instanceof AbstractMessagingContainer)) throw new Xdi2MessagingException("Can only add this interceptor to an AbstractMessagingContainer", null, null);
	}

	/*
	 * MessageInterceptor
	 */

	@Override
	public InterceptorResult before(Message message, ExecutionContext executionContext, ExecutionResult executionResult) throws Xdi2MessagingException {

		resetRefRepRelationsPerMessage(executionContext);
		resetCompletedAddressesPerMessage(executionContext);

		return InterceptorResult.DEFAULT;
	}

	@Override
	public InterceptorResult after(Message message, ExecutionContext executionContext, ExecutionResult executionResult) throws Xdi2MessagingException {

		return InterceptorResult.DEFAULT;
	}

	/*
	 * OperationInterceptor
	 */

	@Override
	public InterceptorResult before(Operation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		resetRefRepRelationsPerOperation(executionContext);

		return InterceptorResult.DEFAULT;
	}

	@Override
	public InterceptorResult after(Operation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		// look through the message result to process result $ref/$rep relations

		if (operation instanceof GetOperation && operation.getTargetXDIAddress() != null) {

			List<Relation> refRepRelations = new IteratorListMaker<Relation> (Equivalence.getAllReferenceAndReplacementRelations(operationResultGraph.getRootContextNode(true))).list();

			for (Relation refRepRelation : refRepRelations) {

				if (log.isDebugEnabled()) log.debug("In message result: Found $ref/$rep relation: " + refRepRelation);

				XDIAddress refRepContextNodeXDIAddress = refRepRelation.getContextNode().getXDIAddress();

				// don't follow $ref/$rep relations to target we covered already

				boolean skip = false;

				for (XDIAddress completedAddress : getCompletedAddressesPerMessage(executionContext)) {

					if (refRepContextNodeXDIAddress.equals(completedAddress)) {

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

				if (XDIDictionaryConstants.XDI_ADD_REF.equals(refRepRelation.getXDIAddress()) || XDIDictionaryConstants.XDI_ADD_REP.equals(refRepRelation.getXDIAddress())) { 

					ContextNode refRepTargetContextNode = refRepRelation.followContextNode();

					if (refRepTargetContextNode != null) {

						refRepRelation.delete();
						deleteWhileEmptyAndNoIncomingRelations(refRepTargetContextNode);
					}
				}

				// $get feedback on the source of the $ref/$rep relation

				Graph feedbackResultGraph = feedbackGetSourceOfRefRepRelation(refRepContextNodeXDIAddress, operation, executionContext);

				// merge the message result

				CopyUtil.copyGraph(feedbackResultGraph, operationResultGraph, null);

				// done with this $ref/$rep relation

				if (log.isDebugEnabled()) log.debug("In message result: After $get feedback on $ref/$rep relation " + refRepRelation + " we now have: " + operationResultGraph);
			}
		}

		// look through the message result to process followed $ref/$rep relations

		Relation refRepRelation;

		while ((refRepRelation = popRefRepRelationPerOperation(executionContext)) != null) {

			// check what to do with this $ref/$rep relation

			ContextNode refRepContextNode = refRepRelation.getContextNode();
			XDIAddress XDIaddress = refRepRelation.getXDIAddress();
			XDIAddress targetXDIAddress = refRepRelation.getTargetXDIAddress();

			boolean doReplaceRefRepRelations = XDIDictionaryConstants.XDI_ADD_REP.equals(XDIaddress) || (XDIDictionaryConstants.XDI_ADD_REF.equals(XDIaddress) && Boolean.TRUE.equals(operation.getParameterBoolean(XDIMessagingConstants.XDI_ADD_OPERATION_PARAMETER_DEREF)));
			boolean doIncludeRefRelations = (XDIDictionaryConstants.XDI_ADD_REF.equals(XDIaddress) && ! Boolean.TRUE.equals(operation.getParameterBoolean(XDIMessagingConstants.XDI_ADD_OPERATION_PARAMETER_DEREF)));

			// replace $ref/$rep relations?

			if (doReplaceRefRepRelations) {

				ContextNode refRepTargetContextNode = operationResultGraph.getDeepContextNode(targetXDIAddress, true);

				if (refRepTargetContextNode != null && ! operationResultGraph.isEmpty()) {

					if (log.isDebugEnabled()) log.debug("In message result: Replacing $ref/$rep relation: " + refRepRelation);

					Graph tempGraph = MemoryGraphFactory.getInstance().openGraph();
					ContextNode tempContextNode = tempGraph.setDeepContextNode(refRepContextNode.getXDIAddress());
					CopyUtil.copyContextNodeContents(refRepTargetContextNode, tempContextNode, null);

					refRepTargetContextNode.clear();
					deleteWhileEmptyAndNoIncomingRelations(refRepTargetContextNode);

					CopyUtil.copyGraph(tempGraph, operationResultGraph, null);

					tempGraph.close();
				} else {

					if (log.isDebugEnabled()) log.debug("In message result: Not replacing $ref/$rep relation: " + refRepRelation);
				}
			}

			// include $ref relations?

			if (doIncludeRefRelations) {

				if (operationResultGraph.containsStatement(refRepRelation.getStatement().getXDIStatement())) {

					if (log.isDebugEnabled()) log.debug("In message result: Not including duplicate $ref relation: " + refRepRelation);
				} else {

					if (log.isDebugEnabled()) log.debug("In message result: Including $ref relation: " + refRepRelation);

					CopyUtil.copyStatement(refRepRelation.getStatement(), operationResultGraph, null);
				}
			}

			// done with this $ref/$rep relation

			if (log.isDebugEnabled()) log.debug("In message result: We now have: " + operationResultGraph);
		}

		// done

		return InterceptorResult.DEFAULT;
	}

	/*
	 * TargetInterceptor
	 */

	@Override
	public XDIAddress targetAddress(XDIAddress targetAddress, Operation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		// don't operate on literal node address

		if (targetAddress.isLiteralNodeXDIAddress()) return targetAddress;

		// remember that we completed this target

		if (operation instanceof GetOperation) {

			XDIAddress contextNodeXDIAddress = targetAddress;

			addCompletedAddressPerMessage(executionContext, contextNodeXDIAddress);
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
	public XDIStatement targetStatement(XDIStatement targetStatement, Operation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		// remember that we completed this target

		/*if (operation instanceof GetOperation) {

			XDIAddress contextNodeXDIAddress;

			if (targetStatement.isContextNodeStatement()) {

				contextNodeXDIAddress = targetStatement.gettargetXDIAddress();

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
		if (targetStatement.isRelationStatement() && XDIConstants.XDI_ADD_COMMON_VARIABLE.equals(targetStatement.getTargetXDIAddress())) doFollowTargetObject = false;

		XDIAddress followedTargetSubject = doFollowTargetSubject ? followAllRefRepRelations(targetStatement.getSubject(), operation, executionContext) : targetStatement.getSubject();
		Object followedTargetObject = doFollowTargetObject ? followAllRefRepRelations((XDIAddress) targetStatement.getObject(), operation, executionContext) : targetStatement.getObject();

		if (followedTargetSubject != targetStatement.getSubject() || followedTargetObject != targetStatement.getObject()) {

			targetStatement = XDIStatement.fromComponents(followedTargetSubject, targetStatement.getPredicate(), followedTargetObject);
		}

		// $ref/$rep relations may have been added now, so let's "forget" them 

		if (XDIDictionaryConstants.XDI_ADD_REF.equals(targetStatement.getRelationXDIAddress()) ||
				XDIDictionaryConstants.XDI_ADD_REP.equals(targetStatement.getRelationXDIAddress())) {

			setRefRepRelationPerMessage(executionContext, followedTargetSubject, null);
		}

		// done

		return targetStatement;
	}

	private static XDIAddress followAllRefRepRelations(XDIAddress contextNodeXDIAddress, Operation operation, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDIAddress followedContextNodeXDIAddress = contextNodeXDIAddress;

		XDIAddress tempContextNodeXDIAddress;

		while (true) { 

			tempContextNodeXDIAddress = followedContextNodeXDIAddress;
			followedContextNodeXDIAddress = followRefRepRelations(tempContextNodeXDIAddress, operation, executionContext);

			if (followedContextNodeXDIAddress == tempContextNodeXDIAddress) break;

			if (log.isDebugEnabled()) log.debug("In message envelope: Followed " + tempContextNodeXDIAddress + " to " + followedContextNodeXDIAddress);
		}

		return followedContextNodeXDIAddress;
	}

	private static XDIAddress followRefRepRelations(XDIAddress contextNodeXDIAddress, Operation operation, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDIAddress originalContextNodeXDIAddress = contextNodeXDIAddress;

		XDIAddress localAddress = XDIConstants.XDI_ADD_ROOT;

		while (! XDIConstants.XDI_ADD_ROOT.equals(contextNodeXDIAddress)) {

			// look up $ref/$rep relations

			Relation[] refRepRelation = getRefRepRelationPerMessage(executionContext, contextNodeXDIAddress);
			Relation refRelation;
			Relation repRelation;

			// if necessary, $get feedback to find $ref/$rep relations in context

			if (refRepRelation == null) {

				Graph feedbackResultGraph = feedbackFindRefRepRelationsInContext(contextNodeXDIAddress, operation, executionContext);

				// check for $ref/$rep relations in this context

				ContextNode contextNode = feedbackResultGraph.getDeepContextNode(contextNodeXDIAddress, false);
				refRelation = contextNode == null ? null : Equivalence.getReferenceRelation(contextNode);
				repRelation = contextNode == null ? null : Equivalence.getReplacementRelation(contextNode);

				// remember $ref/$rep relations in this context

				refRepRelation = new Relation[2];
				refRepRelation[0] = refRelation;
				refRepRelation[1] = repRelation;

				setRefRepRelationPerMessage(executionContext, contextNodeXDIAddress, refRepRelation);
			} else {

				refRelation = refRepRelation[0];
				repRelation = refRepRelation[1];
			}

			// follow $ref/$rep relations

			if (refRelation != null) {

				ContextNode referenceContextNode = refRelation.followContextNode();

				if (referenceContextNode != null) {

					if (referenceContextNode.equals(refRelation.getContextNode())) break;
					pushRefRepRelationPerOperation(executionContext, refRelation);

					return XDIAddressUtil.concatXDIAddresses(referenceContextNode.getXDIAddress(), localAddress);
				}
			}

			if (repRelation != null) {

				ContextNode replacementContextNode  = repRelation.followContextNode();

				if (replacementContextNode != null) {

					if (replacementContextNode.equals(repRelation.getContextNode())) break;
					pushRefRepRelationPerOperation(executionContext, repRelation);

					return XDIAddressUtil.concatXDIAddresses(replacementContextNode.getXDIAddress(), localAddress);
				}
			}

			// continue with parent context node address

			localAddress = XDIAddressUtil.concatXDIAddresses(XDIAddressUtil.localXDIAddress(contextNodeXDIAddress, 1), localAddress);
			contextNodeXDIAddress = XDIAddressUtil.parentXDIAddress(contextNodeXDIAddress, -1);
			if (contextNodeXDIAddress == null) contextNodeXDIAddress = XDIConstants.XDI_ADD_ROOT;
		}

		// done

		return originalContextNodeXDIAddress;
	}

	/*
	 * Feedback methods
	 */

	private static Graph feedbackGetSourceOfRefRepRelation(XDIAddress refRepContextNodeXDIAddress, Operation operation, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (log.isDebugEnabled()) log.debug("Initiating $get feedback to get source of $ref/$rep relation: " + refRepContextNodeXDIAddress);

		// prepare messaging container

		AbstractMessagingContainer messagingContainer = (AbstractMessagingContainer) executionContext.getCurrentMessagingContainer();

		// prepare feedback message

		MessageEnvelope feedbackMessageEnvelope = new MessageEnvelope();

		Message feedbackMessage = feedbackMessageEnvelope.createMessage(operation.getMessage().getSenderXDIAddress());
		feedbackMessage.setToPeerRootXDIArc(operation.getMessage().getToPeerRootXDIArc());

		Operation feedbackOperation = feedbackMessage.createGetOperation(refRepContextNodeXDIAddress);
		if (Boolean.TRUE.equals(operation.getParameterBoolean(XDIMessagingConstants.XDI_ADD_OPERATION_PARAMETER_DEREF))) feedbackOperation.setParameter(XDIMessagingConstants.XDI_ADD_OPERATION_PARAMETER_DEREF, Boolean.TRUE);

		// prepare feedback execution result

		ExecutionResult feedbackExecutionResult = ExecutionResult.createExecutionResult(feedbackMessageEnvelope);

		// feedback

		Map<String, Object> operationAttributes = null;

		try {

			// before feedback: tweak the execution context and messaging container

			LinkContractInterceptor linkContractInterceptor = messagingContainer.getInterceptors().getInterceptor(LinkContractInterceptor.class);
			if (linkContractInterceptor != null) linkContractInterceptor.setDisabledForOperation(feedbackOperation);

			operationAttributes = executionContext.getOperationAttributes();

			// execute feedback message

			messagingContainer.execute(feedbackOperation, executionContext, feedbackExecutionResult);
		} finally {

			// after feedback: restore the execution context and messaging container

			if (operationAttributes != null) executionContext.setOperationAttributes(operationAttributes);
		}

		// finish feedback execution result

		feedbackExecutionResult.finish();

		// done

		if (log.isDebugEnabled()) log.debug("Completed $get feedback on source of $ref/$rep relation: " + refRepContextNodeXDIAddress + ", execution result: " + feedbackExecutionResult);

		return feedbackExecutionResult.getFinishedOperationResultGraph(feedbackOperation);
	}

	private static Graph feedbackFindRefRepRelationsInContext(XDIAddress contextNodeXDIAddress, Operation operation, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (log.isDebugEnabled()) log.debug("Initiating $get feedback to find $ref/$rep relations in context: " + contextNodeXDIAddress);

		// prepare messaging container

		AbstractMessagingContainer messagingContainer = (AbstractMessagingContainer) executionContext.getCurrentMessagingContainer();

		// prepare feedback messages

		MessageEnvelope feedbackMessageEnvelope = new MessageEnvelope();

		Message feedbackMessageRef = feedbackMessageEnvelope.createMessage(operation.getMessage().getSenderXDIAddress());
		Message feedbackMessageRep = feedbackMessageEnvelope.createMessage(operation.getMessage().getSenderXDIAddress());
		feedbackMessageRef.setToPeerRootXDIArc(operation.getMessage().getToPeerRootXDIArc());
		feedbackMessageRep.setToPeerRootXDIArc(operation.getMessage().getToPeerRootXDIArc());

		Operation feedbackOperationRef = feedbackMessageRef.createGetOperation(XDIStatement.fromRelationComponents(contextNodeXDIAddress, XDIDictionaryConstants.XDI_ADD_REF, XDIConstants.XDI_ADD_COMMON_VARIABLE));
		Operation feedbackOperationRep = feedbackMessageRep.createGetOperation(XDIStatement.fromRelationComponents(contextNodeXDIAddress, XDIDictionaryConstants.XDI_ADD_REP, XDIConstants.XDI_ADD_COMMON_VARIABLE));

		// prepare feedback execution result

		ExecutionResult feedbackExecutionResult = ExecutionResult.createExecutionResult(feedbackMessageEnvelope);

		// feedback

		Map<String, Object> messageAttributes = null;
		Map<String, Object> operationAttributes = null;

		try {

			// before feedback: tweak the execution context and messaging container

			RefInterceptor refInterceptor = messagingContainer.getInterceptors().getInterceptor(RefInterceptor.class);
			if (refInterceptor != null) refInterceptor.setDisabledForOperation(feedbackOperationRef);
			if (refInterceptor != null) refInterceptor.setDisabledForOperation(feedbackOperationRep);

			LinkContractInterceptor linkContractInterceptor = messagingContainer.getInterceptors().getInterceptor(LinkContractInterceptor.class);
			if (linkContractInterceptor != null) linkContractInterceptor.setDisabledForOperation(feedbackOperationRef);
			if (linkContractInterceptor != null) linkContractInterceptor.setDisabledForOperation(feedbackOperationRep);

			messageAttributes = executionContext.getMessageAttributes();
			operationAttributes = executionContext.getOperationAttributes();

			// execute feedback messages

			messagingContainer.execute(feedbackOperationRef, executionContext, feedbackExecutionResult);
			messagingContainer.execute(feedbackOperationRep, executionContext, feedbackExecutionResult);
		} finally {

			// after feedback: restore the execution context and messaging container

			if (messageAttributes != null) executionContext.setMessageAttributes(messageAttributes);
			if (operationAttributes != null) executionContext.setOperationAttributes(operationAttributes);
		}

		// finish feedback execution result

		feedbackExecutionResult.finish();

		// done

		if (log.isDebugEnabled()) log.debug("Completed $get feedback to find $ref/$rep relations in context: " + contextNodeXDIAddress + ", execution result: " + feedbackExecutionResult);

		return feedbackExecutionResult.makeLightMessagingResponse().getResultGraph();
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

	private static final String EXECUTIONCONTEXT_KEY_REFREPRELATIONS_PER_MESSAGE = RefInterceptor.class.getCanonicalName() + "#refreprelationspermessage";
	private static final String EXECUTIONCONTEXT_KEY_REFREPRELATIONS_PER_OPERATION = RefInterceptor.class.getCanonicalName() + "#refreprelationsperoperation";
	private static final String EXECUTIONCONTEXT_KEY_COMPLETEDADDRESSES_PER_MESSAGE = RefInterceptor.class.getCanonicalName() + "#completedaddressespermessage";

	@SuppressWarnings("unchecked")
	private static Map<XDIAddress, Relation[]> getRefRepRelationsPerMessage(ExecutionContext executionContext) {

		return (Map<XDIAddress, Relation[]>) executionContext.getMessageAttribute(EXECUTIONCONTEXT_KEY_REFREPRELATIONS_PER_MESSAGE);
	}

	private static Relation[] getRefRepRelationPerMessage(ExecutionContext executionContext, XDIAddress contextNodeXDIAddress) {

		Map<XDIAddress, Relation[]> refRepRelations = getRefRepRelationsPerMessage(executionContext);

		Relation[] refRepRelation = refRepRelations.get(contextNodeXDIAddress);

		if (log.isDebugEnabled()) log.debug("Get $ref/$rep relation for " + contextNodeXDIAddress + ": " + Arrays.asList(refRepRelation));

		return refRepRelation;
	}

	private static void setRefRepRelationPerMessage(ExecutionContext executionContext, XDIAddress contextNodeXDIAddress, Relation[] refRepRelation) {

		Map<XDIAddress, Relation[]> refRepRelations = getRefRepRelationsPerMessage(executionContext);

		refRepRelations.put(contextNodeXDIAddress, refRepRelation);

		if (log.isDebugEnabled()) log.debug("Set $ref/$rep relation for " + contextNodeXDIAddress + ": " + refRepRelation);
	}

	private static void resetRefRepRelationsPerMessage(ExecutionContext executionContext) {

		executionContext.putMessageAttribute(EXECUTIONCONTEXT_KEY_REFREPRELATIONS_PER_MESSAGE, new HashMap<XDIAddress, Relation[]> ());
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
	private static Set<XDIAddress> getCompletedAddressesPerMessage(ExecutionContext executionContext) {

		return (Set<XDIAddress>) executionContext.getMessageAttribute(EXECUTIONCONTEXT_KEY_COMPLETEDADDRESSES_PER_MESSAGE);
	}

	private static void addCompletedAddressPerMessage(ExecutionContext executionContext, XDIAddress contextNodeXDIAddress) {

		getCompletedAddressesPerMessage(executionContext).add(contextNodeXDIAddress);

		if (log.isDebugEnabled()) log.debug("Added completed address: " + contextNodeXDIAddress);
	}

	private static void resetCompletedAddressesPerMessage(ExecutionContext executionContext) {

		executionContext.putMessageAttribute(EXECUTIONCONTEXT_KEY_COMPLETEDADDRESSES_PER_MESSAGE, new HashSet<XDIAddress> ());
	}
}
