package xdi2.messaging.error;

import xdi2.ContextNode;
import xdi2.Graph;
import xdi2.messaging.MessageResult;
import xdi2.xri3.impl.XRI3Segment;

public class ErrorMessageResult extends MessageResult {

	private static final long serialVersionUID = 8816468280233966339L;

	public static final XRI3Segment XRI_S_ERRORCODE = new XRI3Segment("$false$integer");
	public static final XRI3Segment XRI_S_ERRORSTRING = new XRI3Segment("$false$string");

	public static final Integer DEFAULT_ERRORCODE = Integer.valueOf(-1);
	public static final String DEFAULT_ERRORSTRING = "XDI error.";

	protected ErrorMessageResult(Graph graph) {

		super(graph);
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
	 * Factory method that creates an XDI error message result bound to a new in-memory graph.
	 * @return The XDI message result.
	 */
	public static ErrorMessageResult newInstance() {

		Graph graph = graphFactory.openGraph();
		
		graph.findContextNode(XRI_S_ERRORCODE, true).createLiteral(DEFAULT_ERRORCODE.toString());
		graph.findContextNode(XRI_S_ERRORSTRING, true).createLiteral(DEFAULT_ERRORSTRING);
		
		return new ErrorMessageResult(graph);
	}

	/*
	 * Instance methods
	 */

	public Integer getErrorCode() {

		ContextNode contextNode = this.graph.findContextNode(XRI_S_ERRORCODE, false);
		if (contextNode == null || ! contextNode.containsLiteral()) return null;

		return new Integer(contextNode.getLiteral().getLiteralData());
	}

	public void setErrorCode(Integer errorCode) {

		ContextNode contextNode = this.graph.findContextNode(XRI_S_ERRORCODE, true);

		if (contextNode.containsLiteral()) {

			contextNode.getLiteral().setLiteralData(errorCode.toString());
		} else {

			contextNode.createLiteral(errorCode.toString());
		}
	}

	public String getErrorString() {

		ContextNode errorCodeContextNode = this.graph.findContextNode(XRI_S_ERRORCODE, false);
		if (errorCodeContextNode == null || ! errorCodeContextNode.containsLiteral()) return null;

		return errorCodeContextNode.getLiteral().getLiteralData();
	}

	public void setErrorString(String errorString) {

		ContextNode contextNode = this.graph.findContextNode(XRI_S_ERRORCODE, true);

		if (contextNode.containsLiteral()) {

			contextNode.getLiteral().setLiteralData(errorString);
		} else {

			contextNode.createLiteral(errorString);
		}
	}
}
