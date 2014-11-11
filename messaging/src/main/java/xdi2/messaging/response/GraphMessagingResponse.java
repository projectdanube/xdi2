package xdi2.messaging.response;

import java.io.Serializable;
import java.util.Iterator;

import xdi2.core.Graph;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.util.iterators.SingleItemIterator;

/**
 * A graph as an XDI messaging response.
 * 
 * @author markus
 */
public class GraphMessagingResponse implements MessagingResponse, Serializable, Comparable<GraphMessagingResponse> {

	private static final long serialVersionUID = -518357785421448783L;

	private Graph graph;

	protected GraphMessagingResponse(Graph graph) {

		this.graph = graph;
	}

	public GraphMessagingResponse() {

		this(MemoryGraphFactory.getInstance().openGraph());
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a graph is a valid graph messaging response.
	 * @param graph The graph to check.
	 * @return True if the graph is a valid graph messaging response.
	 */
	public static boolean isValid(Graph graph) {

		return true;
	}

	/**
	 * Factory method that creates an graph messaging response bound to a given graph.
	 * @param graph The graph that is an graph messaging response.
	 * @return The graph messaging response.
	 */
	public static GraphMessagingResponse fromGraph(Graph graph) {

		if (! isValid(graph)) return(null);

		return new GraphMessagingResponse(graph);
	}

	/*
	 * Instance methods
	 */

	/**
	 * Returns the underlying graph to which this graph messaging response is bound.
	 * @return The underlying graph.
	 */
	public Graph getGraph() {

		return this.graph;
	}

	@Override
	public Iterator<Graph> getResultGraphs() {

		return new SingleItemIterator<Graph> (this.getResultGraph());
	}

	@Override
	public Graph getResultGraph() {

		return this.getGraph();
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

		if (object == null || ! (object instanceof GraphMessagingResponse)) return false;
		if (object == this) return true;

		GraphMessagingResponse other = (GraphMessagingResponse) object;

		return this.getGraph().equals(other.getGraph());
	}

	@Override
	public int hashCode() {

		int hashCode = 1;

		hashCode = (hashCode * 31) + this.getGraph().hashCode();

		return hashCode;
	}

	@Override
	public int compareTo(GraphMessagingResponse other) {

		if (other == this || other == null) return(0);

		return this.getGraph().compareTo(other.getGraph());
	}
}
