package xdi2.messaging.context;

import xdi2.core.Graph;

public class ExecutionResult {

	private Graph resultGraph;

	public ExecutionResult(Graph resultGraph) {

		if (resultGraph == null) throw new NullPointerException();

		this.resultGraph = resultGraph;
	}

	public Graph getResultGraph() {

		return this.resultGraph;
	}
}
