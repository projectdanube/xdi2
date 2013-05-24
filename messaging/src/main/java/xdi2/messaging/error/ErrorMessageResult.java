package xdi2.messaging.error;

import java.util.Date;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Literal;
import xdi2.core.Statement;
import xdi2.core.features.nodetypes.XdiAbstractContext;
import xdi2.core.features.nodetypes.XdiAttributeSingleton;
import xdi2.core.features.nodetypes.XdiInnerRoot;
import xdi2.core.features.nodetypes.XdiLocalRoot;
import xdi2.core.features.nodetypes.XdiValue;
import xdi2.core.features.timestamps.Timestamps;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3SubSegment;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.ExecutionContext;

public class ErrorMessageResult extends MessageResult {

	private static final long serialVersionUID = 8816468280233966339L;

	public static final XDI3Segment XRI_S_FALSE = XDI3Segment.create("$false");
	public static final XDI3Segment XRI_S_ERROR = XDI3Segment.create("" + XdiAttributeSingleton.createArcXri(XDI3SubSegment.create("$error")));

	public static final XDI3SubSegment XRI_SS_FALSE = XDI3SubSegment.create("$false");

	public static final String DEFAULT_ERRORSTRING = "XDI error.";

	protected ErrorMessageResult(Graph graph) {

		super(graph);
	}

	public ErrorMessageResult() {

		super();
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

		if (XdiAbstractContext.fromContextNode(graph.getRootContextNode()).getXdiAttributeSingleton(XRI_SS_FALSE, false) == null) return false;

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

		// determine error string

		String errorString = ex.getMessage();
		if (errorString == null) errorString = ex.getClass().getName();

		// build an error result

		ErrorMessageResult errorMessageResult = new ErrorMessageResult();
		if (errorString != null) errorMessageResult.setErrorString(errorString);

		// information specific to certain exceptions

		if (ex instanceof Xdi2MessagingException) {

			ExecutionContext executionContext = ((Xdi2MessagingException) ex).getExecutionContext();
			Operation operation = executionContext == null ? null : executionContext.getExceptionOperation();

			if (operation != null) errorMessageResult.setErrorOperation(operation);
		}

		// done

		return errorMessageResult;
	}

	/*
	 * Instance methods
	 */

	public ContextNode getErrorContextNode() {

		return this.getGraph().setDeepContextNode(XRI_S_FALSE);
	}

	public Date getErrorTimestamp() {

		return Timestamps.getContextNodeTimestamp(this.getErrorContextNode());
	}

	public void setErrorTimestamp(Date timestamp) {

		Timestamps.setContextNodeTimestamp(this.getErrorContextNode(), timestamp);
	}

	public String getErrorString() {

		XdiAttributeSingleton xdiAttributeSingleton = XdiAbstractContext.fromContextNode(this.getGraph().getRootContextNode()).getXdiAttributeSingleton(XRI_SS_FALSE, false);
		if (xdiAttributeSingleton == null) return null;

		XdiValue xdiValue = xdiAttributeSingleton.getXdiValue(false);
		if (xdiValue == null) return null;

		Literal errorStringLiteral = xdiValue.getContextNode().getLiteral();
		if (errorStringLiteral == null) return null;

		return errorStringLiteral.getLiteralData();
	}

	public void setErrorString(String errorString) {

		XdiAttributeSingleton xdiAttributeSingleton = XdiAbstractContext.fromContextNode(this.getGraph().getRootContextNode()).getXdiAttributeSingleton(XRI_SS_FALSE, true);
		XdiValue xdiValue = xdiAttributeSingleton.getXdiValue(true);

		Literal errorStringLiteral = xdiValue.getContextNode().getLiteral();

		if (errorStringLiteral != null) 
			errorStringLiteral.setLiteralData(errorString); 
		else
			xdiValue.getContextNode().createLiteral(errorString);
	}

	public void setErrorOperation(Operation operation) {

		XdiInnerRoot innerRoot = XdiLocalRoot.findLocalRoot(this.getGraph()).findInnerRoot(XRI_S_FALSE, XRI_S_ERROR, true);
		innerRoot.getContextNode().clear();

//		Relation relation = ((RelationStatement) innerRoot.createRelativeStatement(operation.getRelation().getStatement().getXri())).getRelation();

		for (Statement statement : operation.getMessage().getContextNode().getAllStatements()) innerRoot.createRelativeStatement(statement.getXri());
//		CopyUtil.copyContextNodeContents(operation.getRelation().follow(), relation.follow(), null);
	}
}
