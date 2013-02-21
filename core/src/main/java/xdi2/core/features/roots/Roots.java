package xdi2.core.features.roots;

import xdi2.core.ContextNode;
import xdi2.core.Graph;

public class Roots {

	private Roots() { }

	/*
	 * Methods for XDI roots.
	 */

	/**
	 * Given a graph, finds and returns the XDI local root.
	 * @param graph The graph.
	 * @return The XDI local root.
	 */
	public static LocalRoot findLocalRoot(Graph graph) {

		ContextNode localRootContextNode = graph.getRootContextNode();

		return new LocalRoot(localRootContextNode);
	}
}
