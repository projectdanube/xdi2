package xdi2.messaging.response;

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
public class ErrorMessagingResponse extends AbstractMessagingResponse implements MessagingResponse {

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

	/*
	 * Static methods
	 */

	public static boolean isValid(Graph graph) {

		if (! ResultGraphMessagingResponse.isValid(graph)) return false;

		if (XdiAbstractContext.fromContextNode(graph.getRootContextNode(false)).getXdiAttributeSingleton(XdiAttributeSingleton.createAttributeSingletonXDIArc(XDI_ARC_FALSE), false) == null) return false;

		return true;
	}

	public static ErrorMessagingResponse fromGraph(Graph graph) {

		if (! isValid(graph)) return(null);

		return new ErrorMessagingResponse(graph, null);
	}

	public static ErrorMessagingResponse fromException(Exception ex) {

		// new messaging response

		ErrorMessagingResponse errorMessagingResponse = new ErrorMessagingResponse(MemoryGraphFactory.getInstance().openGraph(), ex);

		// set error string

		String errorString = ex.getMessage();
		if (errorString == null) errorString = ex.getClass().getName();
		if (errorString != null) errorMessagingResponse.setErrorString(errorString);

		// information specific to certain exceptions

		if (ex instanceof Xdi2MessagingException) {

			ExecutionContext executionContext = ((Xdi2MessagingException) ex).getExecutionContext();
			Operation operation = executionContext == null ? null : executionContext.getExceptionOperation();

			if (operation != null) errorMessagingResponse.setErrorOperation(operation);
		}

		// done

		return errorMessagingResponse;
	}

	/*
	 * Instance methods
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
}
