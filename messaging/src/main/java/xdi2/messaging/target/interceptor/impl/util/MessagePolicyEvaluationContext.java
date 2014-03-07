package xdi2.messaging.target.interceptor.impl.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Statement;
import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.core.features.linkcontracts.evaluation.PolicyEvaluationContext;
import xdi2.core.features.nodetypes.XdiInnerRoot;
import xdi2.core.features.nodetypes.XdiPeerRoot;
import xdi2.core.util.XDI3Util;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;
import xdi2.core.xri3.XDI3SubSegment;
import xdi2.messaging.Message;

public class MessagePolicyEvaluationContext implements PolicyEvaluationContext {

	private static final Logger log = LoggerFactory.getLogger(MessagePolicyEvaluationContext.class);

	public static final XDI3Segment XRI_S_FROM_VARIABLE = XDI3Segment.create("{$from}");
	public static final XDI3Segment XRI_S_MSG_VARIABLE = XDI3Segment.create("{$msg}");

	public static final XDI3SubSegment XRI_SS_FROM_VARIABLE = XDI3SubSegment.create("{$from}");
	public static final XDI3SubSegment XRI_SS_MSG_VARIABLE = XDI3SubSegment.create("{$msg}");

	private Message message;
	private Graph targetGraph;

	public MessagePolicyEvaluationContext(Message message, Graph targetGraph) {

		this.message = message;
		this.targetGraph = targetGraph;
	}

	@Override
	public XDI3Segment resolveXri(XDI3Segment contextNodeXri) {

		XDI3Segment resolvedContextNodeXri = contextNodeXri;

		resolvedContextNodeXri = XDI3Util.replaceXri(resolvedContextNodeXri, XRI_SS_MSG_VARIABLE, this.getMessage().getContextNode().getXri(), true, true, true);
		resolvedContextNodeXri = XDI3Util.replaceXri(resolvedContextNodeXri, XRI_SS_FROM_VARIABLE, this.getMessage().getSenderXri(), true, true, true);

		if (log.isTraceEnabled()) log.trace("resolveXri(" + contextNodeXri + ") --> " + resolvedContextNodeXri);

		return resolvedContextNodeXri;
	}

	@Override
	public ContextNode getContextNode(XDI3Segment contextNodeXri) {

		Graph resolvedGraph = this.resolveGraph(contextNodeXri);
		XDI3Segment resolvedContextNodeXri = this.resolveXri(contextNodeXri);
		ContextNode resolvedContextNode = resolvedGraph.getDeepContextNode(resolvedContextNodeXri, false);

		if (log.isTraceEnabled()) log.trace("getContextNode(" + contextNodeXri + ") --> " + resolvedContextNodeXri + " --> " + resolvedContextNode);

		return resolvedContextNode;
	}

	@Override
	public Statement getStatement(XDI3Statement statementXri) {

		XDI3Segment contextNodeXri = statementXri.getContextNodeXri();
		Graph resolvedGraph = this.resolveGraph(contextNodeXri);
		XDI3Segment resolvedContextNodeXri = this.resolveXri(contextNodeXri);

		XDI3Statement resolvedStatementXri;

		if (statementXri.isContextNodeStatement()) {

			XDI3SubSegment contextNodeArcXri = statementXri.getContextNodeArcXri();

			resolvedStatementXri = XDI3Statement.fromContextNodeComponents(
					resolvedContextNodeXri, 
					contextNodeArcXri);
		} else if (statementXri.isRelationStatement()) {

			XDI3Segment relationArcXri = statementXri.getRelationArcXri();
			XDI3Segment targetContextNodeXri = statementXri.getTargetContextNodeXri();

			XDI3Segment resolvedTargetContextNodeXri = this.resolveXri(targetContextNodeXri);

			resolvedStatementXri = XDI3Statement.fromRelationComponents(
					resolvedContextNodeXri, 
					relationArcXri, 
					resolvedTargetContextNodeXri);
		} else if (statementXri.isLiteralStatement()) {

			Object literalData = statementXri.getLiteralData();

			resolvedStatementXri = XDI3Statement.fromLiteralComponents(
					resolvedContextNodeXri, 
					literalData);
		} else {

			throw new Xdi2RuntimeException("Unexpected statement: " + statementXri);
		}

		Statement resolvedStatement = resolvedGraph.getStatement(resolvedStatementXri);

		if (log.isTraceEnabled()) log.trace("getStatement(" + statementXri + ") --> " + resolvedStatementXri + " --> " + resolvedStatement);

		return resolvedStatement;
	}

	/*
	 * Helper methods
	 */

	private Graph resolveGraph(XDI3Segment contextNodeXri) {

		XDI3SubSegment firstSubSegment = contextNodeXri.getFirstSubSegment();

		if (XdiPeerRoot.isPeerRootArcXri(firstSubSegment)) {

			firstSubSegment = XdiPeerRoot.getXriOfPeerRootArcXri(firstSubSegment).getFirstSubSegment();
		} else if (XdiInnerRoot.isInnerRootArcXri(firstSubSegment)) {

			firstSubSegment = XdiInnerRoot.getSubjectOfInnerRootXri(firstSubSegment).getFirstSubSegment();
		}

		Graph resolvedGraph = null;

		if (XRI_SS_MSG_VARIABLE.equals(firstSubSegment)) {

			resolvedGraph = this.getMessage().getContextNode().getGraph();
		} else if (XRI_SS_FROM_VARIABLE.equals(firstSubSegment)) {

			resolvedGraph = this.getTargetGraph();
		} else {

			resolvedGraph = this.getTargetGraph();
		}

		if (log.isTraceEnabled()) log.trace("getGraph(" + contextNodeXri + ") --> " + resolvedGraph);

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
