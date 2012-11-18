package xdi2.messaging.target.interceptor.impl;

import java.util.AbstractMap;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.Map;

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
import xdi2.core.xri3.impl.XDI3Segment;
import xdi2.messaging.AddOperation;
import xdi2.messaging.GetOperation;
import xdi2.messaging.Message;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
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
 * This interceptor makes sure only one outgoing $is arc can exist on any context.
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

		resetSubstitutions(executionContext);

		return false;
	}

	@Override
	public boolean after(Operation operation, MessageResult operationMessageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// look through the message result to apply substitution

		for (Iterator<Statement> statements = operationMessageResult.getGraph().getRootContextNode().getAllStatements(); statements.hasNext(); ) {

			Statement statement = statements.next();

			if (statement instanceof RelationStatement &&
					(XDIDictionaryConstants.XRI_S_IS.equals(((RelationStatement) statement).getPredicate()) ||
							XDIDictionaryConstants.XRI_S_IS_BANG.equals(((RelationStatement) statement).getPredicate()))) {

				Message feedbackMessage = MessagingCloneUtil.cloneMessage(operation.getMessage());
				feedbackMessage.deleteOperations();
				feedbackMessage.createGetOperation(((RelationStatement) statement.getObject()));
				statement.delete();
				pushSubstitution(executionContext, ((RelationStatement) statement).getObject(), ((RelationStatement) statement).getPredicate(), ((RelationStatement) statement).getSubject());

				this.feedback(feedbackMessage, operationMessageResult, executionContext);
			}
		}

		// look through the message result for post-substitution actions

		Map.Entry<XDI3Segment, XDI3Segment[]> entry;

		while ((entry = popSubstitution(executionContext)) != null) {

			XDI3Segment substitutedContextNodeXri = entry.getKey();
			XDI3Segment arcXri = entry.getValue()[0];
			XDI3Segment contextNodeXri = entry.getValue()[1];

			ContextNode substitutedContextNode = operationMessageResult.getGraph().findContextNode(substitutedContextNodeXri, false);
			if (substitutedContextNode == null) continue;

			boolean doBackSubstitution = XDIDictionaryConstants.XRI_S_IS_BANG.equals(arcXri) || (XDIDictionaryConstants.XRI_S_IS.equals(arcXri) && GetOperation.XRI_EXTENSION_BANG.equals(operation.getOperationExtensionXri()));
			boolean doRecordSubstitution = (XDIDictionaryConstants.XRI_S_IS.equals(arcXri) && ! GetOperation.XRI_EXTENSION_BANG.equals(operation.getOperationExtensionXri()));

			// back-substitution?

			if (doBackSubstitution) {

				if (log.isDebugEnabled()) log.debug("In message result: Back-substituting " + substitutedContextNodeXri + " with " + contextNodeXri);

				ContextNode contextNode = operationMessageResult.getGraph().findContextNode(contextNodeXri, true);
				CopyUtil.copyContextNodeContents(substitutedContextNode, contextNode, null);

				// delete the substituted context as far up the graph as possible

				ContextNode parentSubstitutedContextNode;

				do {

					parentSubstitutedContextNode = substitutedContextNode.getContextNode();
					substitutedContextNode.delete();
					substitutedContextNode = parentSubstitutedContextNode;
				} while (parentSubstitutedContextNode.isEmpty() && (! parentSubstitutedContextNode.isRootContextNode()));
			}

			// record substitution?

			if (doRecordSubstitution) {

				if (log.isDebugEnabled()) log.debug("In message result: Recording substitution " + substitutedContextNodeXri + " with " + contextNodeXri);

				operationMessageResult.getGraph().addStatement(StatementUtil.fromComponents(contextNodeXri, XDIDictionaryConstants.XRI_S_IS, substitutedContextNodeXri));
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

		// apply substitution

		XDI3Segment originalTargetAddress = targetAddress;
		XDI3Segment substitutedTargetAddress = originalTargetAddress;

		XDI3Segment tempTargetAddress;

		while (true) { 

			tempTargetAddress = substitutedTargetAddress;
			substitutedTargetAddress = substituteCanonicalArcs(tempTargetAddress, graph, executionContext);

			if (substitutedTargetAddress == tempTargetAddress) break;

			if (log.isDebugEnabled()) log.debug("In message envelope: Substituted " + tempTargetAddress + " with " + substitutedTargetAddress);
		}

		if (substitutedTargetAddress != originalTargetAddress) {

			return substitutedTargetAddress;
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
				(XDIDictionaryConstants.XRI_S_IS.equals(((RelationStatement) targetStatement).getPredicate()) ||
						XDIDictionaryConstants.XRI_S_IS_BANG.equals(((RelationStatement) targetStatement).getPredicate()))) {

			XDI3Segment targetContextNodeXri = targetStatement.getContextNodeXri();
			ContextNode targetContextNode = graph.findContextNode(targetContextNodeXri, false);

			// check if the context is empty

			if (targetContextNode != null && ! targetContextNode.isEmpty()) {

				throw new Xdi2MessagingException("Cannot add canonical $is or $is! relation to non-empty context node " + targetContextNode.getXri(), null, executionContext);
			}
		}

		// apply substitution

		XDI3Segment originalTargetSubject = targetStatement.getSubject();
		XDI3Segment substitutedTargetSubject = originalTargetSubject;

		XDI3Segment tempTargetSubject;

		while (true) {

			tempTargetSubject = substitutedTargetSubject;
			substitutedTargetSubject = substituteCanonicalArcs(tempTargetSubject, graph, executionContext);

			if (substitutedTargetSubject == tempTargetSubject) break;

			if (log.isDebugEnabled()) log.debug("In message envelope: Substituted " + tempTargetSubject + " with " + substitutedTargetSubject);
		}

		if (substitutedTargetSubject != originalTargetSubject) {

			return StatementUtil.fromComponents(substitutedTargetSubject, targetStatement.getPredicate(), targetStatement.getObject());
		}

		// done

		return targetStatement;
	}

	private static XDI3Segment substituteCanonicalArcs(XDI3Segment contextNodeXri, Graph graph, ExecutionContext executionContext) throws Xdi2MessagingException {

		String localPart = "";

		XDI3Segment originalContextNodeXri = contextNodeXri;

		while (contextNodeXri != null) {

			ContextNode contextNode = graph.findContextNode(contextNodeXri, false);
			ContextNode canonicalContextNode = contextNode == null ? null : Dictionary.getCanonicalContextNode(contextNode);
			ContextNode privateCanonicalContextNode = contextNode == null ? null : Dictionary.getPrivateCanonicalContextNode(contextNode);

			if (canonicalContextNode != null) {

				if (canonicalContextNode.equals(contextNode)) break;

				XDI3Segment substitutedContextNodeXri = canonicalContextNode.getXri();
				pushSubstitution(executionContext, substitutedContextNodeXri, XDIDictionaryConstants.XRI_S_IS, contextNodeXri);

				if (log.isDebugEnabled()) log.debug("Applying " + XDIDictionaryConstants.XRI_S_IS + " arc: " + contextNodeXri + " --> " + substitutedContextNodeXri);

				return new XDI3Segment("" + canonicalContextNode.getXri() + localPart);
			}

			if (privateCanonicalContextNode != null) {

				if (privateCanonicalContextNode.equals(contextNode)) break;

				XDI3Segment privateSubstitutedContextNodeXri = privateCanonicalContextNode.getXri();
				pushSubstitution(executionContext, privateSubstitutedContextNodeXri, XDIDictionaryConstants.XRI_S_IS_BANG, contextNodeXri);

				if (log.isDebugEnabled()) log.debug("Applying " + XDIDictionaryConstants.XRI_S_IS_BANG + " arc: " + contextNodeXri + " --> " + privateSubstitutedContextNodeXri);

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

	private static final String EXECUTIONCONTEXT_KEY_SUBSTITUTIONS_PER_OPERATION = DollarIsInterceptor.class.getCanonicalName() + "#substitutionsperoperation";

	@SuppressWarnings("unchecked")
	private static Deque<Map.Entry<XDI3Segment, XDI3Segment[]>> getSubstitutions(ExecutionContext executionContext) {

		return (Deque<Map.Entry<XDI3Segment, XDI3Segment[]>>) executionContext.getMessageEnvelopeAttribute(EXECUTIONCONTEXT_KEY_SUBSTITUTIONS_PER_OPERATION);
	}

	private static Map.Entry<XDI3Segment, XDI3Segment[]> popSubstitution(ExecutionContext executionContext) {

		Deque<Map.Entry<XDI3Segment, XDI3Segment[]>> substitutions = getSubstitutions(executionContext);
		if (substitutions.isEmpty()) return null;

		return substitutions.pop();
	}

	private static void pushSubstitution(ExecutionContext executionContext, XDI3Segment substitutedContextNodeXri, XDI3Segment arcXri, XDI3Segment contextNodeXri) {

		getSubstitutions(executionContext).push(new AbstractMap.SimpleEntry<XDI3Segment, XDI3Segment[]> (substitutedContextNodeXri, new XDI3Segment[] { arcXri, contextNodeXri }));
	}

	private static void resetSubstitutions(ExecutionContext executionContext) {

		executionContext.putMessageEnvelopeAttribute(EXECUTIONCONTEXT_KEY_SUBSTITUTIONS_PER_OPERATION, new ArrayDeque<Map.Entry<XDI3Segment, XDI3Segment>> ());
	}
}
