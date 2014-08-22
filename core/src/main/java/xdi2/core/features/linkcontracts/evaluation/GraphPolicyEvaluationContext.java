package xdi2.core.features.linkcontracts.evaluation;

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
	public XDIAddress resolveAddress(XDIAddress contextNodeAddress) {

		return contextNodeAddress;
	}

	@Override
	public ContextNode getContextNode(XDIAddress contextNodeAddress) {

		return this.getGraph().getDeepContextNode(contextNodeAddress, false);
	}

	@Override
	public Statement getStatement(XDIStatement statementAddress) {

		return this.getGraph().getStatement(statementAddress);
	}

	public Graph getGraph() {

		return this.graph;
	}

	public void setGraph(Graph graph) {

		this.graph = graph;
	}
}
