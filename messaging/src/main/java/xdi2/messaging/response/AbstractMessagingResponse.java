package xdi2.messaging.response;

import xdi2.core.Graph;

public class AbstractMessagingResponse {

	/**
	 * Checks if a graph is a valid messaging response.
	 * @param graph The graph to check.
	 * @return True if the graph is a valid messaging response.
	 */
	public static boolean isValid(Graph graph) {

		if (graph == null) throw new NullPointerException();

		if (ErrorMessagingResponse.isValid(graph)) return true; 
		if (ResponseMessageEnvelope.isValid(graph)) return true;
		if (GraphMessagingResponse.isValid(graph)) return true;

		return false;
	}

	/**
	 * Factory method that creates a message response bound to a given graph.
	 * @param contextNode The graph that is a message response.
	 * @return The message response.
	 */
	public static MessagingResponse fromGraph(Graph graph) {

		if (graph == null) throw new NullPointerException();

		MessagingResponse messagingResponse = null;

		if ((messagingResponse = ErrorMessagingResponse.fromGraph(graph)) != null) return messagingResponse;
		if ((messagingResponse = ResponseMessageEnvelope.fromGraph(graph)) != null) return messagingResponse;
		if ((messagingResponse = GraphMessagingResponse.fromGraph(graph)) != null) return messagingResponse;

		return null;
	}
}
