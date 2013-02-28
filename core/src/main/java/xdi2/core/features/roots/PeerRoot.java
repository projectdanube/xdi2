package xdi2.core.features.roots;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3SubSegment;
import xdi2.core.xri3.XDI3XRef;

/**
 * An XDI peer root, represented as a context node.
 * 
 * @author markus
 */
public final class PeerRoot extends AbstractRoot {

	private static final long serialVersionUID = -4689596452249483618L;

	protected PeerRoot(ContextNode contextNode) {

		super(contextNode);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI peer root.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI peer root.
	 */
	public static boolean isValid(ContextNode contextNode) {

		return isPeerRootXri(contextNode.getArcXri());
	}

	/**
	 * Factory method that creates an XDI peer root bound to a given context node.
	 * @param contextNode The context node that is an XDI peer root.
	 * @return The XDI peer root.
	 */
	public static PeerRoot fromContextNode(ContextNode contextNode) {

		if (! isValid(contextNode)) return null;

		return new PeerRoot(contextNode);
	}

	/*
	 * Instance methods
	 */

	/**
	 * Checks if this XDI peer root is the self XDI peer root of the graph.
	 * @return True, if this is the self XDI peer root.
	 */
	public boolean isSelfPeerRoot() {

		PeerRoot selfPeerRoot = this.findLocalRoot().getSelfPeerRoot();

		return this.equals(selfPeerRoot);
	}

	public XDI3Segment getXriOfPeerRoot() {

		return getXriOfPeerRootXri(this.getContextNode().getArcXri());
	}

	/*
	 * Methods for XDI peer root XRIs.
	 */

	/**
	 * Returns the peer root XRI of an XRI.
	 * @param xri An XRI.
	 * @return The peer root XRI of the XRI.
	 */
	public static XDI3SubSegment createPeerRootXri(XDI3Segment xri) {

		return XDI3SubSegment.create("(" + xri.toString() + ")");
	}

	/**
	 * Returns the XRI of the peer root XRI.
	 * @param xri A peer root XRI.
	 * @return The XRI of the peer root XRI.
	 */
	public static XDI3Segment getXriOfPeerRootXri(XDI3SubSegment xri) {

		if (xri.hasGCS()) return null;
		if (xri.hasLCS()) return null;
		
		if (! xri.hasXRef()) return null;

		XDI3XRef xref = xri.getXRef();
		if (! xref.hasSegment()) return null;

		return xref.getSegment();
	}

	/**
	 * Checks if a given XRI is a peer root XRI.
	 * @param xri A peer root XRI.
	 * @return True, if the XRI is a peer root XRI.
	 */
	public static boolean isPeerRootXri(XDI3SubSegment xri) {

		return getXriOfPeerRootXri(xri) != null;
	}

	/*
	 * Helper classes
	 */

	public static class MappingContextNodePeerRootIterator extends NotNullIterator<PeerRoot> {

		public MappingContextNodePeerRootIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, PeerRoot> (contextNodes) {

				@Override
				public PeerRoot map(ContextNode contextNode) {

					return PeerRoot.fromContextNode(contextNode);
				}
			});
		}
	}
}
