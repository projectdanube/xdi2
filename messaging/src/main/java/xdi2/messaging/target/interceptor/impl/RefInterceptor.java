package xdi2.messaging.target.interceptor.impl;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
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
import xdi2.core.util.StatementUtil;
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
import xdi2.messaging.target.AbstractMessagingTarget;
import xdi2.messaging.target.ExecutionContext;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.Prototype;
import xdi2.messaging.target.interceptor.AbstractInterceptor;
import xdi2.messaging.target.interceptor.MessageEnvelopeInterceptor;
import xdi2.messaging.target.interceptor.MessagingTargetInterceptor;
import xdi2.messaging.target.interceptor.OperationInterceptor;
import xdi2.messaging.target.interceptor.TargetInterceptor;
import xdi2.messaging.util.MessagingCloneUtil;

/**
 * This interceptor handles $ref and $rep relations.
 * 
 * @author markus
 */
public class RefInterceptor extends AbstractInterceptor implements MessagingTargetInterceptor, MessageEnvelopeInterceptor, OperationInterceptor, TargetInterceptor, Prototype<RefInterceptor> {

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
	 * MessagingTargetInterceptor
	 */

	@Override
	public void init(MessagingTarget messagingTarget) throws Exception {

		if (! (messagingTarget instanceof AbstractMessagingTarget)) throw new Xdi2MessagingException("Can only add this interceptor to an AbstractMessagingTarget", null, null);
	}

	@Override
	public void shutdown(MessagingTarget messagingTarget) throws Exception {

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

				XDI3Segment targetContextNodeXri = refRepRelation.getTargetContextNodeXri();

				// don't follow $ref/$rep relations to target we covered already

				boolean skip = false;

				for (XDI3Segment completedAddress : getCompletedAddresses(executionContext)) {

					if (XDI3Util.startsWith(targetContextNodeXri, completedAddress) != null) {

						if (log.isDebugEnabled()) log.debug("In message result: Skipping $ref/$rep relation to " + targetContextNodeXri + " because of already completed address (" + completedAddress + "): " + refRepRelation);

						if (XDIDictionaryConstants.XRI_S_REP.equals(refRepRelation.getArcXri())) refRepRelation.delete();
						skip = true;
					}
				}

				if (skip) continue;

				// delete the $ref/$rep relation

				ContextNode refRepContextNode = refRepRelation.getContextNode();
				ContextNode refRepTargetContextNode = refRepRelation.follow();
				refRepRelation.delete();
				deleteWhileEmptyAndNoIncomingRelations(refRepTargetContextNode);

				// $get feedback on the source of the $ref/$rep relation

				MessageResult feedbackMessageResult = this.feedbackOnSourceOfRefRepRelation(refRepContextNode, operation, executionContext);

				// merge the message result

				CopyUtil.copyGraph(feedbackMessageResult.getGraph(), operationMessageResult.getGraph(), null);

				// done with this $ref/$rep relation

				if (log.isDebugEnabled()) log.debug("In message result: We now have: " + operationMessageResult);
			}
		}

		// look through the message result to process followed $ref/$rep relations

		Relation refRepRelation;

		while ((refRepRelation = popRefRepRelation(executionContext)) != null) {

			// check what to do with this $ref/$rep relation

			ContextNode refRepContextNode = refRepRelation.getContextNode();
			XDI3Segment arcXri = refRepRelation.getArcXri();
			XDI3Segment targetContextNodeXri = refRepRelation.getTargetContextNodeXri();

			boolean doReplaceRefRepRelations = XDIDictionaryConstants.XRI_S_REP.equals(arcXri) || (XDIDictionaryConstants.XRI_S_REF.equals(arcXri) && Boolean.TRUE.equals(operation.getParameterAsBoolean(GetOperation.XRI_PARAMETER_DEREF)));
			boolean doIncludeRefRelations = (XDIDictionaryConstants.XRI_S_REF.equals(arcXri) && ! Boolean.TRUE.equals(operation.getParameterAsBoolean(GetOperation.XRI_PARAMETER_DEREF)));

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

		XDI3Segment contextNodeXri = targetAddress;
		
		addCompletedAddress(executionContext, contextNodeXri);

		// follow any $ref and $rep arcs

		XDI3Segment originalTargetAddress = targetAddress;
		XDI3Segment followedTargetAddress = originalTargetAddress;

		XDI3Segment tempTargetAddress;

		while (true) { 

			tempTargetAddress = followedTargetAddress;
			followedTargetAddress = followRefRepRelations(tempTargetAddress, operation, executionContext);

			if (followedTargetAddress == tempTargetAddress) break;

			if (log.isDebugEnabled()) log.debug("In message envelope: Followed " + tempTargetAddress + " to " + followedTargetAddress);
		}

		if (followedTargetAddress != originalTargetAddress) {

			targetAddress = followedTargetAddress;
		}

		// done

		return targetAddress;
	}

	@Override
	public XDI3Statement targetStatement(XDI3Statement targetStatement, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// are we operating on a $ref or $rep arc?

		if (targetStatement.isRelationStatement() &&
				(XDIDictionaryConstants.XRI_S_REF.equals(targetStatement.getPredicate()) ||
						XDIDictionaryConstants.XRI_S_REP.equals(targetStatement.getPredicate()))) {

			// don't do anything special

			if (log.isDebugEnabled()) log.debug("Not operating on $ref/$rep target statement: " + targetStatement);

			return targetStatement;
		}

		// remember that we completed this target

		XDI3Segment contextNodeXri;

		if (targetStatement.isContextNodeStatement()) 
			contextNodeXri = targetStatement.getTargetContextNodeXri();
		else
			contextNodeXri = targetStatement.getContextNodeXri();

		addCompletedAddress(executionContext, contextNodeXri);

		// follow any $ref and $rep arcs

		XDI3Segment originalTargetSubject = targetStatement.getSubject();
		XDI3Segment followedTargetSubject = originalTargetSubject;

		XDI3Segment tempTargetSubject;

		while (true) {

			tempTargetSubject = followedTargetSubject;
			followedTargetSubject = followRefRepRelations(tempTargetSubject, operation, executionContext);

			if (followedTargetSubject == tempTargetSubject) break;

			if (log.isDebugEnabled()) log.debug("In message envelope: Followed " + tempTargetSubject + " to " + followedTargetSubject);
		}

		if (followedTargetSubject != originalTargetSubject) {

			targetStatement = StatementUtil.fromComponents(followedTargetSubject, targetStatement.getPredicate(), targetStatement.getObject());
		}

		// done

		return targetStatement;
	}

	private XDI3Segment followRefRepRelations(XDI3Segment contextNodeXri, Operation operation, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment originalContextNodeXri = contextNodeXri;

		XDI3Segment localXri = XDIConstants.XRI_S_ROOT;

		while (! XDIConstants.XRI_S_ROOT.equals(contextNodeXri)) {

			// $get feedback to find $ref/$rep relations in context

			MessageResult feedbackMessageResult = this.feedbackFindRefRepRelationsInContext(contextNodeXri, operation, executionContext);

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

	private MessageResult feedbackOnSourceOfRefRepRelation(ContextNode refRepContextNode, Operation operation, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Initiating $get feedback on source of $ref/$rep relation: " + refRepContextNode);

		// prepare messaging target and message result

		AbstractMessagingTarget messagingTarget = (AbstractMessagingTarget) executionContext.getCurrentMessagingTarget();

		MessageResult feedbackMessageResult = new MessageResult();

		// prepare message

		Message feedbackMessage = MessagingCloneUtil.cloneMessage(operation.getMessage());
		feedbackMessage.deleteOperations();

		Operation feedbackOperation = feedbackMessage.createOperation(XDIMessagingConstants.XRI_S_GET, refRepContextNode.getXri());
		if (Boolean.TRUE.equals(operation.getParameterAsBoolean(GetOperation.XRI_PARAMETER_DEREF))) feedbackOperation.setParameter(GetOperation.XRI_PARAMETER_DEREF, Boolean.TRUE);

		// execute message

		Deque<Relation> tempRefRepRelations = getRefRepRelations(executionContext);
		resetRefRepRelations(executionContext);
		messagingTarget.execute(feedbackMessage, feedbackMessageResult, executionContext);
		putRefRepRelations(executionContext, tempRefRepRelations);

		// done

		if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Completed $get feedback on source of $ref/$rep relation: " + refRepContextNode + ", message result: " + feedbackMessageResult);

		return feedbackMessageResult;
	}

	private MessageResult feedbackFindRefRepRelationsInContext(XDI3Segment contextNodeXri, Operation operation, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Initiating $get feedback to find $ref/$rep relations in context: " + contextNodeXri);

		// prepare messaging target and message result

		AbstractMessagingTarget messagingTarget = (AbstractMessagingTarget) executionContext.getCurrentMessagingTarget();

		MessageResult feedbackMessageResult = new MessageResult();

		// prepare messages

		Message feedbackMessageRef = MessagingCloneUtil.cloneMessage(operation.getMessage());
		Message feedbackMessageRep = MessagingCloneUtil.cloneMessage(operation.getMessage());
		feedbackMessageRef.deleteOperations();
		feedbackMessageRep.deleteOperations();

		feedbackMessageRef.createOperation(XDIMessagingConstants.XRI_S_GET, StatementUtil.fromRelationComponents(contextNodeXri, XDIDictionaryConstants.XRI_S_REF, XDIConstants.XRI_S_VARIABLE));
		feedbackMessageRep.createOperation(XDIMessagingConstants.XRI_S_GET, StatementUtil.fromRelationComponents(contextNodeXri, XDIDictionaryConstants.XRI_S_REP, XDIConstants.XRI_S_VARIABLE));

		// execute messages

		Deque<Relation> tempRefRepRelations = getRefRepRelations(executionContext);
		resetRefRepRelations(executionContext);
		messagingTarget.execute(feedbackMessageRef, feedbackMessageResult, executionContext);
		messagingTarget.execute(feedbackMessageRep, feedbackMessageResult, executionContext);
		putRefRepRelations(executionContext, tempRefRepRelations);

		// done

		if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Completed $get feedback to find $ref/$rep relations in context: " + contextNodeXri + ", message result: " + feedbackMessageResult);

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

	private static void putRefRepRelations(ExecutionContext executionContext, Deque<Relation> referenceRelations) {

		executionContext.putOperationAttribute(EXECUTIONCONTEXT_KEY_REFREPRELATIONS_PER_OPERATION, referenceRelations);
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
