package xdi2.messaging.target.interceptor.impl;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

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
import xdi2.core.util.XRIUtil;
import xdi2.core.util.iterators.IteratorListMaker;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;
import xdi2.messaging.AddOperation;
import xdi2.messaging.GetOperation;
import xdi2.messaging.Message;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import xdi2.messaging.constants.XDIMessagingConstants;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.ExecutionContext;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.Prototype;
import xdi2.messaging.target.impl.graph.GraphMessagingTarget;
import xdi2.messaging.target.interceptor.AbstractInterceptor;
import xdi2.messaging.target.interceptor.OperationInterceptor;
import xdi2.messaging.target.interceptor.TargetInterceptor;
import xdi2.messaging.util.MessagingCloneUtil;

/**
 * This interceptor handles $ref and $ref! relations.
 * 
 * @author markus
 */
public class RefInterceptor extends AbstractInterceptor implements OperationInterceptor, TargetInterceptor, Prototype<RefInterceptor> {

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
	 * OperationInterceptor
	 */

	@Override
	public boolean before(Operation operation, MessageResult operationMessageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		resetReferenceRelations(executionContext);

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

				// don't follow $ref/$rep relations within operation target

				if (XDIConstants.XRI_S_ROOT.equals(operation.getTargetAddress()) || 
						XRIUtil.startsWith(targetContextNodeXri, operation.getTargetAddress())) {

					if (log.isDebugEnabled()) log.debug("In message result: Skipping $ref/$rep relation within operation target (" + operation.getTargetAddress() + "): " + refRepRelation);

					if (XDIDictionaryConstants.XRI_S_REP.equals(refRepRelation.getArcXri())) refRepRelation.delete();
					continue;
				}

				// delete the $ref/$rep relation, and perform a $get on its source

				refRepRelation.delete();
				refRepRelation.follow().deleteWhileEmpty();

				Message feedbackMessage = MessagingCloneUtil.cloneMessage(operation.getMessage());
				feedbackMessage.deleteOperations();

				Operation feedbackOperation = feedbackMessage.createOperation(XDIMessagingConstants.XRI_S_GET, refRepRelation.getContextNode().getXri());
				if (Boolean.TRUE.equals(operation.getParameterAsBoolean(GetOperation.XRI_PARAMETER_DEREF))) feedbackOperation.setParameter(GetOperation.XRI_PARAMETER_DEREF, Boolean.TRUE);

				Deque<Relation> tempRefRepRelations = getRefRepRelations(executionContext);
				resetReferenceRelations(executionContext);
				this.feedback(feedbackMessage, operationMessageResult, executionContext);
				putReferenceRelations(executionContext, tempRefRepRelations);

				// done with this $ref/$rep relation

				if (log.isDebugEnabled()) log.debug("In message result: We now have: " + operationMessageResult);
			}
		}

		// look through the message result to process followed $ref/$rep relations

		Relation refRepRelation;

		while ((refRepRelation = popRefRepRelation(executionContext)) != null) {

			// check what to do with this $ref/$rep relation

			ContextNode contextNode = refRepRelation.getContextNode();
			XDI3Segment arcXri = refRepRelation.getArcXri();
			XDI3Segment targetContextNodeXri = refRepRelation.getTargetContextNodeXri();

			boolean doReplaceRefRepRelations = XDIDictionaryConstants.XRI_S_REP.equals(arcXri) || (XDIDictionaryConstants.XRI_S_REF.equals(arcXri) && Boolean.TRUE.equals(operation.getParameterAsBoolean(GetOperation.XRI_PARAMETER_DEREF)));
			boolean doIncludeRefRelations = (XDIDictionaryConstants.XRI_S_REF.equals(arcXri) && ! Boolean.TRUE.equals(operation.getParameterAsBoolean(GetOperation.XRI_PARAMETER_DEREF)));

			// replace $ref/$rep relations?

			if (doReplaceRefRepRelations) {

				ContextNode targetContextNode = operationMessageResult.getGraph().findContextNode(targetContextNodeXri, false);

				if (targetContextNode != null && ! operationMessageResult.getGraph().isEmpty()) {

					if (log.isDebugEnabled()) log.debug("In message result: Replacing $ref/$rep relation: " + refRepRelation);

					Graph tempGraph = MemoryGraphFactory.getInstance().openGraph();
					ContextNode tempContextNode = tempGraph.findContextNode(contextNode.getXri(), true);
					CopyUtil.copyContextNodeContents(targetContextNode, tempContextNode, null);
					targetContextNode.clear();
					targetContextNode.deleteWhileEmpty();
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

		// find our graph

		MessagingTarget currentMessagingTarget = executionContext.getCurrentMessagingTarget();
		if (! (currentMessagingTarget instanceof GraphMessagingTarget)) return targetAddress;

		Graph graph = ((GraphMessagingTarget) currentMessagingTarget).getGraph();

		// apply following

		XDI3Segment originalTargetAddress = targetAddress;
		XDI3Segment followedTargetAddress = originalTargetAddress;

		XDI3Segment tempTargetAddress;

		while (true) { 

			tempTargetAddress = followedTargetAddress;
			followedTargetAddress = followRefRepRelations(tempTargetAddress, graph, executionContext);

			if (followedTargetAddress == tempTargetAddress) break;

			if (log.isDebugEnabled()) log.debug("In message envelope: Followed " + tempTargetAddress + " to " + followedTargetAddress);
		}

		if (followedTargetAddress != originalTargetAddress) {

			return followedTargetAddress;
		}

		// done

		return targetAddress;
	}

	@Override
	public XDI3Statement targetStatement(XDI3Statement targetStatement, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// find our graph

		MessagingTarget currentMessagingTarget = executionContext.getCurrentMessagingTarget();
		if (! (currentMessagingTarget instanceof GraphMessagingTarget)) return targetStatement;

		Graph graph = ((GraphMessagingTarget) currentMessagingTarget).getGraph();

		// are we operating on a $ref or $rep arc?

		if (targetStatement.isRelationStatement() &&
				(XDIDictionaryConstants.XRI_S_REF.equals(targetStatement.getPredicate()) ||
						XDIDictionaryConstants.XRI_S_REP.equals(targetStatement.getPredicate()))) {

			// cannot add a $ref or $rep arc to non-empty context node

			if (operation instanceof AddOperation) {

				XDI3Segment targetContextNodeXri = targetStatement.getContextNodeXri();
				ContextNode targetContextNode = graph.findContextNode(targetContextNodeXri, false);

				if (targetContextNode != null && ! targetContextNode.isEmpty()) {

					throw new Xdi2MessagingException("Cannot add $ref or $rep relation to non-empty context node " + targetContextNode.getXri(), null, executionContext);
				}
			}

			// don't do anything else if we are operating on $ref and $rep arcs

			return targetStatement;
		}

		// follow any $ref and $rep arcs

		XDI3Segment originalTargetSubject = targetStatement.getSubject();
		XDI3Segment followedTargetSubject = originalTargetSubject;

		XDI3Segment tempTargetSubject;

		while (true) {

			tempTargetSubject = followedTargetSubject;
			followedTargetSubject = followRefRepRelations(tempTargetSubject, graph, executionContext);

			if (followedTargetSubject == tempTargetSubject) break;

			if (log.isDebugEnabled()) log.debug("In message envelope: Followed " + tempTargetSubject + " to " + followedTargetSubject);
		}

		if (followedTargetSubject != originalTargetSubject) {

			return StatementUtil.fromComponents(followedTargetSubject, targetStatement.getPredicate(), targetStatement.getObject());
		}

		// done

		return targetStatement;
	}

	private static XDI3Segment followRefRepRelations(XDI3Segment contextNodeXri, Graph graph, ExecutionContext executionContext) throws Xdi2MessagingException {

		String localPart = "";

		XDI3Segment originalContextNodeXri = contextNodeXri;

		while (contextNodeXri != null) {

			ContextNode contextNode = graph.findContextNode(contextNodeXri, false);
			Relation refRelation = contextNode == null ? null : Equivalence.getReferenceRelation(contextNode);
			Relation repRelation = contextNode == null ? null : Equivalence.getReplacementRelation(contextNode);

			if (refRelation != null) {

				ContextNode canonicalContextNode = refRelation.follow();
				if (canonicalContextNode.equals(contextNode)) break;

				pushReferenceRelation(executionContext, refRelation);

				if (canonicalContextNode.isRootContextNode())
					return XDI3Segment.create("" + (localPart.isEmpty() ? XDIConstants.XRI_S_ROOT : localPart));
				else
					return XDI3Segment.create(canonicalContextNode.getXri() + localPart);
			}

			if (repRelation != null) {

				ContextNode privateCanonicalContextNode  = repRelation.follow();
				if (repRelation.equals(privateCanonicalContextNode)) break;

				pushReferenceRelation(executionContext, repRelation);

				if (privateCanonicalContextNode.isRootContextNode())
					return XDI3Segment.create("" + (localPart.isEmpty() ? XDIConstants.XRI_S_ROOT : localPart));
				else
					return XDI3Segment.create(privateCanonicalContextNode.getXri() + localPart);
			}

			localPart = "" + XRIUtil.localXri(contextNodeXri, 1) + localPart;
			contextNodeXri = XRIUtil.parentXri(contextNodeXri, -1);
		}

		// done

		return originalContextNodeXri;
	}

	/*
	 * ExecutionContext helper methods
	 */

	private static final String EXECUTIONCONTEXT_KEY_REFERENCERELATIONS_PER_OPERATION = RefInterceptor.class.getCanonicalName() + "#referencerelationsperoperation";

	@SuppressWarnings("unchecked")
	private static Deque<Relation> getRefRepRelations(ExecutionContext executionContext) {

		return (Deque<Relation>) executionContext.getMessageEnvelopeAttribute(EXECUTIONCONTEXT_KEY_REFERENCERELATIONS_PER_OPERATION);
	}

	private static void putReferenceRelations(ExecutionContext executionContext, Deque<Relation> referenceRelations) {

		executionContext.putMessageEnvelopeAttribute(EXECUTIONCONTEXT_KEY_REFERENCERELATIONS_PER_OPERATION, referenceRelations);
	}

	private static Relation popRefRepRelation(ExecutionContext executionContext) {

		Deque<Relation> referenceRelations = getRefRepRelations(executionContext);
		if (referenceRelations.isEmpty()) return null;

		Relation referenceRelation = referenceRelations.pop();

		if (log.isDebugEnabled()) log.debug("Popping reference relation: " + referenceRelation);

		return referenceRelation;
	}

	private static void pushReferenceRelation(ExecutionContext executionContext, Relation referenceRelation) {

		getRefRepRelations(executionContext).push(referenceRelation);

		if (log.isDebugEnabled()) log.debug("Pushing reference relation: " + referenceRelation);
	}

	private static void resetReferenceRelations(ExecutionContext executionContext) {

		executionContext.putMessageEnvelopeAttribute(EXECUTIONCONTEXT_KEY_REFERENCERELATIONS_PER_OPERATION, new ArrayDeque<Relation> ());
	}
}
