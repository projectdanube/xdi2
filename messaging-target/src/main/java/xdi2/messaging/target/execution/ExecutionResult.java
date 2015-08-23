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
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.operations.Operation;
import xdi2.messaging.target.exceptions.Xdi2MessagingException;

/**
 * This class represent a result of a message envelope that has been executed
 * against a messaging target. Basically this encapsulates the results of all
 * the individual operations in the message envelope.
 * 
 * The merged result graphs of the individual operations constitute the
 * overall result graph.
 * 
 * This can be used to create a messaging response which a responder would
 * return to a requester.
 */
public final class ExecutionResult {

	private static final Logger log = LoggerFactory.getLogger(ExecutionResult.class);

	private Map<Operation, Graph> operationResultGraphs;
	private Graph resultGraph;
	private Exception resultGraphFinishedEx;

	private ExecutionResult(Map<Operation, Graph> operationResultGraphs) {

		this.operationResultGraphs = operationResultGraphs;
		this.resultGraph = null;
		this.resultGraphFinishedEx = null;
	}

	/*
	 * Static methods
	 */

	public static ExecutionResult createExecutionResult(MessageEnvelope messageEnvelope) {

		if (messageEnvelope == null) throw new NullPointerException();

		// set up operation result graphs

		Map<Operation, Graph> operationResultGraphs = new HashMap<Operation, Graph> ();

		for (Operation operation : messageEnvelope.getOperations()) {

			operationResultGraphs.put(operation, null);
		}

		// create execution result

		ExecutionResult executionResult = new ExecutionResult(operationResultGraphs);

		// done

		return executionResult;
	}

	public static ExecutionResult createExceptionExecutionResult(ExecutionResult executionResult, Exception ex) {

		if (executionResult == null) throw new NullPointerException();
		if (ex == null) throw new NullPointerException();

		// error string

		String errorString = ex.getMessage();
		if (errorString == null) errorString = ex.getClass().getSimpleName();

		log.info("Error string: " + errorString);

		// look for exception operation

		Message exceptionMessage = null;
		Operation exceptionOperation = null;

		if (ex instanceof Xdi2MessagingException) {

			ExecutionContext executionContext = ((Xdi2MessagingException) ex).getExecutionContext();
			exceptionMessage = executionContext == null ? null : executionContext.getExceptionMessage();
			exceptionOperation = executionContext == null ? null : executionContext.getExceptionOperation();
			if (! executionResult.operationResultGraphs.containsKey(exceptionOperation)) exceptionOperation = null;
		}

		if (log.isInfoEnabled()) log.info("Exception message: " + exceptionMessage + " - Exception operation: " + exceptionOperation);

		// set up operation result graphs

		Map<Operation, Graph> exceptionOperationResultGraphs = new HashMap<Operation, Graph> ();

		for (Entry<Operation, Graph> entry : executionResult.operationResultGraphs.entrySet()) {

			Operation operation = entry.getKey();
			Graph operationResultGraph = entry.getValue();

			Graph exceptionOperationResultGraph = MemoryGraphFactory.getInstance().openGraph();

			boolean setErrorForThisOperation = false;
			
			if (exceptionOperation != null && exceptionOperation.equals(operation)) setErrorForThisOperation = true;
			if (exceptionOperation == null && exceptionMessage != null && exceptionMessage.equals(operation.getMessage())) setErrorForThisOperation = true;
			
			if (setErrorForThisOperation) {

				XdiError xdiError = XdiError.findXdiError(XdiCommonRoot.findCommonRoot(exceptionOperationResultGraph), true);
				xdiError.setErrorString(errorString);
				xdiError.setErrorTimestamp(new Date());
			} else if (operationResultGraph != null) {

				CopyUtil.copyGraph(operationResultGraph, exceptionOperationResultGraph, null);
			}

			if (log.isDebugEnabled()) log.debug("For operation " + operation + " we have exception operation result graph " + exceptionOperationResultGraph);

			exceptionOperationResultGraphs.put(operation, exceptionOperationResultGraph);
		}

		// create execution result

		ExecutionResult exceptionExecutionResult = new ExecutionResult(exceptionOperationResultGraphs);
		exceptionExecutionResult.finish();

		// done

		return exceptionExecutionResult;
	}

	/*
	 * Instance methods
	 */

	public Graph createOperationResultGraph(Operation operation) {

		if (operation == null) throw new NullPointerException();

		if (this.isFinished()) throw new Xdi2RuntimeException("Result graph has already been finished.", this.resultGraphFinishedEx);
		if (! this.operationResultGraphs.containsKey(operation)) throw new Xdi2RuntimeException("No operation result graph for operation " + operation);
		if (this.operationResultGraphs.get(operation) != null) throw new Xdi2RuntimeException("Operation result graph for operation " + operation + " has already been created.");

		Graph operationResultGraph = MemoryGraphFactory.getInstance().openGraph();
		this.operationResultGraphs.put(operation, operationResultGraph);

		return operationResultGraph;
	}

	public Graph getOperationResultGraph(Operation operation) {

		if (operation == null) throw new NullPointerException();

		if (! this.operationResultGraphs.containsKey(operation)) throw new Xdi2RuntimeException("No operation result graph for operation " + operation);
		if (this.operationResultGraphs.get(operation) == null) throw new Xdi2RuntimeException("Operation result graph for operation " + operation + " has not been created.");

		return this.operationResultGraphs.get(operation);
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
		this.resultGraphFinishedEx = new Exception();

		for (Graph operationResultGraph : this.operationResultGraphs.values()) CopyUtil.copyGraph(operationResultGraph, this.resultGraph, null);
	}
}
