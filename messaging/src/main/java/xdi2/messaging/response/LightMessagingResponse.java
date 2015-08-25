package xdi2.messaging.response;

import xdi2.core.Graph;

/**
 * A graph as an XDI messaging response.
 * 
 * @author markus
 */
public class LightMessagingResponse extends TransportMessagingResponse implements MessagingResponse {

	private static final long serialVersionUID = -518357785421448783L;

	private Graph graph;

	private LightMessagingResponse(Graph graph) {

		this.graph = graph;
	}

	/*
	 * Static methods
	 */

	public static boolean isValid(Graph graph) {

		return true;
	}

	public static LightMessagingResponse fromGraph(Graph graph) {

		if (! isValid(graph)) return(null);

		return new LightMessagingResponse(graph);
	}

	public static LightMessagingResponse fromResultGraph(Graph resultGraph) {

		if (! isValid(resultGraph)) return(null);

		return new LightMessagingResponse(resultGraph);
	}

	/*
	 * Instance methods
	 */

	@Override
	public Graph getGraph() {

		return this.graph;
	}

	@Override
	public Graph getResultGraph() {

		return this.graph;
	}
}
