package xdi2.messaging.response;

import xdi2.core.Graph;

/**
 * A graph as an XDI messaging response.
 * 
 * @author markus
 */
public class ResultGraphMessagingResponse extends AbstractMessagingResponse implements MessagingResponse {

	private static final long serialVersionUID = -518357785421448783L;

	private Graph resultGraph;

	private ResultGraphMessagingResponse(Graph resultGraph) {

		this.resultGraph = resultGraph;
	}

	/*
	 * Static methods
	 */

	public static ResultGraphMessagingResponse create(Graph resultGraph) {

		ResultGraphMessagingResponse resultGraphMessagingResponse = new ResultGraphMessagingResponse(resultGraph);

		return resultGraphMessagingResponse;
	}

	public static boolean isValid(Graph resultGraph) {

		return true;
	}

	public static ResultGraphMessagingResponse fromGraph(Graph graph) {

		if (! isValid(graph)) return(null);

		return new ResultGraphMessagingResponse(graph);
	}

	/*
	 * Instance methods
	 */

	@Override
	public Graph getGraph() {

		return this.resultGraph;
	}

	@Override
	public Graph getResultGraph() {

		return this.resultGraph;
	}
}
