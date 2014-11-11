package xdi2.messaging.response;

import java.util.Iterator;

import xdi2.core.Graph;
import xdi2.messaging.MessageEnvelope;

public class ResponseMessageEnvelope extends MessageEnvelope<ResponseMessageEnvelope, ResponseMessageCollection, ResponseMessage> implements MessagingResponse {

	private static final long serialVersionUID = 7100877607713812125L;

	protected ResponseMessageEnvelope(Graph graph) {

		super(graph, ResponseMessageEnvelope.class, ResponseMessageCollection.class, ResponseMessage.class);
	}

	public ResponseMessageEnvelope() {

		super(ResponseMessageEnvelope.class, ResponseMessageCollection.class, ResponseMessage.class);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a graph is a valid XDI message envelope.
	 * @param graph The graph to check.
	 * @return True if the graph is a valid XDI message envelope.
	 */
	public static boolean isValid(Graph graph) {

		return MessageEnvelope.isValid(graph);
	}

	/**
	 * Factory method that creates an XDI message envelope bound to a given graph.
	 * @param graph The graph that is an XDI message envelope.
	 * @return The XDI message envelope.
	 */
	public static ResponseMessageEnvelope fromGraph(Graph graph) {

		if (! isValid(graph)) return null;

		return new ResponseMessageEnvelope(graph);
	}

	/*
	 * Instance methods
	 */

	@Override
	public Iterator<Graph> getResultGraphs() {

		return null;
	}

	@Override
	public Graph getResultGraph() {

		return null;
	}
}
