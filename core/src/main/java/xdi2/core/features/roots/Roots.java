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

	/**
	 * Checks if a context node is a valid XDI root.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI root.
	 */
	public static boolean isValid(ContextNode contextNode) {

		return
				LocalRoot.isValid(contextNode) ||
				PeerRoot.isValid(contextNode) ||
				InnerRoot.isValid(contextNode);
	}

	/**
	 * Factory method that creates an XDI root bound to a given context node.
	 * @param contextNode The context node that is an XDI root.
	 * @return The XDI root.
	 */
	public static Root fromContextNode(ContextNode contextNode) {

		if (LocalRoot.isValid(contextNode)) return new LocalRoot(contextNode);
		if (PeerRoot.isValid(contextNode)) return new PeerRoot(contextNode);
		if (InnerRoot.isValid(contextNode)) return new InnerRoot(contextNode);

		return null;
	}
}
