package xdi2.core.features.roots;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3SubSegment;
import xdi2.core.xri3.XDI3XRef;

/**
 * An XDI remote root, represented as a context node.
 * 
 * @author markus
 */
public final class RemoteRoot extends AbstractRoot {

	private static final long serialVersionUID = -4689596452249483618L;

	protected RemoteRoot(ContextNode contextNode) {

		super(contextNode);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI remote root.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI remote root.
	 */
	public static boolean isValid(ContextNode contextNode) {

		return isRemoteRootXri(contextNode.getArcXri());
	}

	/**
	 * Factory method that creates an XDI remote root bound to a given context node.
	 * @param contextNode The context node that is an XDI remote root.
	 * @return The XDI remote root.
	 */
	public static RemoteRoot fromContextNode(ContextNode contextNode) {

		if (! isValid(contextNode)) return null;

		return new RemoteRoot(contextNode);
	}

	/*
	 * Instance methods
	 */

	/**
	 * Checks if this XDI remote root is the self XDI remote root of the graph.
	 * @return True, if this is the self XDI remote root.
	 */
	public boolean isSelfRemoteRoot() {

		RemoteRoot selfRemoteRoot = this.findLocalRoot().getSelfRemoteRoot();

		return this.equals(selfRemoteRoot);
	}

	public XDI3Segment getXriOfRemoteRoot() {

		return getXriOfRemoteRootXri(this.getContextNode().getArcXri());
	}

	/*
	 * Methods for XDI remote root XRIs.
	 */

	/**
	 * Returns the remote root XRI of an XRI.
	 * @param xri An XRI.
	 * @return The remote root XRI of the XRI.
	 */
	public static XDI3SubSegment createRemoteRootXri(XDI3Segment xri) {

		return XDI3SubSegment.create("(" + xri.toString() + ")");
	}

	/**
	 * Returns the XRI of the remote root XRI.
	 * @param xri A remote root XRI.
	 * @return The XRI of the remote root XRI.
	 */
	public static XDI3Segment getXriOfRemoteRootXri(XDI3SubSegment xri) {

		if (xri.hasGCS()) return null;
		if (xri.hasLCS()) return null;
		
		if (! xri.hasXRef()) return null;

		XDI3XRef xref = xri.getXRef();
		if (! xref.hasSegment()) return null;

		return xref.getSegment();
	}

	/**
	 * Checks if a given XRI is a remote root XRI.
	 * @param xri A remote root XRI.
	 * @return True, if the XRI is a remote root XRI.
	 */
	public static boolean isRemoteRootXri(XDI3SubSegment xri) {

		return getXriOfRemoteRootXri(xri) != null;
	}

	/*
	 * Helper classes
	 */

	public static class MappingContextNodeRemoteRootIterator extends NotNullIterator<RemoteRoot> {

		public MappingContextNodeRemoteRootIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, RemoteRoot> (contextNodes) {

				@Override
				public RemoteRoot map(ContextNode contextNode) {

					return RemoteRoot.fromContextNode(contextNode);
				}
			});
		}
	}
}
