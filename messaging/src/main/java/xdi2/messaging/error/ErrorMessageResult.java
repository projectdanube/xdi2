package xdi2.messaging.error;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.ExecutionContext;

public class ErrorMessageResult extends MessageResult {

	private static final long serialVersionUID = 8816468280233966339L;

	public static final XRI3Segment XRI_S_ERRORCODE = new XRI3Segment("$false$integer");
	public static final XRI3Segment XRI_S_ERRORSTRING = new XRI3Segment("$false$string");
	public static final XRI3Segment XRI_S_ERROROPERATION = new XRI3Segment("$false$operation");

	public static final String DEFAULT_ERRORCODE = "-1";
	public static final String DEFAULT_ERRORSTRING = "XDI error.";
	public static final String DEFAULT_ERROROPERATION = "";

	protected ErrorMessageResult(Graph graph) {

		super(graph);
	}

	public ErrorMessageResult() {

		super();

		this.graph.findContextNode(XRI_S_ERRORCODE, true).createLiteral(DEFAULT_ERRORCODE.toString());
		this.graph.findContextNode(XRI_S_ERRORSTRING, true).createLiteral(DEFAULT_ERRORSTRING);
		this.graph.findContextNode(XRI_S_ERROROPERATION, true).createLiteral(DEFAULT_ERROROPERATION);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a graph is a valid XDI message result.
	 * @param graph The graph to check.
	 * @return True if the graph is a valid XDI message result.
	 */
	public static boolean isValid(Graph graph) {

		if (! MessageResult.isValid(graph)) return false;

		if (! graph.containsContextNode(XRI_S_ERRORCODE)) return false;
		if (! graph.containsContextNode(XRI_S_ERRORSTRING)) return false;
		if (! graph.containsContextNode(XRI_S_ERROROPERATION)) return false;

		return true;
	}

	/**
	 * Factory method that creates an XDI error message result bound to a given graph.
	 * @param graph The graph that is an XDI error message result.
	 * @return The XDI error message result.
	 */
	public static ErrorMessageResult fromGraph(Graph graph) {

		if (! isValid(graph)) return(null);

		return new ErrorMessageResult(graph);
	}

	/**
	 * Factory method that creates an XDI error message from an exception.
	 * @param ex The exception.
	 * @return The XDI error message result.
	 */
	public static ErrorMessageResult fromException(Exception ex) {

		// determine error code, error string

		Integer errorCode = new Integer(0);

		String errorString = ex.getMessage();
		if (errorString == null) errorString = ex.getClass().getName();

		// build an error result

		ErrorMessageResult errorMessageResult = new ErrorMessageResult();
		if (errorCode != null) errorMessageResult.setErrorCode(errorCode.toString());
		if (errorString != null) errorMessageResult.setErrorString(errorString);

		// information specific to messaging

		if (ex instanceof Xdi2MessagingException) {

			ExecutionContext executionContext = ((Xdi2MessagingException) ex).getExecutionContext();
			Operation operation = executionContext == null ? null : executionContext.getOperation();

			if (operation != null) errorMessageResult.setErrorOperation(operation.getRelation().getStatement().toString());
		}

		// done

		return errorMessageResult;
	}

	/*
	 * Instance methods
	 */

	public String getErrorCode() {

		ContextNode contextNode = this.graph.findContextNode(XRI_S_ERRORCODE, false);
		if (contextNode == null || ! contextNode.containsLiteral()) return null;

		return contextNode.getLiteral().getLiteralData();
	}

	public void setErrorCode(String errorCode) {

		ContextNode contextNode = this.graph.findContextNode(XRI_S_ERRORCODE, true);

		if (contextNode.containsLiteral()) {

			contextNode.getLiteral().setLiteralData(errorCode.toString());
		} else {

			contextNode.createLiteral(errorCode);
		}
	}

	public String getErrorString() {

		ContextNode contextNode = this.graph.findContextNode(XRI_S_ERRORSTRING, false);
		if (contextNode == null || ! contextNode.containsLiteral()) return null;

		return contextNode.getLiteral().getLiteralData();
	}

	public void setErrorString(String errorString) {

		ContextNode contextNode = this.graph.findContextNode(XRI_S_ERRORSTRING, true);

		if (contextNode.containsLiteral()) {

			contextNode.getLiteral().setLiteralData(errorString);
		} else {

			contextNode.createLiteral(errorString);
		}
	}

	public String getErrorOperation() {

		ContextNode contextNode = this.graph.findContextNode(XRI_S_ERROROPERATION, false);
		if (contextNode == null || ! contextNode.containsLiteral()) return null;

		return contextNode.getLiteral().getLiteralData();
	}

	public void setErrorOperation(String errorOperation) {

		ContextNode contextNode = this.graph.findContextNode(XRI_S_ERROROPERATION, true);

		if (contextNode.containsLiteral()) {

			contextNode.getLiteral().setLiteralData(errorOperation);
		} else {

			contextNode.createLiteral(errorOperation);
		}
	}
}
