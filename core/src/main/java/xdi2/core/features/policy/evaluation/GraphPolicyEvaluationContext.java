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
	public XDIAddress resolveXDIAddress(XDIAddress XDIaddress) {

		return XDIaddress;
	}

	@Override
	public ContextNode getContextNode(XDIAddress XDIaddress) {

		if (XDIaddress.isLiteralNodeXDIAddress()) {

			return this.getGraph().getDeepContextNode(XDIaddress.getContextNodeXDIAddress());
		} else {

			return this.getGraph().getDeepContextNode(XDIaddress);
		}
	}

	@Override
	public Statement getStatement(XDIStatement XDIstatement) {

		return this.getGraph().getStatement(XDIstatement);
	}

	public Graph getGraph() {

		return this.graph;
	}

	public void setGraph(Graph graph) {

		this.graph = graph;
	}
}
