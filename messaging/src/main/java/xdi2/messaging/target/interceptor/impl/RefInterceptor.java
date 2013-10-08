package xdi2.messaging.target.interceptor.impl;

import java.util.ArrayDeque;
import java.util.Deque;
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
import xdi2.core.util.CopyUtil;
import xdi2.core.util.VariableUtil;
import xdi2.core.util.XDI3Util;
import xdi2.core.util.iterators.IteratorListMaker;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;
import xdi2.messaging.GetOperation;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import xdi2.messaging.constants.XDIMessagingConstants;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.exceptions.Xdi2NotAuthorizedException;
import xdi2.messaging.target.AbstractMessagingTarget;
import xdi2.messaging.target.ExecutionContext;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.Prototype;
import xdi2.messaging.target.interceptor.AbstractInterceptor;
import xdi2.messaging.target.interceptor.MessageEnvelopeInterceptor;
import xdi2.messaging.target.interceptor.OperationInterceptor;
import xdi2.messaging.target.interceptor.TargetInterceptor;
import xdi2.messaging.target.interceptor.impl.linkcontract.LinkContractInterceptor;
import xdi2.messaging.util.MessagingCloneUtil;

/**
 * This interceptor handles $ref and $rep relations.
 * 
 * @author markus
 */
public class RefInterceptor extends AbstractInterceptor implements MessageEnvelopeInterceptor, OperationInterceptor, TargetInterceptor, Prototype<RefInterceptor> {

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
	public boolean before(MessageEnvelope messageEnvelope, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		resetCompletedAddresses(executionContext);

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

		resetRefRepRelations(executionContext);

		return false;
	}

	@Override
	public boolean after(Operation operation, MessageResult operationMessageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// look through the message result to process result $ref/$rep relations

		if (operation instanceof GetOperation && operation.getTargetAddress() != null) {

			List<Relation> refRepRelations = new IteratorListMaker<Relation> (Equivalence.getAllReferenceAndReplacementRelations(operationMessageResult.getGraph().getRootContextNode())).list();

			for (Relation refRepRelation : refRepRelations) {

				if (log.isDebugEnabled()) log.debug("In message result: Found $ref/$rep relation: " + refRepRelation);

				// don't follow $ref/$rep relations to target we covered already

				boolean skip = false;

				for (XDI3Segment completedAddress : getCompletedAddresses(executionContext)) {

					if (refRepRelation.getContextNode().getXri().equals(completedAddress)) {

						if (log.isDebugEnabled()) log.debug("In message result: Skipping $ref/$rep relation " + refRepRelation + " because of already completed address " + completedAddress);

						// delete the $rep relation

						if (XDIDictionaryConstants.XRI_S_REP.equals(refRepRelation.getArcXri())) {

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

				if (XDIDictionaryConstants.XRI_S_REF.equals(refRepRelation.getArcXri()) || XDIDictionaryConstants.XRI_S_REP.equals(refRepRelation.getArcXri())) { 

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

		while ((refRepRelation = popRefRepRelation(executionContext)) != null) {

			// check what to do with this $ref/$rep relation

			ContextNode refRepContextNode = refRepRelation.getContextNode();
			XDI3Segment arcXri = refRepRelation.getArcXri();
			XDI3Segment targetContextNodeXri = refRepRelation.getTargetContextNodeXri();

			boolean doReplaceRefRepRelations = XDIDictionaryConstants.XRI_S_REP.equals(arcXri) || (XDIDictionaryConstants.XRI_S_REF.equals(arcXri) && Boolean.TRUE.equals(operation.getParameterBoolean(GetOperation.XRI_PARAMETER_DEREF)));
			boolean doIncludeRefRelations = (XDIDictionaryConstants.XRI_S_REF.equals(arcXri) && ! Boolean.TRUE.equals(operation.getParameterBoolean(GetOperation.XRI_PARAMETER_DEREF)));

			// replace $ref/$rep relations?

			if (doReplaceRefRepRelations) {

				ContextNode refRepTargetContextNode = operationMessageResult.getGraph().getDeepContextNode(targetContextNodeXri);

				if (refRepTargetContextNode != null && ! operationMessageResult.getGraph().isEmpty()) {

					if (log.isDebugEnabled()) log.debug("In message result: Replacing $ref/$rep relation: " + refRepRelation);

					Graph tempGraph = MemoryGraphFactory.getInstance().openGraph();
					ContextNode tempContextNode = tempGraph.setDeepContextNode(refRepContextNode.getXri());
					CopyUtil.copyContextNodeContents(refRepTargetContextNode, tempContextNode, null);

					refRepTargetContextNode.clear();
					deleteWhileEmptyAndNoIncomingRelations(refRepTargetContextNode);

					CopyUtil.copyGraph(tempGraph, operationMessageResult.getGraph(), null);
				} else {

					if (log.isDebugEnabled()) log.debug("In message result: Not replacing $ref/$rep relation: " + refRepRelation);
				}
			}

			// include $ref relations?

			if (doIncludeRefRelations) {

				if (operationMessageResult.getGraph().containsStatement(refRepRelation.getStatement().getXri())) {

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

		return false;
	}

	/*
	 * TargetInterceptor
	 */

	@Override
	public XDI3Segment targetAddress(XDI3Segment targetAddress, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// remember that we completed this target

		if (operation instanceof GetOperation) {

			XDI3Segment contextNodeXri = targetAddress;

			addCompletedAddress(executionContext, contextNodeXri);
		}

		// follow any $ref and $rep arcs

		XDI3Segment followedTargetAddress = followAllRefRepRelations(targetAddress, operation, executionContext);

		if (followedTargetAddress != targetAddress) {

			targetAddress = followedTargetAddress;
		}

		// done

		return targetAddress;
	}

	@Override
	public XDI3Statement targetStatement(XDI3Statement targetStatement, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// $get on a $ref or $rep arc?

		if (operation instanceof GetOperation) {

			if (XDIDictionaryConstants.XRI_S_REF.equals(targetStatement.getRelationArcXri()) ||
					XDIDictionaryConstants.XRI_S_REP.equals(targetStatement.getRelationArcXri())) {

				// don't do anything special

				if (log.isDebugEnabled()) log.debug("Not operating on $get on $ref/$rep target statement: " + targetStatement);

				return targetStatement;
			}
		}

		// remember that we completed this target

		/*if (operation instanceof GetOperation) {

			XDI3Segment contextNodeXri;

			if (targetStatement.isContextNodeStatement()) {

				contextNodeXri = targetStatement.getTargetContextNodeXri();

				addCompletedAddress(executionContext, contextNodeXri);
			}

			if (targetStatement.isRelationStatement() &&
					( XDIDictionaryConstants.XRI_S_REF.equals(targetStatement.getRelationArcXri()) ||
							XDIDictionaryConstants.XRI_S_REP.equals(targetStatement.getRelationArcXri()))) {

				contextNodeXri = targetStatement.getContextNodeXri();

				addCompletedAddress(executionContext, contextNodeXri);
			}
		}*/

		// follow any $ref and $rep arcs

		boolean doFollowTargetSubject = true;
		if (XDIDictionaryConstants.XRI_S_REF.equals(targetStatement.getRelationArcXri())) doFollowTargetSubject = false;
		if (XDIDictionaryConstants.XRI_S_REP.equals(targetStatement.getRelationArcXri())) doFollowTargetSubject = false;

		boolean doFollowTargetObject = true;
		if (! targetStatement.isRelationStatement()) doFollowTargetObject = false;
		if (targetStatement.isRelationStatement() && VariableUtil.isVariable(targetStatement.getTargetContextNodeXri())) doFollowTargetObject = false;

		XDI3Segment followedTargetSubject = doFollowTargetSubject ? followAllRefRepRelations(targetStatement.getSubject(), operation, executionContext) : targetStatement.getSubject();
		Object followedTargetObject = doFollowTargetObject ? followAllRefRepRelations((XDI3Segment) targetStatement.getObject(), operation, executionContext) : targetStatement.getObject();

		if (followedTargetSubject != targetStatement.getSubject() || followedTargetObject != targetStatement.getObject()) {

			targetStatement = XDI3Statement.fromComponents(followedTargetSubject, targetStatement.getPredicate(), followedTargetObject);
		}

		// done

		return targetStatement;
	}

	private static XDI3Segment followAllRefRepRelations(XDI3Segment contextNodeXri, Operation operation, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment followedContextNodeXri = contextNodeXri;

		XDI3Segment tempContextNodeXri;

		while (true) { 

			tempContextNodeXri = followedContextNodeXri;
			followedContextNodeXri = followRefRepRelations(tempContextNodeXri, operation, executionContext);

			if (followedContextNodeXri == tempContextNodeXri) break;

			if (log.isDebugEnabled()) log.debug("In message envelope: Followed " + tempContextNodeXri + " to " + followedContextNodeXri);
		}

		return followedContextNodeXri;
	}

	private static XDI3Segment followRefRepRelations(XDI3Segment contextNodeXri, Operation operation, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment originalContextNodeXri = contextNodeXri;

		XDI3Segment localXri = XDIConstants.XRI_S_ROOT;

		while (! XDIConstants.XRI_S_ROOT.equals(contextNodeXri)) {

			// $get feedback to find $ref/$rep relations in context

			MessageResult feedbackMessageResult = feedbackFindRefRepRelationsInContext(contextNodeXri, operation, executionContext);

			// check for $ref/$rep relations in this context

			ContextNode contextNode = feedbackMessageResult.getGraph().getDeepContextNode(contextNodeXri);
			Relation refRelation = contextNode == null ? null : Equivalence.getReferenceRelation(contextNode);
			Relation repRelation = contextNode == null ? null : Equivalence.getReplacementRelation(contextNode);

			// follow $ref/$rep relations

			if (refRelation != null) {

				ContextNode referenceContextNode = refRelation.follow();
				if (referenceContextNode.equals(contextNode)) break;

				pushRefRepRelation(executionContext, refRelation);

				return XDI3Util.concatXris(referenceContextNode.getXri(), localXri);
			}

			if (repRelation != null) {

				ContextNode replacementContextNode  = repRelation.follow();
				if (repRelation.equals(replacementContextNode)) break;

				pushRefRepRelation(executionContext, repRelation);

				return XDI3Util.concatXris(replacementContextNode.getXri(), localXri);
			}

			// continue with parent context node XRI

			localXri = XDI3Util.concatXris(XDI3Util.localXri(contextNodeXri, 1), localXri);
			contextNodeXri = XDI3Util.parentXri(contextNodeXri, -1);
			if (contextNodeXri == null) contextNodeXri = XDIConstants.XRI_S_ROOT;
		}

		// done

		return originalContextNodeXri;
	}

	/*
	 * Feedback methods
	 */

	private static MessageResult feedbackGetSourceOfRefRepRelation(ContextNode refRepContextNode, Operation operation, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (log.isDebugEnabled()) log.debug("Initiating $get feedback to get source of $ref/$rep relation: " + refRepContextNode);

		// prepare messaging target and message result

		AbstractMessagingTarget messagingTarget = (AbstractMessagingTarget) executionContext.getCurrentMessagingTarget();

		MessageResult feedbackMessageResult = new MessageResult();

		// prepare message

		Message feedbackMessage = MessagingCloneUtil.cloneMessage(operation.getMessage());
		feedbackMessage.deleteOperations();

		Operation feedbackOperation = feedbackMessage.createOperation(XDIMessagingConstants.XRI_S_GET, refRepContextNode.getXri());
		if (Boolean.TRUE.equals(operation.getParameterBoolean(GetOperation.XRI_PARAMETER_DEREF))) feedbackOperation.setParameter(GetOperation.XRI_PARAMETER_DEREF, Boolean.TRUE);

		// feedback

		LinkContractInterceptor linkContractInterceptor = null;
		Boolean linkContractInterceptorEnabled = null;
		Map<String, Object> messageAttributes = null;
		Map<String, Object> operationAttributes = null;

		try {

			// before feedback: tweak the execution context and messaging target

			linkContractInterceptor = messagingTarget.getInterceptors().getInterceptor(LinkContractInterceptor.class);
			linkContractInterceptorEnabled = Boolean.valueOf(linkContractInterceptor != null && linkContractInterceptor.isEnabled());
			if (linkContractInterceptor != null) linkContractInterceptor.setEnabled(false);

			messageAttributes = executionContext.getMessageAttributes();
			operationAttributes = executionContext.getOperationAttributes();

			// execute the message

			try {

				messagingTarget.execute(feedbackMessage, feedbackMessageResult, executionContext);
			} catch (Xdi2NotAuthorizedException ex) {

				if (log.isDebugEnabled()) log.debug("Not authorized to get source of $ref/$rep relation: " + refRepContextNode);
			}
		} finally {

			// after feedback: restore the execution context and messaging target

			if (linkContractInterceptor != null && linkContractInterceptorEnabled != null) linkContractInterceptor.setEnabled(linkContractInterceptorEnabled.booleanValue());

			if (messageAttributes != null) executionContext.setMessageAttributes(messageAttributes);
			if (operationAttributes != null) executionContext.setOperationAttributes(operationAttributes);
		}

		// done

		if (log.isDebugEnabled()) log.debug("Completed $get feedback on source of $ref/$rep relation: " + refRepContextNode + ", message result: " + feedbackMessageResult);

		return feedbackMessageResult;
	}

	private static MessageResult feedbackFindRefRepRelationsInContext(XDI3Segment contextNodeXri, Operation operation, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (log.isDebugEnabled()) log.debug("Initiating $get feedback to find $ref/$rep relations in context: " + contextNodeXri);

		// prepare messaging target and message result

		AbstractMessagingTarget messagingTarget = (AbstractMessagingTarget) executionContext.getCurrentMessagingTarget();

		MessageResult feedbackMessageResult = new MessageResult();

		// prepare messages

		Message feedbackMessageRef = MessagingCloneUtil.cloneMessage(operation.getMessage());
		Message feedbackMessageRep = MessagingCloneUtil.cloneMessage(operation.getMessage());
		feedbackMessageRef.deleteOperations();
		feedbackMessageRep.deleteOperations();

		feedbackMessageRef.createOperation(XDIMessagingConstants.XRI_S_GET, XDI3Statement.fromRelationComponents(contextNodeXri, XDIDictionaryConstants.XRI_S_REF, XDIConstants.XRI_S_VARIABLE));
		feedbackMessageRep.createOperation(XDIMessagingConstants.XRI_S_GET, XDI3Statement.fromRelationComponents(contextNodeXri, XDIDictionaryConstants.XRI_S_REP, XDIConstants.XRI_S_VARIABLE));

		// feedback

		RefInterceptor refInterceptor = null;
		Boolean refInterceptorEnabled = null;
		LinkContractInterceptor linkContractInterceptor = null; 
		Boolean linkContractInterceptorEnabled = null;
		Map<String, Object> messageAttributes = null;
		Map<String, Object> operationAttributes = null;

		try {

			// before feedback: tweak the execution context and messaging target

			refInterceptor = messagingTarget.getInterceptors().getInterceptor(RefInterceptor.class);
			refInterceptorEnabled = Boolean.valueOf(refInterceptor != null && refInterceptor.isEnabled());
			if (refInterceptor != null) refInterceptor.setEnabled(false);

			linkContractInterceptor = messagingTarget.getInterceptors().getInterceptor(LinkContractInterceptor.class);
			linkContractInterceptorEnabled = Boolean.valueOf(linkContractInterceptor != null && linkContractInterceptor.isEnabled());
			if (linkContractInterceptor != null) linkContractInterceptor.setEnabled(false);

			messageAttributes = executionContext.getMessageAttributes();
			operationAttributes = executionContext.getOperationAttributes();

			// execute messages

			try {

				messagingTarget.execute(feedbackMessageRef, feedbackMessageResult, executionContext);
			} catch (Xdi2NotAuthorizedException ex) {

				if (log.isDebugEnabled()) log.debug("Not authorized to find $ref relation in context: " + contextNodeXri);
			}

			try {

				messagingTarget.execute(feedbackMessageRep, feedbackMessageResult, executionContext);
			} catch (Xdi2NotAuthorizedException ex) {

				if (log.isDebugEnabled()) log.debug("Not authorized to find $rep relation in context: " + contextNodeXri);
			}
		} finally {

			// after feedback: restore the execution context and messaging target

			if (refInterceptor != null && refInterceptorEnabled != null) refInterceptor.setEnabled(refInterceptorEnabled.booleanValue());

			if (linkContractInterceptor != null && linkContractInterceptorEnabled != null) linkContractInterceptor.setEnabled(linkContractInterceptorEnabled.booleanValue());

			if (messageAttributes != null) executionContext.setMessageAttributes(messageAttributes);
			if (operationAttributes != null) executionContext.setOperationAttributes(operationAttributes);
		}

		// done

		if (log.isDebugEnabled()) log.debug("Completed $get feedback to find $ref/$rep relations in context: " + contextNodeXri + ", message result: " + feedbackMessageResult);

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

	private static final String EXECUTIONCONTEXT_KEY_REFREPRELATIONS_PER_OPERATION = RefInterceptor.class.getCanonicalName() + "#refreprelationsperoperation";
	private static final String EXECUTIONCONTEXT_KEY_COMPLETEDADDRESSES_PER_MESSAGEENVELOPE = RefInterceptor.class.getCanonicalName() + "#completedaddressespermessageenvelope";

	@SuppressWarnings("unchecked")
	private static Deque<Relation> getRefRepRelations(ExecutionContext executionContext) {

		return (Deque<Relation>) executionContext.getOperationAttribute(EXECUTIONCONTEXT_KEY_REFREPRELATIONS_PER_OPERATION);
	}

	private static Relation popRefRepRelation(ExecutionContext executionContext) {

		Deque<Relation> referenceRelations = getRefRepRelations(executionContext);
		if (referenceRelations.isEmpty()) return null;

		Relation referenceRelation = referenceRelations.pop();

		if (log.isDebugEnabled()) log.debug("Popping $ref/$rep relation: " + referenceRelation);

		return referenceRelation;
	}

	private static void pushRefRepRelation(ExecutionContext executionContext, Relation referenceRelation) {

		getRefRepRelations(executionContext).push(referenceRelation);

		if (log.isDebugEnabled()) log.debug("Pushing $ref/$rep relation: " + referenceRelation);
	}

	private static void resetRefRepRelations(ExecutionContext executionContext) {

		executionContext.putOperationAttribute(EXECUTIONCONTEXT_KEY_REFREPRELATIONS_PER_OPERATION, new ArrayDeque<Relation> ());
	}

	@SuppressWarnings("unchecked")
	private static Set<XDI3Segment> getCompletedAddresses(ExecutionContext executionContext) {

		return (Set<XDI3Segment>) executionContext.getMessageEnvelopeAttribute(EXECUTIONCONTEXT_KEY_COMPLETEDADDRESSES_PER_MESSAGEENVELOPE);
	}

	private static void addCompletedAddress(ExecutionContext executionContext, XDI3Segment contextNodeXri) {

		getCompletedAddresses(executionContext).add(contextNodeXri);

		if (log.isDebugEnabled()) log.debug("Added completed address: " + contextNodeXri);
	}

	private static void resetCompletedAddresses(ExecutionContext executionContext) {

		executionContext.putMessageEnvelopeAttribute(EXECUTIONCONTEXT_KEY_COMPLETEDADDRESSES_PER_MESSAGEENVELOPE, new HashSet<XDI3Segment> ());
	}
}
