package xdi2.core.features.roots;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;
import xdi2.core.xri3.XDI3Constants;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3SubSegment;
import xdi2.core.xri3.XDI3XRef;

/**
 * An XDI peer root, represented as a context node.
 * 
 * @author markus
 */
public final class XdiPeerRoot extends XdiRoot {

	private static final long serialVersionUID = -4689596452249483618L;

	protected XdiPeerRoot(ContextNode contextNode) {

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

		return isPeerRootArcXri(contextNode.getArcXri());
	}

	/**
	 * Factory method that creates an XDI peer root bound to a given context node.
	 * @param contextNode The context node that is an XDI peer root.
	 * @return The XDI peer root.
	 */
	public static XdiPeerRoot fromContextNode(ContextNode contextNode) {

		if (! isValid(contextNode)) return null;

		return new XdiPeerRoot(contextNode);
	}

	/*
	 * Instance methods
	 */

	/**
	 * Checks if this XDI peer root is the self XDI peer root of the graph.
	 * @return True, if this is the self XDI peer root.
	 */
	public boolean isSelfPeerRoot() {

		XdiPeerRoot selfPeerRoot = this.findLocalRoot().getSelfPeerRoot();

		return this.equals(selfPeerRoot);
	}

	public XDI3Segment getXriOfPeerRoot() {

		return getXriOfPeerRootArcXri(this.getContextNode().getArcXri());
	}

	/*
	 * Methods for XDI peer root XRIs
	 */

	/**
	 * Returns the peer root XRI of an XRI.
	 * @param xri An XRI.
	 * @return The peer root XRI of the XRI.
	 */
	public static XDI3SubSegment createPeerRootArcXri(XDI3Segment xri) {

		return XDI3SubSegment.create("" + XDI3Constants.XS_ROOT.charAt(0) + xri + XDI3Constants.XS_ROOT.charAt(1));
	}

	/**
	 * Returns the XRI of the peer root XRI.
	 * @param arcXri A peer root XRI.
	 * @return The XRI of the peer root XRI.
	 */
	public static XDI3Segment getXriOfPeerRootArcXri(XDI3SubSegment arcXri) {

		if (arcXri == null) return null;

		if (arcXri.hasCs()) return null;
		if (arcXri.isSingleton()) return null;
		if (arcXri.isAttribute()) return null;
		if (! arcXri.hasXRef()) return null;

		XDI3XRef xref = arcXri.getXRef();
		if (! XDI3Constants.XS_ROOT.equals(xref.getXs())) return null;
		if (! xref.hasSegment()) return null;

		return xref.getSegment();
	}

	/**
	 * Checks if a given XRI is a peer root XRI.
	 * @param arcXri A peer root XRI.
	 * @return True, if the XRI is a peer root XRI.
	 */
	public static boolean isPeerRootArcXri(XDI3SubSegment arcXri) {

		return getXriOfPeerRootArcXri(arcXri) != null;
	}

	/*
	 * Helper classes
	 */

	public static class MappingContextNodePeerRootIterator extends NotNullIterator<XdiPeerRoot> {

		public MappingContextNodePeerRootIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, XdiPeerRoot> (contextNodes) {

				@Override
				public XdiPeerRoot map(ContextNode contextNode) {

					return XdiPeerRoot.fromContextNode(contextNode);
				}
			});
		}
	}
}
