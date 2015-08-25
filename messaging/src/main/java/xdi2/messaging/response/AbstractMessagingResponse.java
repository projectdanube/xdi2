package xdi2.messaging.response;

import java.util.Iterator;

import xdi2.core.Graph;
import xdi2.core.features.error.XdiError;
import xdi2.core.features.linkcontracts.LinkContracts;
import xdi2.core.features.linkcontracts.instance.LinkContract;
import xdi2.core.features.nodetypes.XdiCommonRoot;
import xdi2.core.util.iterators.EmptyIterator;

public abstract class AbstractMessagingResponse implements MessagingResponse {

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
	 * Factory method that creates a messaging response bound to a given graph.
	 * @param graph The graph that is a messaging response.
	 * @return The messaging response.
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

		Graph resultGraph = this.getResultGraph();
		if (resultGraph == null) return null;

		return XdiError.findXdiError(XdiCommonRoot.findCommonRoot(resultGraph), false);
	}

	@Override
	public boolean hasPushLinkContracts() {

		return this.getPushLinkContracts().hasNext();
	}

	@Override
	public Iterator<LinkContract> getPushLinkContracts() {

		Graph resultGraph = this.getResultGraph();
		if (resultGraph == null) return new EmptyIterator<LinkContract> ();

		// TODO: fix this, not all link contracts are push link contracts
		return LinkContracts.getAllLinkContracts(resultGraph);
	}
}
