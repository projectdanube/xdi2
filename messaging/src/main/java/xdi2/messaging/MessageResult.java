package xdi2.messaging;

import java.io.Serializable;

import xdi2.core.Graph;
import xdi2.core.impl.memory.MemoryGraphFactory;

/**
 * An XDI message result, represented as a graph.
 * 
 * @author markus
 */
public class MessageResult implements Serializable, Comparable<MessageResult> {

	private static final long serialVersionUID = -518357785421448783L;

	private static final MemoryGraphFactory graphFactory = MemoryGraphFactory.getInstance();

	private Graph graph;

	protected MessageResult(Graph graph) {

		this.graph = graph;
	}

	public MessageResult() {

		this(graphFactory.openGraph());
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a graph is a valid XDI message result.
	 * @param graph The graph to check.
	 * @return True if the graph is a valid XDI message result.
	 */
	public static boolean isValid(Graph graph) {

		return true;
	}

	/**
	 * Factory method that creates an XDI message result bound to a given graph.
	 * @param graph The graph that is an XDI message result.
	 * @return The XDI message result.
	 */
	public static MessageResult fromGraph(Graph graph) {

		if (! isValid(graph)) return(null);

		return new MessageResult(graph);
	}

	/*
	 * Instance methods
	 */
	
	/**
	 * Returns the underlying graph to which this XDI message result is bound.
	 * @return A graph that contains XDI messaging data.
	 */
	public Graph getGraph() {

		return this.graph;
	}

	/** 
	 * Check if the message result is empty.
	 * @return True, if the underlying graph contains no context nodes.
	 */
	public boolean isEmpty() {

		return this.graph.getRootContextNode().isEmpty();
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

		if (object == null || ! (object instanceof MessageResult)) return false;
		if (object == this) return true;
		
		MessageResult other = (MessageResult) object;

		return this.getGraph().equals(other.getGraph());
	}

	@Override
	public int hashCode() {

		int hashCode = 1;

		hashCode = (hashCode * 31) + this.getGraph().hashCode();

		return hashCode;
	}

	@Override
	public int compareTo(MessageResult other) {

		if (other == this || other == null) return(0);

		return this.getGraph().compareTo(other.getGraph());
	}
}
