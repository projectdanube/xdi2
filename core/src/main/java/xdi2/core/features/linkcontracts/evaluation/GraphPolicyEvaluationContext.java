package xdi2.core.features.linkcontracts.evaluation;

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
	public XDI3Segment getContextNodeXri(XDI3Segment xri) {

		return xri;
	}

	@Override
	public ContextNode getContextNode(XDI3Segment xri) {

		return this.getGraph().getDeepContextNode(xri);
	}

	@Override
	public Statement getStatement(XDI3Statement statementXri) {

		return this.getGraph().getStatement(statementXri);
	}

	public Graph getGraph() {

		return this.graph;
	}
}
