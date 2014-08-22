package xdi2.core.features.policy.evaluation;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Statement;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;

public class GraphPolicyEvaluationContext implements PolicyEvaluationContext {

	private Graph graph;

	public GraphPolicyEvaluationContext(Graph graph) {

		this.graph = graph;
	}

	@Override
	public XDI3Segment resolveXri(XDI3Segment contextNodeXri) {

		return contextNodeXri;
	}

	@Override
	public ContextNode getContextNode(XDI3Segment contextNodeXri) {

		return this.getGraph().getDeepContextNode(contextNodeXri, false);
	}

	@Override
	public Statement getStatement(XDI3Statement statementXri) {

		return this.getGraph().getStatement(statementXri);
	}

	public Graph getGraph() {

		return this.graph;
	}

	public void setGraph(Graph graph) {

		this.graph = graph;
	}
}
