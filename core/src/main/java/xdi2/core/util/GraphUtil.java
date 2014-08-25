package xdi2.core.util;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Literal;
import xdi2.core.Relation;
import xdi2.core.features.nodetypes.XdiCommonRoot;
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

	public static XDIArc getOwnerPeerRootXDIArc(Graph graph) {

		XdiPeerRoot xdiSelfPeerRoot = XdiCommonRoot.findCommonRoot(graph).getSelfPeerRoot();
		if (xdiSelfPeerRoot == null) return null;

		return xdiSelfPeerRoot.getXDIArc();
	}

	public static XDIAddress getOwnerXDIAddress(Graph graph) {

		XDIArc ownerPeerRootXDIarc = getOwnerPeerRootXDIArc(graph);
		if (ownerPeerRootXDIarc == null) return null;

		return XdiPeerRoot.getXDIAddressOfPeerRootXDIArc(ownerPeerRootXDIarc);
	}

	public static void setOwnerPeerRootXDIArc(Graph graph, XDIArc ownerPeerRootXDIarc) {

		XDIAddress ownerAddress = XdiPeerRoot.getXDIAddressOfPeerRootXDIArc(ownerPeerRootXDIarc);

		setOwnerXDIAddress(graph, ownerAddress);
	}

	public static void setOwnerXDIAddress(Graph graph, XDIAddress ownerAddress) {

		XdiCommonRoot.findCommonRoot(graph).setSelfPeerRoot(ownerAddress);
	}

	/**
	 * Creates a context node from its components.
	 * @param contextNodeXDIAddress The address of the context node.
	 * @return A context node.
	 */
	public static ContextNode contextNodeFromComponents(XDIAddress contextNodeXDIAddress) {

		Graph graph = MemoryGraphFactory.getInstance().openGraph();

		return graph.setDeepContextNode(contextNodeXDIAddress);
	}

	/**
	 * Creates a relation from its components.
	 * @param contextNodeXDIAddress The address of the context node containing the relation.
	 * @param XDIaddress The address of the relation.
	 * @param targetContextNodeXDIAddress The target context node address of the relation.
	 * @return A relation.
	 */
	public static Relation relationFromComponents(XDIAddress contextNodeXDIAddress, XDIAddress XDIaddress, XDIAddress targetContextNodeXDIAddress) {

		Graph graph = MemoryGraphFactory.getInstance().openGraph();

		return graph.setDeepContextNode(contextNodeXDIAddress).setRelation(XDIaddress, targetContextNodeXDIAddress);
	}

	/**
	 * Creates a literal from its components.
	 * @param contextNodeXDIAddress The address of the context node containing the literal.
	 * @param literalData The literal data of the literal.
	 * @return A literal.
	 */
	public static Literal literalFromComponents(XDIAddress contextNodeXDIAddress, Object literalData) {

		Graph graph = MemoryGraphFactory.getInstance().openGraph();

		return graph.setDeepContextNode(contextNodeXDIAddress).setLiteral(literalData);
	}
}
