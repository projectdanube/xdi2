package xdi2.core.features.remoteroots;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Relation;
import xdi2.core.constants.XDIDictionaryConstants;
import xdi2.core.util.iterators.ReadOnlyIterator;
import xdi2.core.util.iterators.SelectingIterator;
import xdi2.core.xri3.impl.XRI3Reference;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.core.xri3.impl.XRI3SubSegment;
import xdi2.core.xri3.impl.XRI3XRef;

public class RemoteRoots {

	private RemoteRoots() { }

	/*
	 * Methods for remote root context nodes.
	 */

	/**
	 * Given a graph, finds and returns a remote root context node for an XRI.
	 * @param graph The graph.
	 * @param xri The XRI whose remote root context node to find.
	 * @param create Whether the remote root context node should be created, if it does not exist.
	 * @return The remote root context node.
	 */
	public static ContextNode findRemoteRootContextNode(Graph graph, XRI3Segment xri, boolean create) {

		return graph.findContextNode(remoteRootXri(xri), create);
	}

	/**
	 * Given a graph, lists all remote root context nodes.
	 * TODO: This is inefficient because it enumerates all context nodes in the graph.
	 * @param graph The graph.
	 * @return An iterator over remote root context nodes.
	 */
	public static ReadOnlyIterator<ContextNode> getAllRemoteRootContextNodes(Graph graph) {

		return new SelectingIterator<ContextNode> (graph.getRootContextNode().getAllContextNodes()) {

			@Override
			public boolean select(ContextNode contextNode) {

				return isRemoteRootContextNode(contextNode);
			}
		};
	}

	/**
	 * Checks if a given context node is a remote root context node.
	 * @param contextNode A context node.
	 * @return True, if the context node is a remote root context node.
	 */
	public static boolean isRemoteRootContextNode(ContextNode contextNode) {

		return isRemoteRootXri(contextNode.getXri());
	}

	/*
	 * Methods for the self remote root context node.
	 */

	public static ContextNode setSelfRemoteRootContextNode(Graph graph, XRI3Segment xri) {

		ContextNode rootContextNode = graph.getRootContextNode();

		rootContextNode.deleteRelations(XDIDictionaryConstants.XRI_S_IS_IS);

		if (xri == null) return null;

		ContextNode remoteRootContextNode = findRemoteRootContextNode(graph, xri, true);

		rootContextNode.createRelation(XDIDictionaryConstants.XRI_S_IS_IS, remoteRootContextNode);
		remoteRootContextNode.createRelation(XDIDictionaryConstants.XRI_S_IS, rootContextNode);

		return remoteRootContextNode;
	}

	public static ContextNode getSelfRemoteRootContextNode(Graph graph) {

		Relation relation = graph.getRootContextNode().getRelation(XDIDictionaryConstants.XRI_S_IS_IS);
		if (relation == null) return null;

		return relation.follow();
	}

	public static boolean isSelfRemoteRootContextNode(ContextNode remoteRootContextNode) {

		ContextNode selfRemoteRootContextNode = getSelfRemoteRootContextNode(remoteRootContextNode.getGraph());

		return remoteRootContextNode.equals(selfRemoteRootContextNode);
	}

	/*
	 * Methods for remote root XRIs.
	 */

	/**
	 * Returns the remote root XRI of an XRI.
	 * @param xri An XRI.
	 * @return The remote root XRI of the XRI.
	 */
	public static XRI3Segment remoteRootXri(XRI3Segment xri) {

		return new XRI3Segment("(" + xri.toString() + ")");
	}

	/**
	 * Returns the XRI of the remote root XRI.
	 * @param xri A remote root XRI.
	 * @return The XRI of the remote root XRI.
	 */
	public static XRI3Segment xriOfRemoteRootXri(XRI3Segment xri) {

		if (xri.getNumSubSegments() != 1) return null;

		XRI3SubSegment xriSubSegment = (XRI3SubSegment) xri.getFirstSubSegment();
		if (! xriSubSegment.hasXRef()) return null;

		XRI3XRef xriXref = (XRI3XRef) xriSubSegment.getXRef();
		if (! xriXref.hasXRIReference()) return null;

		XRI3Reference xriReference = (XRI3Reference) xriXref.getXRIReference();

		return new XRI3Segment(xriReference.toString());
	}

	/**
	 * Checks if a given XRI is a remote root XRI.
	 * @param xri An remote root XRI.
	 * @return True, if the XRI is a remote root XRI.
	 */
	public static boolean isRemoteRootXri(XRI3Segment xri) {

		return xriOfRemoteRootXri(xri) != null;
	}
}
