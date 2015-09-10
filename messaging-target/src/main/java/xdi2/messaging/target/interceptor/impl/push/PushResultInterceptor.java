package xdi2.messaging.target.interceptor.impl.push;

import java.io.Serializable;
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
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.syntax.XDIStatement;
import xdi2.core.util.CopyUtil;
import xdi2.core.util.GraphAware;
import xdi2.messaging.Message;
import xdi2.messaging.constants.XDIMessagingConstants;
import xdi2.messaging.operations.Operation;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.Prototype;
import xdi2.messaging.target.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.execution.ExecutionContext;
import xdi2.messaging.target.execution.ExecutionResult;
import xdi2.messaging.target.interceptor.ExecutionResultInterceptor;
import xdi2.messaging.target.interceptor.impl.AbstractInterceptor;

/**
 * This interceptor can add push results to an execution result.
 */
public class PushResultInterceptor extends AbstractInterceptor<MessagingTarget> implements GraphAware, ExecutionResultInterceptor, Prototype<PushResultInterceptor> {

	private static final Logger log = LoggerFactory.getLogger(PushResultInterceptor.class);

	private Graph targetGraph;

	public PushResultInterceptor(Graph targetGraph) {

		this.targetGraph = targetGraph;
	}

	public PushResultInterceptor() {

		this(null);
	}

	/*
	 * Prototype
	 */

	@Override
	public PushResultInterceptor instanceFor(xdi2.messaging.target.Prototype.PrototypingContext prototypingContext) throws Xdi2MessagingException {

		// create new contributor

		PushResultInterceptor contributor = new PushResultInterceptor();

		// set the graph

		contributor.setTargetGraph(this.getTargetGraph());

		// done

		return contributor;
	}

	/*
	 * GraphAware
	 */

	@Override
	public void setGraph(Graph graph) {

		if (this.getTargetGraph() == null) this.setTargetGraph(graph);
	}

	/*
	 * ResultGraphInterceptor
	 */

	// TODO: maybe move some of this directly into the ExecutionResult
	@Override
	public void finish(ExecutionContext executionContext, ExecutionResult executionResult) throws Xdi2MessagingException {

		// look for push results

		Map<Operation, List<PushResult>> operationPushResultsMap = getOperationPushResults(executionContext);
		if (operationPushResultsMap == null) return;

		for (Map.Entry<Operation, List<PushResult>> operationPushResults : operationPushResultsMap.entrySet()) {

			Operation operation = operationPushResults.getKey();
			List<PushResult> pushResults = operationPushResults.getValue();

			if (pushResults.isEmpty()) continue;

			Graph operationPushResultGraph = executionResult.createOperationPushResultGraph(operation);

			Message message = operation.getMessage();

			for (PushResult pushResult : pushResults) {

				// determine requesting and authorizing authorities

				XDIAddress authorizingAuthority = executionContext.getCurrentMessagingTarget().getOwnerXDIAddress();
				XDIAddress requestingAuthority = message.getSenderXDIAddress();

				// determine variable values

				XDIAddress targetVariableValue = null;
				if (targetVariableValue == null && pushResult.getXDIAddress() != null) targetVariableValue = pushResult.getXDIAddress();
				if (targetVariableValue == null && pushResult.getXDIStatement() != null) targetVariableValue = targetXDIAddressForTargetXDIStatement(pushResult.getXDIStatement());

				if (targetVariableValue == null) throw new NullPointerException();

				Map<XDIArc, XDIAddress> variableValues = new HashMap<XDIArc, XDIAddress> ();
				variableValues.put(XDIArc.create("{$push}"), targetVariableValue);

				// instantiate push link contract

				LinkContractInstantiation linkContractInstantiation = new LinkContractInstantiation(XDIBootstrap.PUSH_LINK_CONTRACT_TEMPLATE);
				linkContractInstantiation.setAuthorizingAuthority(authorizingAuthority);
				linkContractInstantiation.setRequestingAuthority(requestingAuthority);
				linkContractInstantiation.setVariableValues(variableValues);

				LinkContract pushLinkContract = linkContractInstantiation.execute(false, true);

				// associate push link contract with message

				pushLinkContract.setMessageXDIAddress(message.getContextNode().getXDIAddress());

				// write push link contract into operation result graph

				CopyUtil.copyGraph(pushLinkContract.getContextNode().getGraph(), operationPushResultGraph, null);

				// write message, push link contract, and indices into target graph

				if (this.getTargetGraph() != null) {

					CopyUtil.copyContextNode(message.getContextNode(), this.getTargetGraph(), null);
					CopyUtil.copyGraph(pushLinkContract.getContextNode().getGraph(), this.getTargetGraph(), null);

					XdiEntityCollection xdiMessageIndex = Index.getEntityIndex(this.getTargetGraph(), XDIMessagingConstants.XDI_ARC_MSG, true);
					XdiEntityCollection xdiLinkContractIndex = Index.getEntityIndex(this.getTargetGraph(), XDILinkContractConstants.XDI_ARC_DO, true);

					Index.setEntityIndexAggregation(xdiMessageIndex, message.getXdiEntity());
					Index.setEntityIndexAggregation(xdiLinkContractIndex, pushLinkContract.getXdiEntity());
				}
			}

			if (log.isDebugEnabled()) log.debug("For operation " + operation + " we have operation push result graph " + operationPushResultGraph);
		}
	}

	/*
	 * Getters and setters
	 */

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

	private static final String EXECUTIONCONTEXT_KEY_OPERATIONPUSHRESULTS_PER_MESSAGEENVELOPE = PushResultInterceptor.class.getCanonicalName() + "#operationpushresultspermessageenvelope";

	@SuppressWarnings("unchecked")
	public static Map<Operation, List<PushResult>> getOperationPushResults(ExecutionContext executionContext) {

		return (Map<Operation, List<PushResult>>) executionContext.getMessageEnvelopeAttribute(EXECUTIONCONTEXT_KEY_OPERATIONPUSHRESULTS_PER_MESSAGEENVELOPE);
	}

	@SuppressWarnings("unchecked")
	public static void addOperationPushResult(ExecutionContext executionContext, Operation operation, PushResult pushResult) {

		Map<Operation, List<PushResult>> pushResultsMap = (Map<Operation, List<PushResult>>) executionContext.getMessageEnvelopeAttribute(EXECUTIONCONTEXT_KEY_OPERATIONPUSHRESULTS_PER_MESSAGEENVELOPE);
		if (pushResultsMap == null) { pushResultsMap = new HashMap<Operation, List<PushResult>> (); executionContext.putMessageEnvelopeAttribute(EXECUTIONCONTEXT_KEY_OPERATIONPUSHRESULTS_PER_MESSAGEENVELOPE, pushResultsMap); }

		List<PushResult> pushResults = pushResultsMap.get(operation);
		if (pushResults == null) { pushResults = new ArrayList<PushResult> (); pushResultsMap.put(operation, pushResults); }

		pushResults.add(pushResult);
	}

	/*
	 * Helper class
	 */

	public static class PushResult implements Serializable {

		private static final long serialVersionUID = 904748436911142763L;

		private XDIAddress XDIaddress;
		private XDIStatement XDIstatement;

		public PushResult(XDIAddress XDIaddress) {

			this.XDIaddress = XDIaddress;
			this.XDIstatement = null;
		}

		public PushResult(XDIStatement XDIstatement) {

			this.XDIaddress = null;
			this.XDIstatement = XDIstatement;
		}

		public XDIAddress getXDIAddress() {

			return this.XDIaddress;
		}

		public XDIStatement getXDIStatement() {

			return this.XDIstatement;
		}
	}
}
