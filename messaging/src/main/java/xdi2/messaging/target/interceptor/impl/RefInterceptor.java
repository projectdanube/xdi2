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

		// look through the message result to process result reference relations

		XDI3Segment operationContextNodeXri = operation.getTargetAddress() != null ? operation.getTargetAddress() : operation.getTargetStatement().getContextNodeXri();

		List<Relation> referenceRelations = new IteratorListMaker<Relation> (Equivalence.getAllReferenceAndPrivateReferenceRelations(operationMessageResult.getGraph().getRootContextNode())).list();

		for (Relation referenceRelation : referenceRelations) {

			if (log.isDebugEnabled()) log.debug("In message result: Found reference relation: " + referenceRelation);

			XDI3Segment targetContextNodeXri = referenceRelation.getTargetContextNodeXri();
			
			// don't follow reference relations within operation target

			if (XDIConstants.XRI_S_ROOT.equals(operationContextNodeXri) || XRIUtil.startsWith(targetContextNodeXri, operationContextNodeXri)) {

				if (log.isDebugEnabled()) log.debug("In message result: Skipping reference relation within operation target (" + operationContextNodeXri + "): " + referenceRelation);

				if (XDIDictionaryConstants.XRI_S_REF_BANG.equals(referenceRelation.getArcXri())) referenceRelation.delete();
				continue;
			}

			// delete the reference relation, and perform a $get on its source

			referenceRelation.delete();
			referenceRelation.follow().deleteWhileEmpty();

			Message feedbackMessage = MessagingCloneUtil.cloneMessage(operation.getMessage());
			feedbackMessage.deleteOperations();
			feedbackMessage.createOperation(XDI3Segment.create("" + XDIMessagingConstants.XRI_S_GET + (operation.getOperationExtensionXri() == null ? "" : operation.getOperationExtensionXri())), referenceRelation.getContextNode().getXri());
			Deque<Relation> tempEquivalenceRelations = getReferenceRelations(executionContext);
			resetReferenceRelations(executionContext);
			this.feedback(feedbackMessage, operationMessageResult, executionContext);
			putReferenceRelations(executionContext, tempEquivalenceRelations);

			// done with this reference relation

			if (log.isDebugEnabled()) log.debug("In message result: We now have: " + operationMessageResult);
		}

		// look through the message result to process followed reference relations

		Relation referenceRelation;

		while ((referenceRelation = popReferenceRelation(executionContext)) != null) {

			// check what to do with this reference relation

			ContextNode contextNode = referenceRelation.getContextNode();
			XDI3Segment arcXri = referenceRelation.getArcXri();
			XDI3Segment targetContextNodeXri = referenceRelation.getTargetContextNodeXri();

			boolean doSubstituteReferenceRelations = XDIDictionaryConstants.XRI_S_REF_BANG.equals(arcXri) || (XDIDictionaryConstants.XRI_S_REF.equals(arcXri) && GetOperation.XRI_EXTENSION_BANG.equals(operation.getOperationExtensionXri()));
			boolean doIncludeReferenceRelations = (XDIDictionaryConstants.XRI_S_REF.equals(arcXri) && ! GetOperation.XRI_EXTENSION_BANG.equals(operation.getOperationExtensionXri()));

			// substitute reference relations?

			if (doSubstituteReferenceRelations) {

				ContextNode targetContextNode = operationMessageResult.getGraph().findContextNode(targetContextNodeXri, false);

				if (targetContextNode != null && ! operationMessageResult.getGraph().isEmpty()) {

					if (log.isDebugEnabled()) log.debug("In message result: Substituting reference relation: " + referenceRelation);

					Graph tempGraph = MemoryGraphFactory.getInstance().openGraph();
					ContextNode tempContextNode = tempGraph.findContextNode(contextNode.getXri(), true);
					CopyUtil.copyContextNodeContents(targetContextNode, tempContextNode, null);
					targetContextNode.clear();
					targetContextNode.deleteWhileEmpty();
					CopyUtil.copyGraph(tempGraph, operationMessageResult.getGraph(), null);
				} else {

					if (log.isDebugEnabled()) log.debug("In message result: Not substituting reference relation: " + referenceRelation);
				}
			}

			// include reference relations?

			if (doIncludeReferenceRelations) {

				if (operationMessageResult.getGraph().containsStatement(referenceRelation.getStatement().getXri())) {

					if (log.isDebugEnabled()) log.debug("In message result: Not including duplicate reference relation: " + referenceRelation);
				} else {

					if (log.isDebugEnabled()) log.debug("In message result: Including reference relation: " + referenceRelation);

					CopyUtil.copyStatement(referenceRelation.getStatement(), operationMessageResult.getGraph(), null);
				}
			}

			// done with this reference relation

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
			followedTargetAddress = followReferenceRelations(tempTargetAddress, graph, executionContext);

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

		// are we operating on a $ref or $ref! arc?

		if (targetStatement.isRelationStatement() &&
				(XDIDictionaryConstants.XRI_S_REF.equals(targetStatement.getPredicate()) ||
						XDIDictionaryConstants.XRI_S_REF_BANG.equals(targetStatement.getPredicate()))) {

			// cannot add a $ref or $ref! arc to non-empty context node

			if (operation instanceof AddOperation) {

				XDI3Segment targetContextNodeXri = targetStatement.getContextNodeXri();
				ContextNode targetContextNode = graph.findContextNode(targetContextNodeXri, false);

				if (targetContextNode != null && ! targetContextNode.isEmpty()) {

					throw new Xdi2MessagingException("Cannot add canonical $ref or $ref! relation to non-empty context node " + targetContextNode.getXri(), null, executionContext);
				}
			}

			// don't do anything else if we are operating on $ref and $ref! arcs

			return targetStatement;
		}

		// apply following

		XDI3Segment originalTargetSubject = targetStatement.getSubject();
		XDI3Segment followedTargetSubject = originalTargetSubject;

		XDI3Segment tempTargetSubject;

		while (true) {

			tempTargetSubject = followedTargetSubject;
			followedTargetSubject = followReferenceRelations(tempTargetSubject, graph, executionContext);

			if (followedTargetSubject == tempTargetSubject) break;

			if (log.isDebugEnabled()) log.debug("In message envelope: Followed " + tempTargetSubject + " to " + followedTargetSubject);
		}

		if (followedTargetSubject != originalTargetSubject) {

			return StatementUtil.fromComponents(followedTargetSubject, targetStatement.getPredicate(), targetStatement.getObject());
		}

		// done

		return targetStatement;
	}

	private static XDI3Segment followReferenceRelations(XDI3Segment contextNodeXri, Graph graph, ExecutionContext executionContext) throws Xdi2MessagingException {

		String localPart = "";

		XDI3Segment originalContextNodeXri = contextNodeXri;

		while (contextNodeXri != null) {

			ContextNode contextNode = graph.findContextNode(contextNodeXri, false);
			Relation referenceRelation = contextNode == null ? null : Equivalence.getReferenceRelation(contextNode);
			Relation privateReferenceRelation = contextNode == null ? null : Equivalence.getPrivateReferenceRelation(contextNode);

			if (referenceRelation != null) {

				ContextNode canonicalContextNode = referenceRelation.follow();
				if (canonicalContextNode.equals(contextNode)) break;

				pushReferenceRelation(executionContext, referenceRelation);

				if (canonicalContextNode.isRootContextNode())
					return XDI3Segment.create("" + (localPart.isEmpty() ? XDIConstants.XRI_S_ROOT : localPart));
				else
					return XDI3Segment.create(canonicalContextNode.getXri() + localPart);
			}

			if (privateReferenceRelation != null) {

				ContextNode privateCanonicalContextNode  = privateReferenceRelation.follow();
				if (privateReferenceRelation.equals(privateCanonicalContextNode)) break;

				pushReferenceRelation(executionContext, privateReferenceRelation);

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
	private static Deque<Relation> getReferenceRelations(ExecutionContext executionContext) {

		return (Deque<Relation>) executionContext.getMessageEnvelopeAttribute(EXECUTIONCONTEXT_KEY_REFERENCERELATIONS_PER_OPERATION);
	}

	private static void putReferenceRelations(ExecutionContext executionContext, Deque<Relation> referenceRelations) {

		executionContext.putMessageEnvelopeAttribute(EXECUTIONCONTEXT_KEY_REFERENCERELATIONS_PER_OPERATION, referenceRelations);
	}

	private static Relation popReferenceRelation(ExecutionContext executionContext) {

		Deque<Relation> referenceRelations = getReferenceRelations(executionContext);
		if (referenceRelations.isEmpty()) return null;

		Relation referenceRelation = referenceRelations.pop();

		if (log.isDebugEnabled()) log.debug("Popping reference relation: " + referenceRelation);

		return referenceRelation;
	}

	private static void pushReferenceRelation(ExecutionContext executionContext, Relation referenceRelation) {

		getReferenceRelations(executionContext).push(referenceRelation);

		if (log.isDebugEnabled()) log.debug("Pushing reference relation: " + referenceRelation);
	}

	private static void resetReferenceRelations(ExecutionContext executionContext) {

		executionContext.putMessageEnvelopeAttribute(EXECUTIONCONTEXT_KEY_REFERENCERELATIONS_PER_OPERATION, new ArrayDeque<Relation> ());
	}
}
