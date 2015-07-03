package xdi2.messaging.target.execution;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.Graph;
import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.core.features.error.XdiError;
import xdi2.core.features.nodetypes.XdiCommonRoot;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.util.CopyUtil;
import xdi2.messaging.MessageEnvelope;
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

	private static final Logger log = LoggerFactory.getLogger(ExecutionResult.class);

	private Map<Operation, Graph> operationResultGraphs;
	private Graph resultGraph;

	private ExecutionResult(Map<Operation, Graph> operationResultGraphs) {

		this.operationResultGraphs = operationResultGraphs;
		this.resultGraph = null;
	}

	/*
	 * Static methods
	 */

	public static ExecutionResult createExecutionResult(MessageEnvelope messageEnvelope) {

		if (messageEnvelope == null) throw new NullPointerException();
		
		// set up operation result graphs

		Map<Operation, Graph> operationResultGraphs = new HashMap<Operation, Graph> ();

		for (Operation operation : messageEnvelope.getOperations()) {

			operationResultGraphs.put(operation, MemoryGraphFactory.getInstance().openGraph());
		}

		// create execution result

		return new ExecutionResult(operationResultGraphs);
	}

	public static ExecutionResult createExecutionResult(ExecutionResult executionResult, Exception ex) {

		if (executionResult == null) throw new NullPointerException();
		if (ex == null) throw new NullPointerException();

		// error string

		String errorString = ex.getMessage();
		if (errorString == null) errorString = ex.getClass().getName();

		log.info("Error string: " + errorString);

		// look for exception operation

		Operation exceptionOperation = null;

		if (ex instanceof Xdi2MessagingException) {

			ExecutionContext executionContext = ((Xdi2MessagingException) ex).getExecutionContext();
			exceptionOperation = executionContext == null ? null : executionContext.getExceptionOperation();
			if (! executionResult.getOperationResultGraphs().containsKey(exceptionOperation)) exceptionOperation = null;
		}

		log.info("Exception operation: " + exceptionOperation);

		// set up operation result graphs

		Map<Operation, Graph> exceptionOperationResultGraphs = new HashMap<Operation, Graph> ();

		for (Entry<Operation, Graph> entry : executionResult.getOperationResultGraphs().entrySet()) {

			Operation operation = entry.getKey();
			Graph operationResultGraph = entry.getValue();

			Graph exceptionOperationResultGraph = MemoryGraphFactory.getInstance().openGraph();

			if (exceptionOperation == null || exceptionOperation.equals(operation)) {

				XdiError xdiError = XdiError.findXdiError(XdiCommonRoot.findCommonRoot(exceptionOperationResultGraph), true);
				xdiError.setErrorString(errorString);
				xdiError.setErrorTimestamp(new Date());
			} else {

				CopyUtil.copyGraph(operationResultGraph, exceptionOperationResultGraph, null);
			}

			if (log.isDebugEnabled()) log.debug("For operation " + operation + " we have exception operation result graph " + exceptionOperationResultGraph);

			exceptionOperationResultGraphs.put(operation, exceptionOperationResultGraph);
		}

		// create execution result

		ExecutionResult exceptionExecutionResult = new ExecutionResult(exceptionOperationResultGraphs);

		// done

		return exceptionExecutionResult;
	}

	/*
	 * Instance methods
	 */

	public Graph getOperationResultGraph(Operation operation) {

		if (operation == null) throw new NullPointerException();

		if (this.isFinished()) throw new Xdi2RuntimeException("Result graph has already been finished.");
		if (! this.operationResultGraphs.containsKey(operation)) throw new Xdi2RuntimeException("No operation result graph for operation " + operation);

		return this.operationResultGraphs.get(operation);
	}

	public Map<Operation, Graph> getOperationResultGraphs() {

		return this.operationResultGraphs;
	}

	public Graph getResultGraph() {

		if (! this.isFinished()) this.finish();

		return this.resultGraph;
	}

	public boolean isFinished() {

		return this.resultGraph != null;
	}

	private void finish() {

		if (this.isFinished()) throw new Xdi2RuntimeException("Result graph has already been finished.");

		this.resultGraph = MemoryGraphFactory.getInstance().openGraph();

		for (Graph operationResultGraph : this.getOperationResultGraphs().values()) CopyUtil.copyGraph(operationResultGraph, this.resultGraph, null);
	}
}
