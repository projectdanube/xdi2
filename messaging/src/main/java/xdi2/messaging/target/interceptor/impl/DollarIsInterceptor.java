package xdi2.messaging.target.interceptor.impl;

import java.util.AbstractMap;
import java.util.ArrayDeque;
import java.util.Deque;
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
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.messaging.AddOperation;
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

		// look through the message result for back-substitutions

		Map.Entry<XRI3Segment, XRI3Segment> entry;

		while ((entry = popSubstitution(executionContext)) != null) {

			XRI3Segment substitutedContextNodeXri = entry.getKey();
			XRI3Segment contextNodeXri = entry.getValue();

			if (log.isDebugEnabled()) log.debug("In message result: Back-substituting " + substitutedContextNodeXri + " with " + contextNodeXri);

			ContextNode substitutedContextNode = operationMessageResult.getGraph().findContextNode(substitutedContextNodeXri, false);
			if (substitutedContextNode == null) continue;

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

		// done

		return false;
	}

	/*
	 * TargetInterceptor
	 */

	@Override
	public XRI3Segment targetAddress(XRI3Segment targetAddress, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// find our graph

		MessagingTarget currentMessagingTarget = executionContext.getCurrentMessagingTarget();
		if (! (currentMessagingTarget instanceof GraphMessagingTarget)) return targetAddress;

		Graph graph = ((GraphMessagingTarget) currentMessagingTarget).getGraph();

		// do we have to apply substitution?

		XRI3Segment originalTargetAddress = targetAddress;
		XRI3Segment substitutedTargetAddress = originalTargetAddress;

		XRI3Segment tempTargetAddress;

		while (true) { 

			tempTargetAddress = substitutedTargetAddress;
			substitutedTargetAddress = substituteExistingCanonicalArcs(tempTargetAddress, graph, executionContext);

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

		if (targetStatement instanceof RelationStatement &&
				XDIDictionaryConstants.XRI_S_IS.equals(((RelationStatement) targetStatement).getPredicate())) {

			// are we adding a $is arc to a non-empty context?

			if (operation instanceof AddOperation) {

				XRI3Segment targetContextNodeXri = targetStatement.getContextNodeXri();
				ContextNode targetContextNode = graph.findContextNode(targetContextNodeXri, false);

				// check if the context is empty

				if (targetContextNode != null && ! targetContextNode.isEmpty()) {

					throw new Xdi2MessagingException("Cannot add canonical $is relation to non-empty context node " + targetContextNode.getXri(), null, executionContext);
				}
			}
		} else {

			// do we have to apply substitution?

			XRI3Segment originalTargetSubject = targetStatement.getSubject();
			XRI3Segment substitutedTargetSubject = originalTargetSubject;

			XRI3Segment tempTargetSubject;

			while (true) {

				tempTargetSubject = substitutedTargetSubject;
				substitutedTargetSubject = substituteExistingCanonicalArcs(tempTargetSubject, graph, executionContext);

				if (substitutedTargetSubject == tempTargetSubject) break;

				if (log.isDebugEnabled()) log.debug("In message envelope: Substituted " + tempTargetSubject + " with " + substitutedTargetSubject);
			}

			if (substitutedTargetSubject != originalTargetSubject) {

				return StatementUtil.fromComponents(substitutedTargetSubject, targetStatement.getPredicate(), targetStatement.getObject());
			}
		}

		// done

		return targetStatement;
	}

	private static XRI3Segment substituteExistingCanonicalArcs(XRI3Segment contextNodeXri, Graph graph, ExecutionContext executionContext) throws Xdi2MessagingException {

		String localPart = "";

		XRI3Segment originalContextNodeXri = contextNodeXri;

		while (contextNodeXri != null) {

			ContextNode contextNode = graph.findContextNode(contextNodeXri, false);
			ContextNode canonicalContextNode = contextNode == null ? null : Dictionary.getCanonicalContextNode(contextNode);

			if (canonicalContextNode != null) {

				if (canonicalContextNode.equals(contextNode)) break;

				XRI3Segment substitutedContextNodeXri = canonicalContextNode.getXri();

				if (log.isDebugEnabled()) log.debug("Applying canonical arc: " + contextNodeXri + " --> " + substitutedContextNodeXri);

				pushSubstitution(executionContext, substitutedContextNodeXri, contextNodeXri);

				return new XRI3Segment("" + canonicalContextNode.getXri() + localPart);
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
	private static Deque<Map.Entry<XRI3Segment, XRI3Segment>> getSubstitutions(ExecutionContext executionContext) {

		return (Deque<Map.Entry<XRI3Segment, XRI3Segment>>) executionContext.getMessageEnvelopeAttribute(EXECUTIONCONTEXT_KEY_SUBSTITUTIONS_PER_OPERATION);
	}

	private static Map.Entry<XRI3Segment, XRI3Segment> popSubstitution(ExecutionContext executionContext) {

		Deque<Map.Entry<XRI3Segment, XRI3Segment>> substitutions = getSubstitutions(executionContext);
		if (substitutions.isEmpty()) return null;

		return substitutions.pop();
	}

	private static void pushSubstitution(ExecutionContext executionContext, XRI3Segment key, XRI3Segment value) {

		getSubstitutions(executionContext).push(new AbstractMap.SimpleEntry<XRI3Segment, XRI3Segment> (key, value));
	}

	private static void resetSubstitutions(ExecutionContext executionContext) {

		executionContext.putMessageEnvelopeAttribute(EXECUTIONCONTEXT_KEY_SUBSTITUTIONS_PER_OPERATION, new ArrayDeque<Map.Entry<XRI3Segment, XRI3Segment>> ());
	}
}
