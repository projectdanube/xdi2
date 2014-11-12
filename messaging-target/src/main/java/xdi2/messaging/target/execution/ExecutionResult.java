package xdi2.messaging.target.execution;

import java.util.HashMap;
import java.util.Map;

import xdi2.core.Graph;
import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.util.CopyUtil;
import xdi2.messaging.operations.Operation;

public class ExecutionResult {

	private Map<Operation, Graph> operationResultGraphs;
	private Graph resultGraph;

	public ExecutionResult() {

		this.operationResultGraphs = new HashMap<Operation, Graph> ();
		this.resultGraph = null;
	}

	public Graph getOperationResultGraph(Operation operation) {

		Graph operationResultGraph = this.operationResultGraphs.get(operation);

		if (operationResultGraph == null) {

			operationResultGraph = MemoryGraphFactory.getInstance().openGraph();
			this.operationResultGraphs.put(operation, operationResultGraph);
		}

		return operationResultGraph;
	}

	public void putOperationResultGraph(Operation operation, Graph operationResultGraph) {

		if (operation == null) throw new NullPointerException();
		if (operationResultGraph == null) throw new NullPointerException();

		if (this.resultGraph != null) throw new Xdi2RuntimeException("Result graph has already been finished.");

		this.operationResultGraphs.put(operation, operationResultGraph);
	}

	public Map<Operation, Graph> getOperationResultGraphs() {

		return this.operationResultGraphs;
	}

	public Graph getResultGraph() {

		if (this.resultGraph == null) {

			this.resultGraph = MemoryGraphFactory.getInstance().openGraph();
			for (Graph operationResultGraph : this.getOperationResultGraphs().values()) CopyUtil.copyGraph(operationResultGraph, this.resultGraph, null);
		}

		return this.resultGraph;
	}
}
