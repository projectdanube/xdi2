package xdi2.core.util;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.LiteralNode;
import xdi2.core.Relation;
import xdi2.core.features.equivalence.Equivalence;
import xdi2.core.features.nodetypes.XdiAbstractContext;
import xdi2.core.features.nodetypes.XdiCommonRoot;
import xdi2.core.features.nodetypes.XdiInnerRoot;
import xdi2.core.features.nodetypes.XdiPeerRoot;
import xdi2.core.features.nodetypes.XdiRoot;
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

		XdiPeerRoot selfPeerRoot = XdiCommonRoot.findCommonRoot(graph).getSelfPeerRoot();
		if (selfPeerRoot == null) return null;

		return selfPeerRoot.getXDIArc();
	}

	public static XDIAddress getOwnerXDIAddress(Graph graph) {

		XDIArc ownerPeerRootXDIArc = getOwnerPeerRootXDIArc(graph);
		if (ownerPeerRootXDIArc == null) return null;

		return XdiPeerRoot.getXDIAddressOfPeerRootXDIArc(ownerPeerRootXDIArc);
	}

	public static void setOwnerPeerRootXDIArc(Graph graph, XDIArc ownerPeerRootXDIArc) {

		XDIAddress ownerXDIAddress = XdiPeerRoot.getXDIAddressOfPeerRootXDIArc(ownerPeerRootXDIArc);

		setOwnerXDIAddress(graph, ownerXDIAddress);
	}

	public static void setOwnerXDIAddress(Graph graph, XDIAddress ownerXDIAddress) {

		XdiCommonRoot.findCommonRoot(graph).setSelfPeerRoot(ownerXDIAddress);
	}

	public static boolean ownsPeerRootXDIArc(Graph graph, XDIArc peerRootXDIArc) {

		XdiPeerRoot xdiPeerRoot = XdiCommonRoot.findCommonRoot(graph).getPeerRoot(peerRootXDIArc, false);
		if (xdiPeerRoot == null) return false;

		XdiRoot xdiRoot = xdiPeerRoot.dereference();

		return xdiRoot instanceof XdiCommonRoot;
	}

	public static ContextNode dereference(ContextNode contextNode, boolean subgraph, boolean reference, boolean replacement, boolean identity) {

		ContextNode dereferencedContextNode = contextNode;

		while (true) {

			if (reference) {

				ContextNode referenceContextNode = Equivalence.getReferenceContextNode(dereferencedContextNode);
				if (referenceContextNode != null) { dereferencedContextNode = referenceContextNode; continue; }
			}

			if (replacement) {

				ContextNode replacementContextNode = Equivalence.getReplacementContextNode(dereferencedContextNode);
				if (replacementContextNode != null) { dereferencedContextNode = replacementContextNode; continue; }
			}

			if (identity) {

				ContextNode identityContextNode = Equivalence.getIdentityContextNode(dereferencedContextNode);
				if (identityContextNode != null) { dereferencedContextNode = identityContextNode; continue; }
			}

			break;
		}

		return dereferencedContextNode;
	}

	public static ContextNode dereference(ContextNode contextNode, boolean subgraph) {

		return dereference(contextNode, subgraph, true, true, false);
	}

	public static ContextNode dereference(ContextNode contextNode, XDIAddress XDIaddress, boolean subgraph, boolean reference, boolean replacement, boolean identity) {

		for (XDIArc XDIarc : XDIaddress.getXDIArcs()) {

			if (XdiInnerRoot.isValidXDIArc(XDIarc)) {

				XDIAddress subject = XdiInnerRoot.getSubjectOfInnerRootXDIArc(XDIarc);
				XDIAddress predicate = XdiInnerRoot.getPredicateOfInnerRootXDIArc(XDIarc);

				ContextNode subjectContextNode = dereference(contextNode, subject, subgraph, reference, replacement, identity);
				XdiInnerRoot xdiInnerRoot = XdiAbstractContext.fromContextNode(subjectContextNode).getXdiInnerRoot(predicate, false);
				if (xdiInnerRoot == null) return null;

				contextNode = xdiInnerRoot.getContextNode();
			} else {

				contextNode = contextNode.getContextNode(XDIarc);
				if (contextNode == null) return null;
			}

			contextNode = dereference(contextNode, subgraph, reference, replacement, identity);
		}

		return contextNode;
	}

	public static ContextNode dereference(ContextNode contextNode, XDIAddress XDIaddress, boolean subgraph) {

		return dereference(contextNode, XDIaddress, subgraph, true, true, false);
	}

	public static ContextNode dereference(Graph graph, XDIAddress XDIaddress, boolean subgraph, boolean reference, boolean replacement, boolean identity) {

		return dereference(graph.getRootContextNode(), XDIaddress, subgraph, reference, replacement, identity);
	}

	public static ContextNode dereference(Graph graph, XDIAddress XDIaddress, boolean subgraph) {

		return dereference(graph, XDIaddress, subgraph, true, true, false);
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
	 * @param targetXDIAddress The target context node address of the relation.
	 * @return A relation.
	 */
	public static Relation relationFromComponents(XDIAddress contextNodeXDIAddress, XDIAddress XDIaddress, XDIAddress targetXDIAddress) {

		Graph graph = MemoryGraphFactory.getInstance().openGraph();

		return graph.setDeepContextNode(contextNodeXDIAddress).setRelation(XDIaddress, targetXDIAddress);
	}

	/**
	 * Creates a literal from its components.
	 * @param contextNodeXDIAddress The address of the context node containing the literal.
	 * @param literalData The literal data of the literal.
	 * @return A literal.
	 */
	public static LiteralNode literalFromComponents(XDIAddress contextNodeXDIAddress, Object literalData) {

		Graph graph = MemoryGraphFactory.getInstance().openGraph();

		return graph.setDeepContextNode(contextNodeXDIAddress).setLiteralNode(literalData);
	}

	/**
	 * Creates a relative graph in which a given context node is the root context node.
	 * @param contextNode A context node in a graph.
	 * @return The relative graph.
	 */
	public static Graph relativeGraph(ContextNode contextNode) {

		Graph relativeGraph = MemoryGraphFactory.getInstance().openGraph();
		CopyUtil.copyContextNodeContents(contextNode, relativeGraph, null);

		return relativeGraph;
	}
}
