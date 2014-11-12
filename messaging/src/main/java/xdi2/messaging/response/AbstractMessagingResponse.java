package xdi2.messaging.response;

import xdi2.core.Graph;

public abstract class AbstractMessagingResponse implements MessagingResponse {
	
	private static final long serialVersionUID = -6307483543411797001L;

	/*
	 * Static methods
	 */

	/**
	 * Checks if a graph is a valid messaging response.
	 * @param graph The graph to check.
	 * @return True if the graph is a valid messaging response.
	 */
	public static boolean isValid(Graph graph) {

		if (graph == null) throw new NullPointerException();

		if (ErrorMessagingResponse.isValid(graph)) return true; 
		if (MessageEnvelopeMessagingResponse.isValid(graph)) return true;
		if (ResultGraphMessagingResponse.isValid(graph)) return true;

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
		if ((messagingResponse = MessageEnvelopeMessagingResponse.fromGraph(graph)) != null) return messagingResponse;
		if ((messagingResponse = ResultGraphMessagingResponse.fromGraph(graph)) != null) return messagingResponse;

		return null;
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

		if (object == null || ! (object instanceof MessageEnvelopeMessagingResponse)) return false;
		if (object == this) return true;

		MessageEnvelopeMessagingResponse other = (MessageEnvelopeMessagingResponse) object;

		return this.getGraph().equals(other.getGraph());
	}

	@Override
	public int hashCode() {

		int hashCode = 1;

		hashCode = (hashCode * 31) + this.getGraph().hashCode();

		return hashCode;
	}

	@Override
	public int compareTo(MessagingResponse other) {

		if (other == this || other == null) return(0);

		return this.getGraph().compareTo(other.getGraph());
	}
}
