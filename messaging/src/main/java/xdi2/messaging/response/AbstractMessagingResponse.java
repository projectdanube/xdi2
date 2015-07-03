package xdi2.messaging.response;

import xdi2.core.Graph;
import xdi2.core.features.error.XdiError;
import xdi2.core.features.nodetypes.XdiCommonRoot;

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

		if (FullMessagingResponse.isValid(graph)) return true;
		if (LightMessagingResponse.isValid(graph)) return true;

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

		if ((messagingResponse = FullMessagingResponse.fromGraph(graph)) != null) return messagingResponse;
		if ((messagingResponse = LightMessagingResponse.fromGraph(graph)) != null) return messagingResponse;

		return null;
	}

	/*
	 * Instance methods
	 */

	@Override
	public boolean hasXdiError() {

		return this.getXdiError() != null;
	}

	@Override
	public XdiError getXdiError() {

		return XdiError.findXdiError(XdiCommonRoot.findCommonRoot(this.getResultGraph()), false);
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

		if (object == null || ! (object instanceof FullMessagingResponse)) return false;
		if (object == this) return true;

		FullMessagingResponse other = (FullMessagingResponse) object;

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
