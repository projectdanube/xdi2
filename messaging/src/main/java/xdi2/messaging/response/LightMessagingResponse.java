package xdi2.messaging.response;

import xdi2.core.Graph;

/**
 * A graph as an XDI messaging response.
 * 
 * @author markus
 */
public class LightMessagingResponse extends AbstractMessagingResponse implements MessagingResponse {

	private static final long serialVersionUID = -518357785421448783L;

	private Graph resultGraph;

	private LightMessagingResponse(Graph resultGraph) {

		this.resultGraph = resultGraph;
	}

	/*
	 * Static methods
	 */

	public static boolean isValid(Graph resultGraph) {

		return true;
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

		return this.resultGraph;
	}

	@Override
	public Graph getResultGraph() {

		return this.resultGraph;
	}
}
