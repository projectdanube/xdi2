package xdi2.core.features.linkcontracts.evaluation;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Relation;
import xdi2.core.xri3.XDI3Segment;

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

		return this.getGraph().findContextNode(xri, false);
	}

	@Override
	public Relation getRelation(XDI3Segment arcXri, XDI3Segment targetContextNodeXri) {

		return this.getGraph().getRootContextNode().getRelation(arcXri, targetContextNodeXri);
	}

	public Graph getGraph() {

		return this.graph;
	}
}
