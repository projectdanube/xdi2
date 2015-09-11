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
import xdi2.core.features.nodetypes.XdiPeerRoot;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.util.CopyUtil;
import xdi2.core.util.XDIAddressUtil;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.constants.XDIMessagingConstants;
import xdi2.messaging.operations.Operation;
import xdi2.messaging.response.FullMessagingResponse;
import xdi2.messaging.response.LightMessagingResponse;
import xdi2.messaging.target.MessagingTarget;
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
	private Map<Operation, Graph> operationPushResultGraphs;
	private Throwable ex;
	private Exception resultGraphFinishedEx;

	private ExecutionResult(Map<Operation, Graph> operationResultGraphs, Map<Operation, Graph> operationPushResultGraphs) {

		this.operationResultGraphs = operationResultGraphs;
		this.operationPushResultGraphs = operationPushResultGraphs;
		this.ex = null;
		this.resultGraphFinishedEx = null;
	}

	/*
	 * Static methods
	 */

	public static ExecutionResult createExecutionResult(MessageEnvelope messageEnvelope) {

		if (messageEnvelope == null) throw new NullPointerException();

		// set up operation result graphs

		Map<Operation, Graph> operationResultGraphs = new HashMap<Operation, Graph> ();
		for (Operation operation : messageEnvelope.getOperations()) operationResultGraphs.put(operation, null);

		// set up operation push result graphs

		Map<Operation, Graph> operationPushResultGraphs = new HashMap<Operation, Graph> ();
		for (Operation operation : messageEnvelope.getOperations()) operationPushResultGraphs.put(operation, null);

		// create execution result

		ExecutionResult executionResult = new ExecutionResult(operationResultGraphs, operationPushResultGraphs);

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

	public Graph createOperationPushResultGraph(Operation operation) {

		if (operation == null) throw new NullPointerException();

		if (this.isFinished()) throw new Xdi2RuntimeException("Execution result has already been finished.", this.resultGraphFinishedEx);
		if (! this.operationPushResultGraphs.containsKey(operation)) throw new Xdi2RuntimeException("No operation push result graph for operation " + operation);
		if (this.operationPushResultGraphs.get(operation) != null) throw new Xdi2RuntimeException("Operation push result graph for operation " + operation + " has already been created.");

		Graph operationPushResultGraph = MemoryGraphFactory.getInstance().openGraph();
		this.operationPushResultGraphs.put(operation, operationPushResultGraph);

		return operationPushResultGraph;
	}

	public void addException(Throwable ex) {

		if (ex == null) throw new NullPointerException();

		if (this.isFinished()) throw new Xdi2RuntimeException("Execution result has already been finished.", this.resultGraphFinishedEx);
		if (this.ex != null) throw new Xdi2RuntimeException("Already have an exception.");

		this.ex = ex;
	}

	public Graph getFinishedOperationResultGraph(Operation operation) {

		if (operation == null) throw new NullPointerException();

		if (! this.isFinished()) throw new Xdi2RuntimeException("Execution result has not been finished yet.", this.resultGraphFinishedEx);
		if (! this.operationResultGraphs.containsKey(operation)) throw new Xdi2RuntimeException("No operation result graph for operation " + operation);
		if (this.operationResultGraphs.get(operation) == null) throw new Xdi2RuntimeException("Operation result graph for operation " + operation + " has not been created.");

		return this.operationResultGraphs.get(operation);
	}

	public Graph getFinishedOperationPushResultGraph(Operation operation) {

		if (operation == null) throw new NullPointerException();

		if (! this.isFinished()) throw new Xdi2RuntimeException("Execution result has not been finished yet.", this.resultGraphFinishedEx);
		if (! this.operationPushResultGraphs.containsKey(operation)) throw new Xdi2RuntimeException("No operation push result graph for operation " + operation);

		return this.operationPushResultGraphs.get(operation);
	}

	public boolean isFinished() {

		return this.resultGraphFinishedEx != null;
	}

	/*
	 * Helper methods
	 */

	public final LightMessagingResponse makeLightMessagingResponse() {

		if (! this.isFinished()) throw new Xdi2RuntimeException("Execution result has not been finished yet.", this.resultGraphFinishedEx);

		// result graph

		Graph resultGraph = MemoryGraphFactory.getInstance().openGraph();

		for (Graph operationResultGraph : this.operationResultGraphs.values()) {

			if (operationResultGraph == null) continue;

			CopyUtil.copyGraph(operationResultGraph, resultGraph, null);
		}

		// create messaging response

		LightMessagingResponse lightMessagingResponse = LightMessagingResponse.fromResultGraph(resultGraph);

		// done

		return lightMessagingResponse;
	}

	public final FullMessagingResponse makeFullMessagingResponse(MessageEnvelope messageEnvelope, MessagingTarget messagingTarget) {

		if (! this.isFinished()) throw new Xdi2RuntimeException("Execution result has not been finished yet.", this.resultGraphFinishedEx);

		// create messaging response

		MessageEnvelope responseMessageEnvelope = new MessageEnvelope();

		for (Message message : messageEnvelope.getMessages()) {

			XDIArc toPeerRootXDIArc = message.getFromPeerRootXDIArc();
			XDIArc fromPeerRootXDIArc = message.getToPeerRootXDIArc();
			XDIAddress senderXDIAddress = XdiPeerRoot.getXDIAddressOfPeerRootXDIArc(messagingTarget.getOwnerPeerRootXDIArc());

			Message responseMessage = responseMessageEnvelope.createMessage(senderXDIAddress);
			responseMessage.setFromPeerRootXDIArc(fromPeerRootXDIArc);
			responseMessage.setToPeerRootXDIArc(toPeerRootXDIArc);
			responseMessage.setTimestamp(new Date());
			responseMessage.setCorrelationXDIAddress(message.getContextNode().getXDIAddress());

			for (Operation operation : message.getOperations()) {

				Graph operationResultGraph = this.getFinishedOperationResultGraph(operation);
				Graph operationPushResultGraph = this.getFinishedOperationPushResultGraph(operation);

				if (operationResultGraph != null) {

					responseMessage.createOperationResult(operation.getOperationXDIAddress(), operationResultGraph);
				}

				if (operationPushResultGraph != null) {

					responseMessage.createOperationResult(XDIAddressUtil.concatXDIAddresses(operation.getOperationXDIAddress(), XDIMessagingConstants.XDI_ADD_PUSH), operationPushResultGraph);
				}
			}
		}

		FullMessagingResponse fullMessagingResponse = FullMessagingResponse.fromMessageEnvelope(responseMessageEnvelope);

		// done

		return fullMessagingResponse;
	}

	public void finish() {

		if (this.isFinished()) throw new Xdi2RuntimeException("Execution result has already been finished.");

		// finish exception

		this.finishException();

		// done

		this.resultGraphFinishedEx = new Exception();
		if (log.isInfoEnabled()) log.info("Execution result finished.");
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

		for (Map.Entry<Operation, Graph> entry : this.operationResultGraphs.entrySet()) {

			Operation operation = entry.getKey();
			Graph operationResultGraph = entry.getValue();

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
