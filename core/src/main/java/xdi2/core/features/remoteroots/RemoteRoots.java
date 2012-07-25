package xdi2.core.features.remoteroots;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.util.iterators.ReadOnlyIterator;
import xdi2.core.util.iterators.SelectingIterator;
import xdi2.core.xri3.impl.XRI3Reference;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.core.xri3.impl.XRI3SubSegment;
import xdi2.core.xri3.impl.XRI3XRef;

public class RemoteRoots {

	private RemoteRoots() { }

	/**
	 * Given a graph, finds and returns a remote root context node for an XRI.
	 * @param graph The graph.
	 * @param xri The XRI whose remote root context node to find.
	 * @param create Whether the remote root context node should be created, if it does not exist.
	 * @return The remote root context node.
	 */
	public static ContextNode findRemoteRootContextNode(Graph graph, XRI3Segment xri, boolean create) {

		return graph.findContextNode(getRemoteRootXri(xri), create);
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
				
				return isRemoteRootXri(contextNode.getXri());
			}
		};
	}
	
	/**
	 * Returns the remote root XRI of an XRI.
	 * @param xri An XRI.
	 * @return The remote root XRI of the XRI.
	 */
	public static XRI3Segment getRemoteRootXri(XRI3Segment xri) {

		return new XRI3Segment("(" + xri.toString() + ")");
	}

	/**
	 * Returns the XRI of the remote root XRI.
	 * @param xri A remote root XRI.
	 * @return The XRI of the remote root XRI.
	 */
	public static XRI3Segment getXriOfRemoteRootXri(XRI3Segment xri) {

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

		return getXriOfRemoteRootXri(xri) != null;
	}
}
