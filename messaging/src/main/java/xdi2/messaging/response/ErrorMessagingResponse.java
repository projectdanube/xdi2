package xdi2.messaging.response;

import java.io.Serializable;
import java.util.Date;
import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Literal;
import xdi2.core.features.nodetypes.XdiAbstractContext;
import xdi2.core.features.nodetypes.XdiAttributeSingleton;
import xdi2.core.features.nodetypes.XdiCommonRoot;
import xdi2.core.features.nodetypes.XdiInnerRoot;
import xdi2.core.features.nodetypes.XdiValue;
import xdi2.core.features.timestamps.Timestamps;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.syntax.XDIStatement;
import xdi2.core.util.iterators.EmptyIterator;
import xdi2.core.util.iterators.MappingXDIStatementIterator;
import xdi2.messaging.context.ExecutionContext;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.operations.Operation;

/**
 * An error as an XDI messaging response.
 * 
 * @author markus
 */
public class ErrorMessagingResponse implements MessagingResponse, Serializable, Comparable<ErrorMessagingResponse> {

	private static final long serialVersionUID = 8816468280233966339L;

	public static final XDIAddress XDI_ADD_FALSE = XDIAddress.create("$false");
	public static final XDIAddress XDI_ADD_ERROR = XDIAddress.create("" + XdiAttributeSingleton.createAttributeSingletonXDIArc(XDIArc.create("$error")));

	public static final XDIArc XDI_ARC_FALSE = XDIArc.create("$false");

	public static final String DEFAULT_ERRORSTRING = "XDI error.";

	private Graph graph;
	private Exception ex;

	protected ErrorMessagingResponse(Graph graph, Exception ex) {

		this.graph = graph;
		this.ex = ex;
	}

	protected ErrorMessagingResponse(Graph graph) {

		this(graph, null);
	}

	protected ErrorMessagingResponse(Exception ex) {

		this(MemoryGraphFactory.getInstance().openGraph(), ex);
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

		if (! GraphMessagingResponse.isValid(graph)) return false;

		if (XdiAbstractContext.fromContextNode(graph.getRootContextNode(false)).getXdiAttributeSingleton(XdiAttributeSingleton.createAttributeSingletonXDIArc(XDI_ARC_FALSE), false) == null) return false;

		return true;
	}

	/**
	 * Factory method that creates an XDI error message result bound to a given graph.
	 * @param graph The graph that is an XDI error message result.
	 * @return The XDI error message result.
	 */
	public static ErrorMessagingResponse fromGraph(Graph graph) {

		if (! isValid(graph)) return(null);

		return new ErrorMessagingResponse(graph, null);
	}

	/**
	 * Factory method that creates an XDI error message from an exception.
	 * @param ex The exception.
	 * @return The XDI error message result.
	 */
	public static ErrorMessagingResponse fromException(Exception ex) {

		// determine error string

		String errorString = ex.getMessage();
		if (errorString == null) errorString = ex.getClass().getName();

		// build an error result

		ErrorMessagingResponse errorMessageResult = new ErrorMessagingResponse(ex);
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

	/**
	 * Returns the underlying graph to which this error messaging response is bound.
	 * @return The underlying graph.
	 */
	public Graph getGraph() {

		return this.graph;
	}

	public Exception getException() {

		return this.ex;
	}

	@Override
	public Iterator<Graph> getResultGraphs() {

		return new EmptyIterator<Graph> ();
	}

	@Override
	public Graph getResultGraph() {

		return null;
	}

	public Graph getErrorGraph() {

		return this.graph;
	}

	public ContextNode getErrorContextNode() {

		return this.getGraph().setDeepContextNode(XDI_ADD_FALSE);
	}

	public Date getErrorTimestamp() {

		return Timestamps.getContextNodeTimestamp(this.getErrorContextNode());
	}

	public void setErrorTimestamp(Date timestamp) {

		Timestamps.setContextNodeTimestamp(this.getErrorContextNode(), timestamp);
	}

	public String getErrorString() {

		XdiAttributeSingleton xdiAttributeSingleton = XdiAbstractContext.fromContextNode(this.getGraph().getRootContextNode(false)).getXdiAttributeSingleton(XdiAttributeSingleton.createAttributeSingletonXDIArc(XDI_ARC_FALSE), false);
		if (xdiAttributeSingleton == null) return null;

		XdiValue xdiValue = xdiAttributeSingleton.getXdiValue(false);
		if (xdiValue == null) return null;

		Literal errorStringLiteral = xdiValue.getContextNode().getLiteral();
		if (errorStringLiteral == null) return null;

		return errorStringLiteral.getLiteralDataString();
	}

	public void setErrorString(String errorString) {

		XdiAttributeSingleton xdiAttributeSingleton = XdiAbstractContext.fromContextNode(this.getGraph().getRootContextNode(false)).getXdiAttributeSingleton(XdiAttributeSingleton.createAttributeSingletonXDIArc(XDI_ARC_FALSE), true);
		XdiValue xdiValue = xdiAttributeSingleton.getXdiValue(true);

		xdiValue.getContextNode().setLiteralString(errorString);
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

	/*
	 * Object methods
	 */

	@Override
	public String toString() {

		return this.getGraph().toString();
	}

	@Override
	public boolean equals(Object object) {

		if (object == null || ! (object instanceof GraphMessagingResponse)) return false;
		if (object == this) return true;

		GraphMessagingResponse other = (GraphMessagingResponse) object;

		return this.getGraph().equals(other.getGraph());
	}

	@Override
	public int hashCode() {

		int hashCode = 1;

		hashCode = (hashCode * 31) + this.getGraph().hashCode();

		return hashCode;
	}

	@Override
	public int compareTo(ErrorMessagingResponse other) {

		if (other == this || other == null) return(0);

		return this.getGraph().compareTo(other.getGraph());
	}
}