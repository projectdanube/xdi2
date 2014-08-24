package xdi2.core.features.policy.evaluation;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Statement;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIStatement;

public class GraphPolicyEvaluationContext implements PolicyEvaluationContext {

	private Graph graph;

	public GraphPolicyEvaluationContext(Graph graph) {

		this.graph = graph;
	}

	@Override
	public XDIAddress resolveXDIAddress(XDIAddress contextNodeXri) {

		return contextNodeXri;
	}

	@Override
	public ContextNode getContextNode(XDIAddress contextNodeXri) {

		return this.getGraph().getDeepContextNode(contextNodeXri, false);
	}

	@Override
	public Statement getStatement(XDIStatement statementXri) {

		return this.getGraph().getStatement(statementXri);
	}

	public Graph getGraph() {

		return this.graph;
	}

	public void setGraph(Graph graph) {

		this.graph = graph;
	}
}
