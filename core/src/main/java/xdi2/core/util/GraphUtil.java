package xdi2.core.util;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Literal;
import xdi2.core.Relation;
import xdi2.core.features.nodetypes.XdiLocalRoot;
import xdi2.core.features.nodetypes.XdiPeerRoot;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;

/**
 * Various utility methods for working with context nodes, relations and literals.
 * 
 * @author markus
 */
public final class GraphUtil {

	private GraphUtil() { }

	public static XDIArc getOwnerPeerRootAddress(Graph graph) {

		XdiPeerRoot xdiSelfPeerRoot = XdiLocalRoot.findLocalRoot(graph).getSelfPeerRoot();
		if (xdiSelfPeerRoot == null) return null;

		return xdiSelfPeerRoot.getArc();
	}

	public static XDIAddress getOwnerAddress(Graph graph) {

		XDIArc ownerPeerRootArc = getOwnerPeerRootAddress(graph);
		if (ownerPeerRootArc == null) return null;

		return XdiPeerRoot.getAddressOfPeerRootArc(ownerPeerRootArc);
	}

	public static void setOwnerPeerRootAddress(Graph graph, XDIArc ownerPeerRootAddress) {

		XDIAddress ownerAddress = XdiPeerRoot.getAddressOfPeerRootArc(ownerPeerRootAddress);

		setOwnerAddress(graph, ownerAddress);
	}

	public static void setOwnerAddress(Graph graph, XDIAddress ownerAddress) {

		XdiLocalRoot.findLocalRoot(graph).setSelfPeerRoot(ownerAddress);
	}

	/**
	 * Creates a context node from its components.
	 * @param contextNodeAddress The address of the context node.
	 * @return A context node.
	 */
	public static ContextNode contextNodeFromComponents(XDIAddress contextNodeAddress) {

		Graph graph = MemoryGraphFactory.getInstance().openGraph();

		return graph.setDeepContextNode(contextNodeAddress);
	}

	/**
	 * Creates a relation from its components.
	 * @param contextNodeAddress The address of the context node containing the relation.
	 * @param arc The arc of the relation.
	 * @param targetContextNodeAddress The target context node address of the relation.
	 * @return A relation.
	 */
	public static Relation relationFromComponents(XDIAddress contextNodeAddress, XDIAddress arc, XDIAddress targetContextNodeAddress) {

		Graph graph = MemoryGraphFactory.getInstance().openGraph();

		return graph.setDeepContextNode(contextNodeAddress).setRelation(arc, targetContextNodeAddress);
	}

	/**
	 * Creates a literal from its components.
	 * @param contextNodeAddress The address of the context node containing the literal.
	 * @param literalData The literal data of the literal.
	 * @return A literal.
	 */
	public static Literal literalFromComponents(XDIAddress contextNodeAddress, Object literalData) {

		Graph graph = MemoryGraphFactory.getInstance().openGraph();

		return graph.setDeepContextNode(contextNodeAddress).setLiteral(literalData);
	}
}
