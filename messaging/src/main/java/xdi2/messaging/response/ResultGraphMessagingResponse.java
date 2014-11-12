package xdi2.messaging.response;

import java.util.Iterator;

import xdi2.core.Graph;
import xdi2.core.util.iterators.SingleItemIterator;

/**
 * A graph as an XDI messaging response.
 * 
 * @author markus
 */
public class ResultGraphMessagingResponse extends AbstractMessagingResponse implements MessagingResponse {

	private static final long serialVersionUID = -518357785421448783L;

	private Graph resultGraph;

	protected ResultGraphMessagingResponse(Graph resultGraph) {

		this.resultGraph = resultGraph;
	}

	/*
	 * Static methods
	 */

	public static boolean isValid(Graph graph) {

		return true;
	}

	public static ResultGraphMessagingResponse fromResultGraph(Graph resultGraph) {

		if (! isValid(resultGraph)) return(null);

		return new ResultGraphMessagingResponse(resultGraph);
	}

	/*
	 * Instance methods
	 */

	public Graph getGraph() {

		return this.resultGraph;
	}

	@Override
	public Iterator<Graph> getResultGraphs() {

		return new SingleItemIterator<Graph> (this.resultGraph);
	}

	@Override
	public Graph getResultGraph() {

		return this.resultGraph;
	}
}
