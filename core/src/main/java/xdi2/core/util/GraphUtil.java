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

	public static XDIArc getOwnerPeerRootXri(Graph graph) {

		XdiPeerRoot xdiSelfPeerRoot = XdiLocalRoot.findLocalRoot(graph).getSelfPeerRoot();
		if (xdiSelfPeerRoot == null) return null;

		return xdiSelfPeerRoot.getArc();
	}

	public static XDIAddress getOwnerXri(Graph graph) {

		XDIArc ownerPeerRootxri = getOwnerPeerRootXri(graph);
		if (ownerPeerRootxri == null) return null;

		return XdiPeerRoot.getAddressOfPeerRootArc(ownerPeerRootxri);
	}

	public static void setOwnerPeerRootXri(Graph graph, XDIArc ownerPeerRootXri) {

		XDIAddress ownerXri = XdiPeerRoot.getAddressOfPeerRootArc(ownerPeerRootXri);

		setOwnerXri(graph, ownerXri);
	}

	public static void setOwnerXri(Graph graph, XDIAddress ownerXri) {

		XdiLocalRoot.findLocalRoot(graph).setSelfPeerRoot(ownerXri);
	}

	/**
	 * Creates a context node from its components.
	 * @param contextNodeAddress The XRI of the context node.
	 * @return A context node.
	 */
	public static ContextNode contextNodeFromComponents(XDIAddress contextNodeAddress) {

		Graph graph = MemoryGraphFactory.getInstance().openGraph();

		return graph.setDeepContextNode(contextNodeAddress);
	}

	/**
	 * Creates a relation from its components.
	 * @param contextNodeAddress The relation XRI of the context node containing the relation.
	 * @param arc The arc of the relation.
	 * @param targetContextNodeAddress The target context node XRI of the relation.
	 * @return A relation.
	 */
	public static Relation relationFromComponents(XDIAddress contextNodeAddress, XDIAddress arc, XDIAddress targetContextNodeAddress) {

		Graph graph = MemoryGraphFactory.getInstance().openGraph();

		return graph.setDeepContextNode(contextNodeAddress).setRelation(arc, targetContextNodeAddress);
	}

	/**
	 * Creates a literal from its components.
	 * @param contextNodeAddress The XRI of the context node containing the literal.
	 * @param literalData The literal data of the literal.
	 * @return A literal.
	 */
	public static Literal literalFromComponents(XDIAddress contextNodeAddress, Object literalData) {

		Graph graph = MemoryGraphFactory.getInstance().openGraph();

		return graph.setDeepContextNode(contextNodeAddress).setLiteral(literalData);
	}
}
