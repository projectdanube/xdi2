package xdi2.core.util;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.impl.memory.MemoryGraphFactory;

/**
 * Various utility methods for cloning graph components.
 * 
 * @author markus
 */
public final class CloneUtil {

	private static final MemoryGraphFactory graphFactory = MemoryGraphFactory.getInstance();

	private CloneUtil() { }

	/**
	 * Creates a clone of the given graph with the same contents.
	 * @param graph The graph to clone.
	 * @return The cloned graph.
	 */
	public static Graph cloneGraph(Graph graph) {

		Graph newGraph = graphFactory.openGraph();
		CopyUtil.copyContextNode(graph.getRootContextNode(), newGraph, null);

		return newGraph;
	}

	/**
	 * Creates a clone of the given context node with the same contents.
	 * @param contextNode The context node to clone.
	 * @return The cloned context node.
	 */
	public static ContextNode cloneContextNode(ContextNode contextNode) {

		Graph newGraph = graphFactory.openGraph();
		ContextNode newContextNode = CopyUtil.copyContextNode(contextNode, newGraph, null);

		return newContextNode;
	}
}
