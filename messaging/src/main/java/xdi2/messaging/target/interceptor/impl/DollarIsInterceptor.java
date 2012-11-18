package xdi2.messaging.target.interceptor.impl;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Statement;
import xdi2.core.Statement.RelationStatement;
import xdi2.core.constants.XDIDictionaryConstants;
import xdi2.core.features.dictionary.Dictionary;
import xdi2.core.util.CopyUtil;
import xdi2.core.util.StatementUtil;
import xdi2.core.util.XRIUtil;
import xdi2.core.util.iterators.IteratorListMaker;
import xdi2.core.xri3.impl.XDI3Segment;
import xdi2.messaging.AddOperation;
import xdi2.messaging.GetOperation;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.ExecutionContext;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.Prototype;
import xdi2.messaging.target.impl.graph.GraphMessagingTarget;
import xdi2.messaging.target.interceptor.AbstractInterceptor;
import xdi2.messaging.target.interceptor.MessageEnvelopeInterceptor;
import xdi2.messaging.target.interceptor.OperationInterceptor;
import xdi2.messaging.target.interceptor.TargetInterceptor;
import xdi2.messaging.util.MessagingCloneUtil;

/**
 * This interceptor handles $is and $is! relations.
 * 
 * @author markus
 */
public class DollarIsInterceptor extends AbstractInterceptor implements MessageEnvelopeInterceptor, OperationInterceptor, TargetInterceptor, Prototype<DollarIsInterceptor> {

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
	 * MessageEnvelopeInterceptor
	 */

	@Override
	public boolean before(MessageEnvelope messageEnvelope, MessageResult operationMessageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	@Override
	public boolean after(MessageEnvelope messageEnvelope, MessageResult operationMessageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

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

		resetEquivalences(executionContext);

		return false;
	}

	@Override
	public boolean after(Operation operation, MessageResult operationMessageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// look through the message result to apply following

		List<Statement> statements = new IteratorListMaker<Statement> (Dictionary.getEquivalenceStatements(operationMessageResult.getGraph())).list();

		for (Statement statement : statements) {

			Message feedbackMessage = MessagingCloneUtil.cloneMessage(operation.getMessage());
			feedbackMessage.deleteOperations();
			feedbackMessage.createOperation(operation.getOperationXri(), statement.getSubject());

			((RelationStatement) statement).getRelation().follow().deleteUntilEmpty();

//			MessageResult feedbackMessageResult = new MessageResult();
			Deque<Equivalence> equivalences = getEquivalences(executionContext);
			this.feedback(feedbackMessage, operationMessageResult, executionContext);
			putEquivalences(executionContext, equivalences);
//			CopyUtil.copyGraph(feedbackMessageResult.getGraph(), operationMessageResult.getGraph(), null);
		}

		// look through the message result for post-following actions

		Equivalence equivalence;

		while ((equivalence = popEquivalence(executionContext)) != null) {

			XDI3Segment contextNodeXri = equivalence.getContextNodeXri();
			XDI3Segment arcXri = equivalence.getArcXri();
			XDI3Segment followedContextNodeXri = equivalence.getFollowedContextNodeXri();

			ContextNode followedContextNode = operationMessageResult.getGraph().findContextNode(followedContextNodeXri, false);
			if (followedContextNode == null) continue;

			boolean doSubstituteEquivalenceStatements = XDIDictionaryConstants.XRI_S_IS_BANG.equals(arcXri) || (XDIDictionaryConstants.XRI_S_IS.equals(arcXri) && GetOperation.XRI_EXTENSION_BANG.equals(operation.getOperationExtensionXri()));
			boolean doIncludeEquivalenceStatements = (XDIDictionaryConstants.XRI_S_IS.equals(arcXri) && ! GetOperation.XRI_EXTENSION_BANG.equals(operation.getOperationExtensionXri()));

			// substitute equivalence statements?

			if (doSubstituteEquivalenceStatements) {

				if (log.isDebugEnabled()) log.debug("In message result: Substituting equivalence: " + equivalence);

				ContextNode contextNode = operationMessageResult.getGraph().findContextNode(contextNodeXri, true);
				CopyUtil.copyContextNodeContents(followedContextNode, contextNode, null);
				followedContextNode.deleteUntilEmpty();
			}

			// include equivalence statements?

			if (doIncludeEquivalenceStatements) {

				if (log.isDebugEnabled()) log.debug("In message result: Including equivalence: " + equivalence);

				operationMessageResult.getGraph().addStatement(StatementUtil.fromComponents(contextNodeXri, XDIDictionaryConstants.XRI_S_IS, followedContextNodeXri));
			}
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
			followedTargetAddress = followEquivalences(tempTargetAddress, graph, executionContext);

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

		// are we adding a $is arc or $is! arc to a non-empty context?

		if (operation instanceof AddOperation &&
				targetStatement instanceof RelationStatement &&
				(XDIDictionaryConstants.XRI_S_IS.equals(targetStatement.getPredicate()) ||
						XDIDictionaryConstants.XRI_S_IS_BANG.equals(targetStatement.getPredicate()))) {

			XDI3Segment targetContextNodeXri = targetStatement.getContextNodeXri();
			ContextNode targetContextNode = graph.findContextNode(targetContextNodeXri, false);

			// check if the context is empty

			if (targetContextNode != null && ! targetContextNode.isEmpty()) {

				throw new Xdi2MessagingException("Cannot add canonical $is or $is! relation to non-empty context node " + targetContextNode.getXri(), null, executionContext);
			}
		}

		// apply following

		XDI3Segment originalTargetSubject = targetStatement.getSubject();
		XDI3Segment followedTargetSubject = originalTargetSubject;

		XDI3Segment tempTargetSubject;

		while (true) {

			tempTargetSubject = followedTargetSubject;
			followedTargetSubject = followEquivalences(tempTargetSubject, graph, executionContext);

			if (followedTargetSubject == tempTargetSubject) break;

			if (log.isDebugEnabled()) log.debug("In message envelope: Followed " + tempTargetSubject + " to " + followedTargetSubject);
		}

		if (followedTargetSubject != originalTargetSubject) {

			return StatementUtil.fromComponents(followedTargetSubject, targetStatement.getPredicate(), targetStatement.getObject());
		}

		// done

		return targetStatement;
	}

	private static XDI3Segment followEquivalences(XDI3Segment contextNodeXri, Graph graph, ExecutionContext executionContext) throws Xdi2MessagingException {

		String localPart = "";

		XDI3Segment originalContextNodeXri = contextNodeXri;

		while (contextNodeXri != null) {

			ContextNode contextNode = graph.findContextNode(contextNodeXri, false);
			ContextNode canonicalContextNode = contextNode == null ? null : Dictionary.getCanonicalContextNode(contextNode);
			ContextNode privateCanonicalContextNode = contextNode == null ? null : Dictionary.getPrivateCanonicalContextNode(contextNode);

			if (canonicalContextNode != null) {

				if (canonicalContextNode.equals(contextNode)) break;

				XDI3Segment followedContextNodeXri = canonicalContextNode.getXri();
				pushEquivalence(executionContext, new Equivalence(contextNodeXri, XDIDictionaryConstants.XRI_S_IS, followedContextNodeXri));

				return new XDI3Segment("" + canonicalContextNode.getXri() + localPart);
			}

			if (privateCanonicalContextNode != null) {

				if (privateCanonicalContextNode.equals(contextNode)) break;

				XDI3Segment privateFollowedContextNodeXri = privateCanonicalContextNode.getXri();
				pushEquivalence(executionContext, new Equivalence(contextNodeXri, XDIDictionaryConstants.XRI_S_IS_BANG, privateFollowedContextNodeXri));

				return new XDI3Segment("" + privateCanonicalContextNode.getXri() + localPart);
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

	private static final String EXECUTIONCONTEXT_KEY_EQUIVALENCES_PER_OPERATION = DollarIsInterceptor.class.getCanonicalName() + "#equivalencesperoperation";

	@SuppressWarnings("unchecked")
	private static Deque<Equivalence> getEquivalences(ExecutionContext executionContext) {

		return (Deque<Equivalence>) executionContext.getMessageEnvelopeAttribute(EXECUTIONCONTEXT_KEY_EQUIVALENCES_PER_OPERATION);
	}

	private static void putEquivalences(ExecutionContext executionContext, Deque<Equivalence> equivalences) {

		executionContext.putMessageEnvelopeAttribute(EXECUTIONCONTEXT_KEY_EQUIVALENCES_PER_OPERATION, equivalences);
	}

	private static Equivalence popEquivalence(ExecutionContext executionContext) {

		Deque<Equivalence> equivalences = getEquivalences(executionContext);
		if (equivalences.isEmpty()) return null;

		Equivalence equivalence = equivalences.pop();

		if (log.isDebugEnabled()) log.debug("Popping equivalence: " + equivalence);

		return equivalence;
	}

	private static void pushEquivalence(ExecutionContext executionContext, Equivalence equivalence) {

		getEquivalences(executionContext).push(equivalence);

		if (log.isDebugEnabled()) log.debug("Pushing equivalence: " + equivalence);
	}

	private static void resetEquivalences(ExecutionContext executionContext) {

		executionContext.putMessageEnvelopeAttribute(EXECUTIONCONTEXT_KEY_EQUIVALENCES_PER_OPERATION, new ArrayDeque<Equivalence> ());
	}

	private static class Equivalence {

		private XDI3Segment contextNodeXri;
		private XDI3Segment arcXri;
		private XDI3Segment followedContextNodeXri;

		public Equivalence(XDI3Segment contextNodeXri, XDI3Segment arcXri, XDI3Segment followedContextNodeXri) {

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
	}
}
