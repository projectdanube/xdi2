package xdi2.messaging.error;

import java.util.Date;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.LiteralNode;
import xdi2.core.features.nodetypes.XdiAbstractContext;
import xdi2.core.features.nodetypes.XdiAttributeSingleton;
import xdi2.core.features.nodetypes.XdiCommonRoot;
import xdi2.core.features.nodetypes.XdiInnerRoot;
import xdi2.core.features.timestamps.Timestamps;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.syntax.XDIStatement;
import xdi2.core.util.iterators.MappingXDIStatementIterator;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import xdi2.messaging.context.ExecutionContext;
import xdi2.messaging.exceptions.Xdi2MessagingException;

public class ErrorMessageResult extends MessageResult {

	private static final long serialVersionUID = 8816468280233966339L;

	public static final XDIAddress XDI_ADD_FALSE = XDIAddress.create("$false");
	public static final XDIAddress XDI_ADD_ERROR = XDIAddress.create("" + XdiAttributeSingleton.createXDIArc(XDIArc.create("$error")));

	public static final XDIArc XDI_ARC_FALSE = XDIArc.create("$false");

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

		if (XdiAbstractContext.fromContextNode(graph.getRootContextNode(false)).getXdiAttributeSingleton(XdiAttributeSingleton.createXDIArc(XDI_ARC_FALSE), false) == null) return false;

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

		return this.getGraph().setDeepContextNode(XDI_ADD_FALSE);
	}

	public Date getErrorTimestamp() {

		return Timestamps.getTimestamp(XdiAbstractContext.fromContextNode(this.getErrorContextNode()));
	}

	public void setErrorTimestamp(Date timestamp) {

		Timestamps.setTimestamp(XdiAbstractContext.fromContextNode(this.getErrorContextNode()), timestamp);
	}

	public String getErrorString() {

		XdiAttributeSingleton xdiAttributeSingleton = XdiAbstractContext.fromContextNode(this.getGraph().getRootContextNode(false)).getXdiAttributeSingleton(XdiAttributeSingleton.createXDIArc(XDI_ARC_FALSE), false);
		if (xdiAttributeSingleton == null) return null;

		LiteralNode errorStringLiteral = xdiAttributeSingleton.getLiteralNode();
		if (errorStringLiteral == null) return null;

		return errorStringLiteral.getLiteralDataString();
	}

	public void setErrorString(String errorString) {

		XdiAttributeSingleton xdiAttributeSingleton = XdiAbstractContext.fromContextNode(this.getGraph().getRootContextNode(false)).getXdiAttributeSingleton(XdiAttributeSingleton.createXDIArc(XDI_ARC_FALSE), true);

		xdiAttributeSingleton.setLiteralDataString(errorString);
	}

	public void setErrorOperation(Operation operation) {

		XdiInnerRoot xdiInnerRoot = XdiCommonRoot.findCommonRoot(this.getGraph()).getInnerRoot(XDI_ADD_FALSE, XDI_ADD_ERROR, true);
		xdiInnerRoot.getContextNode().clear();

		//		Relation relation = ((RelationStatement) innerRoot.createRelativeStatement(operation.getRelation().getStatement().getAddress())).getRelation();

		for (XDIStatement XDIstatement : new MappingXDIStatementIterator(operation.getMessage().getContextNode().getAllStatements())) {

			xdiInnerRoot.getContextNode().setStatement(XDIstatement);
		}

		//		CopyUtil.copyContextNodeContents(operation.getRelation().follow(), relation.follow(), null);
	}
}
