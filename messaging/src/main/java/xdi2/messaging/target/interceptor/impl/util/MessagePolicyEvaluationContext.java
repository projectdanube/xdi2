package xdi2.messaging.target.interceptor.impl.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Statement;
import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.core.features.nodetypes.XdiInnerRoot;
import xdi2.core.features.nodetypes.XdiPeerRoot;
import xdi2.core.features.policy.evaluation.PolicyEvaluationContext;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.syntax.XDIStatement;
import xdi2.core.util.XDIAddressUtil;
import xdi2.messaging.Message;

public class MessagePolicyEvaluationContext implements PolicyEvaluationContext {

	private static final Logger log = LoggerFactory.getLogger(MessagePolicyEvaluationContext.class);

	public static final XDIAddress XDI_ADD_FROM_VARIABLE = XDIAddress.create("{$from}");
	public static final XDIAddress XDI_ADD_MSG_VARIABLE = XDIAddress.create("{$msg}");

	public static final XDIArc XDI_ARC_FROM_VARIABLE = XDIArc.create("{$from}");
	public static final XDIArc XDI_ARC_MSG_VARIABLE = XDIArc.create("{$msg}");

	private Message message;
	private Graph targetGraph;

	public MessagePolicyEvaluationContext(Message message, Graph targetGraph) {

		this.message = message;
		this.targetGraph = targetGraph;
	}

	@Override
	public XDIAddress resolveXDIAddress(XDIAddress contextNodeAddress) {

		XDIAddress resolvedcontextNodeAddress = contextNodeAddress;

		resolvedcontextNodeAddress = XDIAddressUtil.replaceXDIAddress(resolvedcontextNodeAddress, XDI_ARC_MSG_VARIABLE, this.getMessage().getContextNode().getXDIAddress());
		resolvedcontextNodeAddress = XDIAddressUtil.replaceXDIAddress(resolvedcontextNodeAddress, XDI_ARC_FROM_VARIABLE, this.getMessage().getSenderAddress());

		if (log.isTraceEnabled()) log.trace("resolveXDIAddress(" + contextNodeAddress + ") --> " + resolvedcontextNodeAddress);

		return resolvedcontextNodeAddress;
	}

	@Override
	public ContextNode getContextNode(XDIAddress contextNodeAddress) {

		Graph resolvedGraph = this.resolveGraph(contextNodeAddress);
		XDIAddress resolvedcontextNodeAddress = this.resolveXDIAddress(contextNodeAddress);
		ContextNode resolvedContextNode = resolvedGraph.getDeepContextNode(resolvedcontextNodeAddress, false);

		if (log.isTraceEnabled()) log.trace("getContextNode(" + contextNodeAddress + ") --> " + resolvedcontextNodeAddress + " --> " + resolvedContextNode);

		return resolvedContextNode;
	}

	@Override
	public Statement getStatement(XDIStatement statementAddress) {

		XDIAddress contextNodeAddress = statementAddress.getContextNodeXDIAddress();
		Graph resolvedGraph = this.resolveGraph(contextNodeAddress);
		XDIAddress resolvedcontextNodeAddress = this.resolveXDIAddress(contextNodeAddress);

		XDIStatement resolvedStatementAddress;

		if (statementAddress.isContextNodeStatement()) {

			XDIArc contextNodeArc = statementAddress.getContextNodeXDIArc();

			resolvedStatementAddress = XDIStatement.fromContextNodeComponents(
					resolvedcontextNodeAddress, 
					contextNodeArc);
		} else if (statementAddress.isRelationStatement()) {

			XDIAddress relationAddress = statementAddress.getRelationXDIAddress();
			XDIAddress targetContextNodeAddress = statementAddress.getTargetContextNodeXDIAddress();

			XDIAddress resolvedtargetContextNodeAddress = this.resolveXDIAddress(targetContextNodeAddress);

			resolvedStatementAddress = XDIStatement.fromRelationComponents(
					resolvedcontextNodeAddress, 
					relationAddress, 
					resolvedtargetContextNodeAddress);
		} else if (statementAddress.isLiteralStatement()) {

			Object literalData = statementAddress.getLiteralData();

			resolvedStatementAddress = XDIStatement.fromLiteralComponents(
					resolvedcontextNodeAddress, 
					literalData);
		} else {

			throw new Xdi2RuntimeException("Unexpected statement: " + statementAddress);
		}

		Statement resolvedStatement = resolvedGraph.getStatement(resolvedStatementAddress);

		if (log.isTraceEnabled()) log.trace("getStatement(" + statementAddress + ") --> " + resolvedStatementAddress + " --> " + resolvedStatement);

		return resolvedStatement;
	}

	/*
	 * Helper methods
	 */

	private Graph resolveGraph(XDIAddress contextNodeAddress) {

		XDIArc firstArc = contextNodeAddress.getFirstXDIArc();

		if (XdiPeerRoot.isPeerRootXDIArc(firstArc)) {

			firstArc = XdiPeerRoot.getXDIAddressOfPeerRootXDIArc(firstArc).getFirstXDIArc();
		} else if (XdiInnerRoot.isInnerRootXDIArc(firstArc)) {

			firstArc = XdiInnerRoot.getSubjectOfInnerRootXDIArc(firstArc).getFirstXDIArc();
		}

		Graph resolvedGraph = null;

		if (XDI_ARC_MSG_VARIABLE.equals(firstArc)) {

			resolvedGraph = this.getMessage().getContextNode().getGraph();
		} else if (XDI_ARC_FROM_VARIABLE.equals(firstArc)) {

			resolvedGraph = this.getTargetGraph();
		} else {

			resolvedGraph = this.getTargetGraph();
		}

		if (log.isTraceEnabled()) log.trace("getGraph(" + contextNodeAddress + ") --> " + resolvedGraph);

		return resolvedGraph;
	}

	/*
	 * Getters and setters
	 */

	public Message getMessage() {

		return this.message;
	}

	public Graph getTargetGraph() {

		return this.targetGraph;
	}
}
