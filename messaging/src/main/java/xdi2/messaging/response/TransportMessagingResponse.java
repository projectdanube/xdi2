package xdi2.messaging.response;

import java.io.Serializable;

import xdi2.core.Graph;
import xdi2.core.features.error.XdiError;
import xdi2.core.features.linkcontracts.LinkContracts;
import xdi2.core.features.linkcontracts.instance.GenericLinkContract;
import xdi2.core.features.linkcontracts.instance.LinkContract;
import xdi2.core.features.nodetypes.XdiCommonRoot;
import xdi2.core.util.iterators.EmptyIterator;
import xdi2.core.util.iterators.ReadOnlyIterator;
import xdi2.core.util.iterators.SelectingIterator;

public abstract class TransportMessagingResponse implements MessagingResponse, Serializable, Comparable<MessagingResponse> {

	/*
	 * Static methods
	 */

	private static final long serialVersionUID = 9000799040158882358L;

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
	 * Factory method that creates a messaging response bound to a given graph.
	 * @param graph The graph that is a messaging response.
	 * @return The messaging response.
	 */
	public static TransportMessagingResponse fromGraph(Graph graph) {

		if (graph == null) throw new NullPointerException();

		TransportMessagingResponse messagingResponse = null;

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

		Graph resultGraph = this.getResultGraph();
		if (resultGraph == null) return null;

		return XdiError.findXdiError(XdiCommonRoot.findCommonRoot(resultGraph), false);
	}

	/*
	 * Helper methods
	 */

	/*
	 * TODO: This should be completely removed. Instead of manually extracting link contracts
	 * of an incoming push, we should process pushed messaging results like other messaging results
	 * (e.g. of a $connect)
	 */
	public static ReadOnlyIterator<LinkContract> getLinkContracts(MessagingResponse messagingResponse) {
		
		if (! (messagingResponse instanceof TransportMessagingResponse)) return new EmptyIterator<LinkContract> ();

		Graph resultGraph = ((TransportMessagingResponse) messagingResponse).getResultGraph();
		if (resultGraph == null) return new EmptyIterator<LinkContract> ();

		return new SelectingIterator<LinkContract> (LinkContracts.getAllLinkContracts(resultGraph)) {

			@Override
			public boolean select(LinkContract linkContract) {

				if (! (linkContract instanceof GenericLinkContract)) return false;
				if (! (((GenericLinkContract) linkContract).getXdiInnerRoot().getXdiContext() instanceof XdiCommonRoot)) return false;

				return true;
			}
		};
	}
	
	/*
	 * Object methods
	 */

	@Override
	public String toString() {

		if (this.getGraph() == null) return super.toString();

		return String.valueOf(this.getGraph());
	}

	@Override
	public boolean equals(Object object) {

		if (object == null || ! (object instanceof MessagingResponse)) return false;
		if (object == this) return true;

		MessagingResponse other = (MessagingResponse) object;

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
