package xdi2.messaging.target.execution;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
	private Exception ex;
	private Graph resultGraph;
	private Exception resultGraphFinishedEx;

	private ExecutionResult(Map<Operation, Graph> operationResultGraphs) {

		this.operationResultGraphs = operationResultGraphs;
		this.ex = null;
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

	/*
	 * Instance methods
	 */

	public Graph createOperationResultGraph(Operation operation) {

		if (operation == null) throw new NullPointerException();

		if (this.isFinished()) throw new Xdi2RuntimeException("Execution result has already been finished.", this.resultGraphFinishedEx);
		if (! this.operationResultGraphs.containsKey(operation)) throw new Xdi2RuntimeException("No operation result graph for operation " + operation);
		if (this.operationResultGraphs.get(operation) != null) throw new Xdi2RuntimeException("Operation result graph for operation " + operation + " has already been created.");

		Graph operationResultGraph = MemoryGraphFactory.getInstance().openGraph();
		this.operationResultGraphs.put(operation, operationResultGraph);

		return operationResultGraph;
	}

	public Map<Operation, Graph> getOperationResultGraphs() {

		if (this.isFinished()) throw new Xdi2RuntimeException("Execution result has already been finished.", this.resultGraphFinishedEx);

		return this.operationResultGraphs;
	}

	public void addException(Exception ex) {

		if (ex == null) throw new NullPointerException();

		if (this.isFinished()) throw new Xdi2RuntimeException("Execution result has already been finished.", this.resultGraphFinishedEx);
		if (this.ex != null) throw new Xdi2RuntimeException("Already have an exception.");

		this.ex = ex;
	}

	public Graph getFinishedResultGraph() {

		if (! this.isFinished()) throw new Xdi2RuntimeException("Execution result has not been finished yet.", this.resultGraphFinishedEx);

		return this.resultGraph;
	}

	public Graph getFinishedOperationResultGraph(Operation operation) {

		if (operation == null) throw new NullPointerException();

		if (! this.isFinished()) throw new Xdi2RuntimeException("Execution result has not been finished yet.", this.resultGraphFinishedEx);
		if (! this.operationResultGraphs.containsKey(operation)) throw new Xdi2RuntimeException("No operation result graph for operation " + operation);
		if (this.operationResultGraphs.get(operation) == null) throw new Xdi2RuntimeException("Operation result graph for operation " + operation + " has not been created.");

		return this.operationResultGraphs.get(operation);
	}

	public boolean isFinished() {

		return this.resultGraph != null;
	}

	/*
	 * Helper methods
	 */

	public void finish() {

		if (this.isFinished()) throw new Xdi2RuntimeException("Execution result has already been finished.");

		this.resultGraph = MemoryGraphFactory.getInstance().openGraph();
		this.resultGraphFinishedEx = new Exception();

		// finish exception

		this.finishException();

		// finish operation result graphs

		for (Graph operationResultGraph : this.operationResultGraphs.values()) {

			if (operationResultGraph == null) continue;

			CopyUtil.copyGraph(operationResultGraph, this.resultGraph, null);
		}

		// done

		if (log.isInfoEnabled()) log.info("Execution result finished.");
	}

	public void finish(Graph finishedResultGraph) {

		if (this.isFinished()) throw new Xdi2RuntimeException("Execution result has already been finished.");

		this.resultGraph = finishedResultGraph;
		this.resultGraphFinishedEx = new Exception();
	}

	private void finishException() {

		if (this.ex == null) return;

		// error string

		String errorString = this.ex.getMessage();
		if (errorString == null) errorString = this.ex.getClass().getSimpleName();

		if (log.isInfoEnabled()) log.info("Error string: " + errorString);

		// look for exception operation

		Message exceptionMessage = null;
		Operation exceptionOperation = null;

		if (this.ex instanceof Xdi2MessagingException) {

			ExecutionContext executionContext = ((Xdi2MessagingException) this.ex).getExecutionContext();
			exceptionMessage = executionContext == null ? null : executionContext.getExceptionMessage();
			exceptionOperation = executionContext == null ? null : executionContext.getExceptionOperation();
			if (! this.operationResultGraphs.containsKey(exceptionOperation)) exceptionOperation = null;
		}

		if (log.isInfoEnabled()) log.info("Exception message: " + exceptionMessage + " - Exception operation: " + exceptionOperation);

		// look at all operations

		for (Operation operation : this.operationResultGraphs.keySet()) {

			Graph operationResultGraph = this.operationResultGraphs.get(operation);

			boolean setErrorForThisOperation = false;

			if (exceptionOperation != null && exceptionOperation.equals(operation)) setErrorForThisOperation = true;
			if (exceptionOperation == null && exceptionMessage != null && exceptionMessage.equals(operation.getMessage())) setErrorForThisOperation = true;
			if (exceptionOperation == null && exceptionMessage == null) setErrorForThisOperation = true;

			if (! setErrorForThisOperation) continue;

			// write into operation result graph

			Graph exceptionOperationResultGraph = MemoryGraphFactory.getInstance().openGraph();

			XdiError xdiError = XdiError.findXdiError(XdiCommonRoot.findCommonRoot(exceptionOperationResultGraph), true);
			xdiError.setErrorString(errorString);
			xdiError.setErrorTimestamp(new Date());

			if (log.isDebugEnabled()) log.debug("For operation " + operation + " we have exception operation result graph " + exceptionOperationResultGraph);

			if (operationResultGraph != null)
				CopyUtil.copyGraph(exceptionOperationResultGraph, operationResultGraph, null);
			else
				this.operationResultGraphs.put(operation, exceptionOperationResultGraph);
		}
	}

	/*
	 * Object methods
	 */

	@Override
	public String toString() {

		StringBuffer buffer = new StringBuffer();

		buffer.append("" + this.operationResultGraphs + " / ");
		buffer.append("" + this.ex);

		return buffer.toString();
	}
}
