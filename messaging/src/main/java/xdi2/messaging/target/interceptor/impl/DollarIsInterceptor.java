package xdi2.messaging.target.interceptor.impl;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Relation;
import xdi2.core.Statement;
import xdi2.core.Statement.RelationStatement;
import xdi2.core.constants.XDIConstants;
import xdi2.core.constants.XDIDictionaryConstants;
import xdi2.core.features.dictionary.Dictionary;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.util.CopyUtil;
import xdi2.core.util.StatementUtil;
import xdi2.core.util.XRIUtil;
import xdi2.core.util.iterators.IteratorListMaker;
import xdi2.core.xri3.impl.XDI3Segment;
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
 * This interceptor handles $is and $is! relations.
 * 
 * @author markus
 */
public class DollarIsInterceptor extends AbstractInterceptor implements OperationInterceptor, TargetInterceptor, Prototype<DollarIsInterceptor> {

	private static final Logger log = LoggerFactory.getLogger(DollarIsInterceptor.class);

	/*
	 * Prototype
	 */

	@Override
	public DollarIsInterceptor instanceFor(PrototypingContext prototypingContext) {

		// done

		return this;
	}

	/*
	 * OperationInterceptor
	 */

	@Override
	public boolean before(Operation operation, MessageResult operationMessageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		resetEquivalenceRelations(executionContext);

		return false;
	}

	@Override
	public boolean after(Operation operation, MessageResult operationMessageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// look through the message result to process result equivalence relations

		List<Relation> equivalenceRelations = new IteratorListMaker<Relation> (Dictionary.getEquivalenceRelations(operationMessageResult.getGraph())).list();

		for (Relation equivalenceRelation : equivalenceRelations) {

			if (log.isDebugEnabled()) log.debug("In message result: Found equivalence relation: " + equivalenceRelation);

			// don't follow equivalence relations to parent nodes

			if (XRIUtil.startsWith(equivalenceRelation.getTargetContextNodeXri(), equivalenceRelation.getContextNode().getXri())) {

				if (log.isDebugEnabled()) log.debug("In message result: Skipping equivalence relation: " + equivalenceRelation);

				if (XDIDictionaryConstants.XRI_S_IS_BANG.equals(equivalenceRelation.getArcXri())) equivalenceRelation.delete();
				continue;
			}

			// delete the equivalence relation, and perform a $get on its source

			equivalenceRelation.delete();
			equivalenceRelation.follow().deleteWhileEmpty();

			Message feedbackMessage = MessagingCloneUtil.cloneMessage(operation.getMessage());
			feedbackMessage.deleteOperations();
			feedbackMessage.createOperation(new XDI3Segment("" + XDIMessagingConstants.XRI_S_GET + (operation.getOperationExtensionXri() == null ? "" : operation.getOperationExtensionXri())), equivalenceRelation.getContextNode().getXri());
			Deque<Relation> tempEquivalenceRelations = getEquivalenceRelations(executionContext);
			resetEquivalenceRelations(executionContext);
			this.feedback(feedbackMessage, operationMessageResult, executionContext);
			putEquivalenceRelations(executionContext, tempEquivalenceRelations);

			// done with this equivalence relation

			if (log.isDebugEnabled()) log.debug("In message result: We now have: " + operationMessageResult);
		}

		// look through the message result to process followed equivalence relations

		Relation equivalenceRelation;

		while ((equivalenceRelation = popEquivalenceRelation(executionContext)) != null) {

			// check what to do with this equivalence relation

			ContextNode contextNode = equivalenceRelation.getContextNode();
			XDI3Segment arcXri = equivalenceRelation.getArcXri();
			XDI3Segment targetContextNodeXri = equivalenceRelation.getTargetContextNodeXri();

			boolean doSubstituteEquivalenceRelations = XDIDictionaryConstants.XRI_S_IS_BANG.equals(arcXri) || (XDIDictionaryConstants.XRI_S_IS.equals(arcXri) && GetOperation.XRI_EXTENSION_BANG.equals(operation.getOperationExtensionXri()));
			boolean doIncludeEquivalenceRelations = (XDIDictionaryConstants.XRI_S_IS.equals(arcXri) && ! GetOperation.XRI_EXTENSION_BANG.equals(operation.getOperationExtensionXri()));

			// substitute equivalence relations?

			if (doSubstituteEquivalenceRelations) {

				ContextNode targetContextNode = operationMessageResult.getGraph().findContextNode(targetContextNodeXri, false);

				if (targetContextNode != null && ! operationMessageResult.getGraph().isEmpty()) {

					if (log.isDebugEnabled()) log.debug("In message result: Substituting equivalence relation: " + equivalenceRelation);

					Graph tempGraph = MemoryGraphFactory.getInstance().openGraph();
					ContextNode tempContextNode = tempGraph.findContextNode(contextNode.getXri(), true);
					CopyUtil.copyContextNodeContents(targetContextNode, tempContextNode, null);
					targetContextNode.clear();
					targetContextNode.deleteWhileEmpty();
					CopyUtil.copyGraph(tempGraph, operationMessageResult.getGraph(), null);
				} else {

					if (log.isDebugEnabled()) log.debug("In message result: Not substituting equivalence relation: " + equivalenceRelation);
				}
			}

			// include equivalence relations?

			if (doIncludeEquivalenceRelations) {

				if (operationMessageResult.getGraph().containsStatement(equivalenceRelation.getStatement())) {

					if (log.isDebugEnabled()) log.debug("In message result: Not including duplicate equivalence relation: " + equivalenceRelation);
				} else {

					if (log.isDebugEnabled()) log.debug("In message result: Including equivalence relation: " + equivalenceRelation);

					operationMessageResult.getGraph().createStatement(equivalenceRelation.getStatement());
				}
			}

			// done with this equivalence relation

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
			followedTargetAddress = followEquivalenceRelations(tempTargetAddress, graph, executionContext);

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
	public Statement targetStatement(Statement targetStatement, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// find our graph

		MessagingTarget currentMessagingTarget = executionContext.getCurrentMessagingTarget();
		if (! (currentMessagingTarget instanceof GraphMessagingTarget)) return targetStatement;

		Graph graph = ((GraphMessagingTarget) currentMessagingTarget).getGraph();

		// are we operating on a $is or $is! arc?

		if (targetStatement instanceof RelationStatement &&
				(XDIDictionaryConstants.XRI_S_IS.equals(targetStatement.getPredicate()) ||
						XDIDictionaryConstants.XRI_S_IS_BANG.equals(targetStatement.getPredicate()))) {

			// cannot add a $is or $is! arc to non-empty context node

			if (operation instanceof AddOperation) {

				XDI3Segment targetContextNodeXri = targetStatement.getContextNodeXri();
				ContextNode targetContextNode = graph.findContextNode(targetContextNodeXri, false);

				if (targetContextNode != null && ! targetContextNode.isEmpty()) {

					throw new Xdi2MessagingException("Cannot add canonical $is or $is! relation to non-empty context node " + targetContextNode.getXri(), null, executionContext);
				}
			}

			// don't do anything else if we are operating on $is and $is! arcs

			return targetStatement;
		}

		// apply following

		XDI3Segment originalTargetSubject = targetStatement.getSubject();
		XDI3Segment followedTargetSubject = originalTargetSubject;

		XDI3Segment tempTargetSubject;

		while (true) {

			tempTargetSubject = followedTargetSubject;
			followedTargetSubject = followEquivalenceRelations(tempTargetSubject, graph, executionContext);

			if (followedTargetSubject == tempTargetSubject) break;

			if (log.isDebugEnabled()) log.debug("In message envelope: Followed " + tempTargetSubject + " to " + followedTargetSubject);
		}

		if (followedTargetSubject != originalTargetSubject) {

			return StatementUtil.fromComponents(followedTargetSubject, targetStatement.getPredicate(), targetStatement.getObject());
		}

		// done

		return targetStatement;
	}

	private static XDI3Segment followEquivalenceRelations(XDI3Segment contextNodeXri, Graph graph, ExecutionContext executionContext) throws Xdi2MessagingException {

		String localPart = "";

		XDI3Segment originalContextNodeXri = contextNodeXri;

		while (contextNodeXri != null) {

			ContextNode contextNode = graph.findContextNode(contextNodeXri, false);
			Relation canonicalRelation = contextNode == null ? null : Dictionary.getCanonicalRelation(contextNode);
			Relation privateCanonicalRelation = contextNode == null ? null : Dictionary.getPrivateCanonicalRelation(contextNode);

			if (canonicalRelation != null) {

				if (canonicalRelation.equals(contextNode)) break;

				ContextNode canonicalContextNode = canonicalRelation.follow();
				pushEquivalenceRelation(executionContext, canonicalRelation);

				if (canonicalContextNode.isRootContextNode())
					return new XDI3Segment("" + (localPart.isEmpty() ? XDIConstants.XRI_S_ROOT : localPart));
				else
					return new XDI3Segment(canonicalContextNode.getXri() + localPart);
			}

			if (privateCanonicalRelation != null) {

				if (privateCanonicalRelation.equals(contextNode)) break;

				ContextNode privateCanonicalContextNode  = privateCanonicalRelation.follow();
				pushEquivalenceRelation(executionContext, privateCanonicalRelation);

				if (privateCanonicalContextNode.isRootContextNode())
					return new XDI3Segment("" + (localPart.isEmpty() ? XDIConstants.XRI_S_ROOT : localPart));
				else
					return new XDI3Segment(privateCanonicalContextNode.getXri() + localPart);
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

	private static final String EXECUTIONCONTEXT_KEY_EQUIVALENCERELATIONS_PER_OPERATION = DollarIsInterceptor.class.getCanonicalName() + "#equivalencerelationsperoperation";

	@SuppressWarnings("unchecked")
	private static Deque<Relation> getEquivalenceRelations(ExecutionContext executionContext) {

		return (Deque<Relation>) executionContext.getMessageEnvelopeAttribute(EXECUTIONCONTEXT_KEY_EQUIVALENCERELATIONS_PER_OPERATION);
	}

	private static void putEquivalenceRelations(ExecutionContext executionContext, Deque<Relation> equivalenceRelations) {

		executionContext.putMessageEnvelopeAttribute(EXECUTIONCONTEXT_KEY_EQUIVALENCERELATIONS_PER_OPERATION, equivalenceRelations);
	}

	private static Relation popEquivalenceRelation(ExecutionContext executionContext) {

		Deque<Relation> equivalenceRelations = getEquivalenceRelations(executionContext);
		if (equivalenceRelations.isEmpty()) return null;

		Relation equivalenceRelation = equivalenceRelations.pop();

		if (log.isDebugEnabled()) log.debug("Popping equivalence relation: " + equivalenceRelation);

		return equivalenceRelation;
	}

	private static void pushEquivalenceRelation(ExecutionContext executionContext, Relation equivalenceRelation) {

		getEquivalenceRelations(executionContext).push(equivalenceRelation);

		if (log.isDebugEnabled()) log.debug("Pushing equivalence relation: " + equivalenceRelation);
	}

	private static void resetEquivalenceRelations(ExecutionContext executionContext) {

		executionContext.putMessageEnvelopeAttribute(EXECUTIONCONTEXT_KEY_EQUIVALENCERELATIONS_PER_OPERATION, new ArrayDeque<Relation> ());
	}

	/*	private static class Statement implements Serializable, Comparable<Statement> {

		private static final long serialVersionUID = -8103575725476096458L;

		private XDI3Segment contextNodeXri;
		private XDI3Segment arcXri;
		private XDI3Segment followedContextNodeXri;

		public Statement(XDI3Segment contextNodeXri, XDI3Segment arcXri, XDI3Segment followedContextNodeXri) {

			this.contextNodeXri = contextNodeXri;
			this.arcXri = arcXri;
			this.followedContextNodeXri = followedContextNodeXri;
		}

		public XDI3Segment getContextNodeXri() {

			return this.contextNodeXri;
		}

		public XDI3Segment getArcXri() {

			return this.arcXri;
		}

		public XDI3Segment getFollowedContextNodeXri() {

			return this.followedContextNodeXri;
		}

		@Override
		public String toString() {

			return this.getContextNodeXri() + " --> " + this.getArcXri() + " --> " + this.getFollowedContextNodeXri();
		}

		@Override
		public boolean equals(Object object) {

			if (this == object) return true;
			if (object == null) return false;

			Statement other = (Statement) object;

			if (! this.contextNodeXri.equals(other.contextNodeXri)) return false;
			if (! this.arcXri.equals(other.arcXri)) return false;
			if (! this.followedContextNodeXri.equals(other.followedContextNodeXri)) return false;

			return true;
		}

		@Override
		public int hashCode() {

			int hashCode = 1;

			hashCode = (hashCode * 31) + this.getContextNodeXri().hashCode();
			hashCode = (hashCode * 31) + this.getArcXri().hashCode();
			hashCode = (hashCode * 31) + this.getFollowedContextNodeXri().hashCode();

			return hashCode;
		}

		@Override
		public int compareTo(Statement other) {

			if (other == null || other == this) return 0;

			int compare;

			if ((compare = this.getContextNodeXri().compareTo(other.getContextNodeXri())) != 0) return compare;
			if ((compare = this.getArcXri().compareTo(other.getArcXri())) != 0) return compare;
			if ((compare = this.getFollowedContextNodeXri().compareTo(other.getFollowedContextNodeXri())) != 0) return compare;

			return 0;
		}
	}*/
}
