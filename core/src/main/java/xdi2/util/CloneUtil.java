package xdi2.util;

import xdi2.ContextNode;
import xdi2.Graph;
import xdi2.impl.memory.MemoryGraphFactory;

/**
 * Various utility methods for cloning graph components.
 * 
 * @author markus
 */
public final class CloneUtil {

	protected static final MemoryGraphFactory graphFactory = MemoryGraphFactory.getInstance();

	private CloneUtil() { }

	/**
	 * Creates a copy of the given graph containing the same contents.
	 * @param graph The graph to clone.
	 * @return The cloned graph.
	 */
	public static Graph cloneGraph(Graph graph) {

		Graph newGraph = graphFactory.openGraph();
		CopyUtil.copyContextNode(graph.getRootContextNode(), newGraph, null);

		return newGraph;
	}

	/**
	 * Creates a copy of the given context node containing the same contents.
	 * @param context node The context node to clone.
	 * @return The cloned context node.
	 */
	public static ContextNode cloneContextNode(ContextNode contextNode) {

		Graph newGraph = graphFactory.openGraph();
		ContextNode newContextNode = CopyUtil.copyContextNode(contextNode, newGraph, null);

		return newContextNode;
	}
}
