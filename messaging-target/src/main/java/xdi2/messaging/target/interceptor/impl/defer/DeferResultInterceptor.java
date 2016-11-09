package xdi2.messaging.target.interceptor.impl.defer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.Graph;
import xdi2.core.bootstrap.XDIBootstrap;
import xdi2.core.constants.XDILinkContractConstants;
import xdi2.core.features.index.Index;
import xdi2.core.features.linkcontracts.instance.LinkContract;
import xdi2.core.features.linkcontracts.instantiation.LinkContractInstantiation;
import xdi2.core.features.nodetypes.XdiEntityCollection;
import xdi2.core.features.nodetypes.XdiEntityInstanceUnordered;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.syntax.XDIStatement;
import xdi2.core.util.CopyUtil;
import xdi2.core.util.iterators.IterableIterator;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.constants.XDIMessagingConstants;
import xdi2.messaging.operations.Operation;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.Prototype;
import xdi2.messaging.target.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.execution.ExecutionContext;
import xdi2.messaging.target.execution.ExecutionResult;
import xdi2.messaging.target.interceptor.InterceptorResult;
import xdi2.messaging.target.interceptor.MessageEnvelopeInterceptor;
import xdi2.messaging.target.interceptor.impl.AbstractInterceptor;

/**
 * This interceptor can add defer results to a messaging target and execution result.
 */
public class DeferResultInterceptor extends AbstractInterceptor<MessagingTarget> implements MessageEnvelopeInterceptor, Prototype<DeferResultInterceptor> {

	private static final Logger log = LoggerFactory.getLogger(DeferResultInterceptor.class);

	private Graph targetGraph;

	public DeferResultInterceptor(Graph targetGraph) {

		this.targetGraph = targetGraph;
	}

	public DeferResultInterceptor() {

		this(null);
	}

	/*
	 * Prototype
	 */

	@Override
	public DeferResultInterceptor instanceFor(xdi2.messaging.target.Prototype.PrototypingContext prototypingContext) throws Xdi2MessagingException {

		// done

		return this;
	}

	/*
	 * MessageEnvelopeInterceptor
	 */


	@Override
	public InterceptorResult before(MessageEnvelope messageEnvelope, ExecutionContext executionContext, ExecutionResult executionResult) throws Xdi2MessagingException {

		return InterceptorResult.DEFAULT;
	}

	@Override
	public InterceptorResult after(MessageEnvelope messageEnvelope, ExecutionContext executionContext, ExecutionResult executionResult) throws Xdi2MessagingException {

		// look for defer results

		MessagingTarget messagingTarget = executionContext.getCurrentMessagingTarget();

		Map<Message, Boolean> deferResults = getDeferResults(executionContext);
		if (deferResults == null) return InterceptorResult.DEFAULT;

		for (Map.Entry<Message, Boolean> deferResult : deferResults.entrySet()) {

			Message message = deferResult.getKey();
			Boolean push = deferResult.getValue();

			// write message and index into target graph

			if (this.getTargetGraph(executionContext) != null) {

				CopyUtil.copyContextNode(message.getContextNode(), this.getTargetGraph(executionContext), null);
				XdiEntityCollection xdiMessageIndex = Index.getEntityIndex(this.getTargetGraph(executionContext), XDIMessagingConstants.XDI_ARC_MSG, true);
				Index.setEntityIndexAggregation(xdiMessageIndex, message.getXdiEntity().getXDIAddress());
			}

			// create a deferred push link contract?

			if (! Boolean.TRUE.equals(push)) continue;

			Graph messageDeferredPushResultGraph = executionResult.createMessageDeferredPushResultGraph(message);

			// determine requesting and authorizing authorities

			XDIAddress authorizingAuthority = messagingTarget.getOwnerXDIAddress();
			XDIAddress requestingAuthority = message.getFromXDIAddress();

			// determine link contract instance ID

			XDIArc instanceXDIArc = XdiEntityInstanceUnordered.createXDIArc();

			// determine link contract variable values

			List<XDIAddress> pushVariableValues = new ArrayList<XDIAddress> ();

			for (Operation operation : message.getOperations()) {

				XDIAddress targetXDIAddress = operation.getTargetXDIAddress();
				IterableIterator<XDIStatement> targetXDIStatements = operation.getTargetXDIStatements();

				if (targetXDIAddress != null) {

					pushVariableValues.add(targetXDIAddress);
				}

				if (targetXDIStatements != null) {

					for (XDIStatement targetXDIStatement : targetXDIStatements) {

						pushVariableValues.add(targetXDIAddressForTargetXDIStatement(targetXDIStatement));
					}
				}
			}

			XDIAddress msgVariableValue = message.getContextNode().getXDIAddress();

			Map<XDIArc, Object> variableValues = new HashMap<XDIArc, Object> ();
			variableValues.put(XDIArc.create("{$push}"), pushVariableValues);
			variableValues.put(XDIArc.create("{$msg}"), msgVariableValue);

			// instantiate deferred push link contract

			LinkContractInstantiation linkContractInstantiation = new LinkContractInstantiation(XDIBootstrap.DEFER_PUSH_LINK_CONTRACT_TEMPLATE);
			linkContractInstantiation.setAuthorizingAuthority(authorizingAuthority);
			linkContractInstantiation.setRequestingAuthority(requestingAuthority);
			linkContractInstantiation.setVariableValues(variableValues);

			LinkContract pushLinkContract;

			try {

				pushLinkContract = linkContractInstantiation.execute(instanceXDIArc, true);
			} catch (Exception ex) {

				throw new Xdi2MessagingException("Cannot instantiate $push link contract: " + ex.getMessage(), ex, executionContext);
			}

			// write push link contract into message push result graph

			CopyUtil.copyGraph(pushLinkContract.getContextNode().getGraph(), messageDeferredPushResultGraph, null);

			// write push link contract and index into target graph

			if (this.getTargetGraph(executionContext) != null) {

				CopyUtil.copyGraph(pushLinkContract.getContextNode().getGraph(), this.getTargetGraph(executionContext), null);
				XdiEntityCollection xdiLinkContractIndex = Index.getEntityIndex(this.getTargetGraph(executionContext), XDILinkContractConstants.XDI_ARC_CONTRACT, true);
				Index.setEntityIndexAggregation(xdiLinkContractIndex, pushLinkContract.getXdiEntity().getXDIAddress());
			}

			if (log.isDebugEnabled()) log.debug("For message " + message + " we have message push result graph " + messageDeferredPushResultGraph);
		}

		// done

		return InterceptorResult.DEFAULT;
	}

	@Override
	public void exception(MessageEnvelope messageEnvelope, ExecutionContext executionContext, ExecutionResult executionResult, Exception ex) {

	}

	/*
	 * Getters and setters
	 */

	public Graph getTargetGraph(ExecutionContext executionContext) {

		Graph targetGraph = this.getTargetGraph();
		if (targetGraph == null) targetGraph = executionContext.getCurrentGraph();
		if (targetGraph == null) throw new NullPointerException("No target graph.");

		return targetGraph;
	}

	public Graph getTargetGraph() {

		return this.targetGraph;
	}

	public void setTargetGraph(Graph targetGraph) {

		this.targetGraph = targetGraph;
	}

	/*
	 * Helper methods
	 */

	private static XDIAddress targetXDIAddressForTargetXDIStatement(XDIStatement targetXDIStatement) {

		if (targetXDIStatement.isContextNodeStatement()) {

			return targetXDIStatement.getTargetXDIAddress();
		} else {

			return targetXDIStatement.getContextNodeXDIAddress();
		}
	}

	/*
	 * ExecutionContext helper methods
	 */

	private static final String EXECUTIONCONTEXT_KEY_DEFERRESULTS_PER_MESSAGEENVELOPE = DeferResultInterceptor.class.getCanonicalName() + "#deferresultspermessageenvelope";

	@SuppressWarnings("unchecked")
	public static Map<Message, Boolean> getDeferResults(ExecutionContext executionContext) {

		return (Map<Message, Boolean>) executionContext.getMessageEnvelopeAttribute(EXECUTIONCONTEXT_KEY_DEFERRESULTS_PER_MESSAGEENVELOPE);
	}

	@SuppressWarnings("unchecked")
	public static boolean hasDeferResult(ExecutionContext executionContext, Message message) {

		Map<Message, Boolean> deferResults = (Map<Message, Boolean>) executionContext.getMessageEnvelopeAttribute(EXECUTIONCONTEXT_KEY_DEFERRESULTS_PER_MESSAGEENVELOPE);
		if (deferResults == null) return false;

		return deferResults.containsKey(message);
	}

	@SuppressWarnings("unchecked")
	public static void putDeferResult(ExecutionContext executionContext, Message message, Boolean deferResult) {

		Map<Message, Boolean> deferResults = (Map<Message, Boolean>) executionContext.getMessageEnvelopeAttribute(EXECUTIONCONTEXT_KEY_DEFERRESULTS_PER_MESSAGEENVELOPE);
		if (deferResults == null) { deferResults = new HashMap<Message, Boolean> (); executionContext.putMessageEnvelopeAttribute(EXECUTIONCONTEXT_KEY_DEFERRESULTS_PER_MESSAGEENVELOPE, deferResults); }

		deferResults.put(message, deferResult);
	}
}
