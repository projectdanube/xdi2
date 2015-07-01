package xdi2.messaging.target.execution;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import xdi2.core.Graph;
import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.core.features.nodetypes.XdiCommonRoot;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.util.CopyUtil;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.error.MessagingError;
import xdi2.messaging.operations.Operation;
import xdi2.messaging.target.exceptions.Xdi2MessagingException;

/**
 * This class represent a result of a message envelope that has been executed
 * against a messaging target. Basically this encapsulates the results of all
 * the individual individuals in the message envelope.
 * 
 * The result graph of the individual operations constitute the overall result
 * graph.
 * 
 * This is not yet a complete messaging response which a responder would return
 * to a requester.
 */
public final class ExecutionResult {

	private Map<Operation, Graph> operationResultGraphs;
	private Graph resultGraph;

	private ExecutionResult(Map<Operation, Graph> operationResultGraphs) {

		this.operationResultGraphs = operationResultGraphs;
		this.resultGraph = null;
	}

	public static ExecutionResult forMessageEnvelope(MessageEnvelope messageEnvelope) {

		Map<Operation, Graph> operationResultGraphs = new HashMap<Operation, Graph> ();

		for (Operation operation : messageEnvelope.getOperations()) {

			operationResultGraphs.put(operation, MemoryGraphFactory.getInstance().openGraph());
		}

		return new ExecutionResult(operationResultGraphs);
	}

	public Graph getOperationResultGraph(Operation operation) {

		if (operation == null) throw new NullPointerException();

		if (this.isFinished()) throw new Xdi2RuntimeException("Result graph has already been finished.");
		if (! this.operationResultGraphs.containsKey(operation)) throw new Xdi2RuntimeException("No operation result graph for operation " + operation);

		return this.operationResultGraphs.get(operation);
	}

	public void putOperationResultGraph(Operation operation, Graph operationResultGraph) {

		if (operation == null) throw new NullPointerException();
		if (operationResultGraph == null) throw new NullPointerException();

		if (this.isFinished()) throw new Xdi2RuntimeException("Result graph has already been finished.");
		if (! this.operationResultGraphs.containsKey(operation)) throw new Xdi2RuntimeException("No operation result graph for operation " + operation);

		this.operationResultGraphs.put(operation, operationResultGraph);
	}

	public Map<Operation, Graph> getOperationResultGraphs() {

		return this.operationResultGraphs;
	}

	public Graph getResultGraph() {

		if (! this.isFinished()) throw new Xdi2RuntimeException("Result graph has not been finished yet.");

		return this.resultGraph;
	}

	public void setException(Exception ex) {

		// restart the result graph

		this.resultGraph = null;

		// error string

		String errorString = ex.getMessage();
		if (errorString == null) errorString = ex.getClass().getName();

		// see if the exception corresponds to a specific operation

		Graph singleOperationResultGraph = null;

		if (ex instanceof Xdi2MessagingException) {

			singleOperationResultGraph = this.getOperationResultGraph(((Xdi2MessagingException) ex).getExecutionContext().getExceptionOperation());
		}

		// put the error into that specific operation result graph, or in all of them

		Collection<Graph> operationResultGraphs = null;
		if (singleOperationResultGraph != null) operationResultGraphs = Collections.singleton(singleOperationResultGraph);
		if (operationResultGraphs == null) operationResultGraphs = this.getOperationResultGraphs().values();

		for (Graph operationResultGraph : operationResultGraphs) {

			operationResultGraph.clear();
			MessagingError messagingError = MessagingError.findMessagingError(XdiCommonRoot.findCommonRoot(operationResultGraph), true);
			messagingError.setErrorString(errorString);
		}

		// finish the result graph

		this.finish();
	}

	public boolean isFinished() {

		return this.resultGraph != null;
	}

	public void finish() {

		if (this.isFinished()) throw new Xdi2RuntimeException("Result graph has already been finished.");

		this.resultGraph = MemoryGraphFactory.getInstance().openGraph();
		for (Graph operationResultGraph : this.getOperationResultGraphs().values()) CopyUtil.copyGraph(operationResultGraph, this.resultGraph, null);
	}
}
