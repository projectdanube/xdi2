package xdi2.messaging.response;

import java.util.Date;

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
import xdi2.core.util.iterators.MappingXDIStatementIterator;
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

	private ErrorMessagingResponse(Graph graph) {

		this.graph = graph;
	}

	/*
	 * Static methods
	 */

	public static ErrorMessagingResponse create(String errorString, Operation errorOperation) {

		ErrorMessagingResponse errorMessagingResponse = new ErrorMessagingResponse(MemoryGraphFactory.getInstance().openGraph());

		errorMessagingResponse.setErrorTimestamp(new Date());
		if (errorString != null) errorMessagingResponse.setErrorString(errorString);
		if (errorOperation != null) errorMessagingResponse.setErrorOperation(errorOperation);

		return errorMessagingResponse;
	}

	public static boolean isValid(Graph graph) {

		if (XdiAbstractContext.fromContextNode(graph.getRootContextNode(false)).getXdiAttributeSingleton(XdiAttributeSingleton.createAttributeSingletonXDIArc(XDI_ARC_FALSE), false) == null) return false;

		return true;
	}

	public static ErrorMessagingResponse fromGraph(Graph graph) {

		if (! isValid(graph)) return(null);

		return new ErrorMessagingResponse(graph);
	}

	/*
	 * Overrides
	 */

	public Graph getGraph() {

		return this.graph;
	}

	@Override
	public Graph getResultGraph() {

		return null;
	}

	/*
	 * Instance methods
	 */

	public Date getErrorTimestamp() {

		return Timestamps.getContextNodeTimestamp(this.getErrorContextNode());
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

	/*
	 * Helper methods
	 */

	private ContextNode getErrorContextNode() {

		return this.getGraph().setDeepContextNode(XDI_ADD_FALSE);
	}

	private void setErrorTimestamp(Date errorTimestamp) {

		Timestamps.setContextNodeTimestamp(this.getErrorContextNode(), errorTimestamp);
	}

	private void setErrorString(String errorString) {

		XdiAttributeSingleton xdiAttributeSingleton = XdiAbstractContext.fromContextNode(this.getGraph().getRootContextNode(false)).getXdiAttributeSingleton(XdiAttributeSingleton.createAttributeSingletonXDIArc(XDI_ARC_FALSE), true);
		XdiValue xdiValue = xdiAttributeSingleton.getXdiValue(true);

		xdiValue.getContextNode().setLiteralString(errorString);
	}

	private void setErrorOperation(Operation errorOperation) {

		XdiInnerRoot xdiInnerRoot = XdiCommonRoot.findCommonRoot(this.getGraph()).getInnerRoot(XDI_ADD_FALSE, XDI_ADD_ERROR, true);
		xdiInnerRoot.getContextNode().clear();

		for (XDIStatement XDIstatement : new MappingXDIStatementIterator(errorOperation.getMessage().getContextNode().getAllStatements())) {

			xdiInnerRoot.getContextNode().setStatement(XDIstatement);
		}
	}
}
