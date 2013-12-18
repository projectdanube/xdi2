package xdi2.core.util;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Literal;
import xdi2.core.Relation;
import xdi2.core.features.nodetypes.XdiLocalRoot;
import xdi2.core.features.nodetypes.XdiPeerRoot;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3SubSegment;

/**
 * Various utility methods for working with context nodes, relations and literals.
 * 
 * @author markus
 */
public final class GraphUtil {

	private GraphUtil() { }

	public static XDI3SubSegment getOwnerPeerRootXri(Graph graph) {

		XdiPeerRoot xdiSelfPeerRoot = XdiLocalRoot.findLocalRoot(graph).getSelfPeerRoot();
		if (xdiSelfPeerRoot == null) return null;

		return xdiSelfPeerRoot.getArcXri();
	}

	public static XDI3Segment getOwnerXri(Graph graph) {

		XDI3SubSegment ownerPeerRootxri = getOwnerPeerRootXri(graph);
		if (ownerPeerRootxri == null) return null;

		return XdiPeerRoot.getXriOfPeerRootArcXri(ownerPeerRootxri);
	}

	public static void setOwnerPeerRootXri(Graph graph, XDI3SubSegment ownerPeerRootXri) {

		XDI3Segment ownerXri = XdiPeerRoot.getXriOfPeerRootArcXri(ownerPeerRootXri);

		setOwnerXri(graph, ownerXri);
	}

	public static void setOwnerXri(Graph graph, XDI3Segment ownerXri) {

		XdiLocalRoot.findLocalRoot(graph).setSelfPeerRoot(ownerXri);
	}

	/**
	 * Creates a context node from its components.
	 * @param contextNodeXri The XRI of the context node.
	 * @return A context node.
	 */
	public static ContextNode contextNodeFromComponents(XDI3Segment contextNodeXri) {

		Graph graph = MemoryGraphFactory.getInstance().openGraph();

		return graph.setDeepContextNode(contextNodeXri);
	}

	/**
	 * Creates a relation from its components.
	 * @param contextNodeXri The relation XRI of the context node containing the relation.
	 * @param arcXri The arc XRI of the relation.
	 * @param targetContextNodeXri The target context node XRI of the relation.
	 * @return A relation.
	 */
	public static Relation relationFromComponents(XDI3Segment contextNodeXri, XDI3Segment arcXri, XDI3Segment targetContextNodeXri) {

		Graph graph = MemoryGraphFactory.getInstance().openGraph();

		return graph.setDeepContextNode(contextNodeXri).setRelation(arcXri, targetContextNodeXri);
	}

	/**
	 * Creates a literal from its components.
	 * @param contextNodeXri The XRI of the context node containing the literal.
	 * @param literalData The literal data of the literal.
	 * @return A literal.
	 */
	public static Literal literalFromComponents(XDI3Segment contextNodeXri, Object literalData) {

		Graph graph = MemoryGraphFactory.getInstance().openGraph();

		return graph.setDeepContextNode(contextNodeXri).setLiteral(literalData);
	}
}
