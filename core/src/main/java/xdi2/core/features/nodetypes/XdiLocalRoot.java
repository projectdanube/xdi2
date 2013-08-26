package xdi2.core.features.nodetypes;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Relation;
import xdi2.core.constants.XDIConstants;
import xdi2.core.constants.XDIDictionaryConstants;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;
import xdi2.core.util.iterators.ReadOnlyIterator;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3SubSegment;

/**
 * An XDI local root, represented as a context node.
 * 
 * @author markus
 */
public class XdiLocalRoot extends XdiAbstractRoot {

	private static final long serialVersionUID = 2956364705721958108L;

	protected XdiLocalRoot(ContextNode contextNode) {

		super(contextNode);
	}

	/*
	 * Static methods
	 */

	/**
	 * Given a graph, finds and returns the XDI local root.
	 * @param graph The graph.
	 * @return The XDI local root.
	 */
	public static XdiLocalRoot findLocalRoot(Graph graph) {

		ContextNode localRootContextNode = graph.getRootContextNode();

		return new XdiLocalRoot(localRootContextNode);
	}

	/**
	 * Checks if a context node is a valid XDI local root.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI local root.
	 */
	public static boolean isValid(ContextNode contextNode) {

		return contextNode.isRootContextNode();
	}

	/**
	 * Factory method that creates an XDI local root bound to a given context node.
	 * @param contextNode The context node that is an XDI local root.
	 * @return The XDI local root.
	 */
	public static XdiLocalRoot fromContextNode(ContextNode contextNode) {

		if (! isValid(contextNode)) return null;

		return new XdiLocalRoot(contextNode);
	}

	/*
	 * Instance methods
	 */

	public XdiPeerRoot setSelfPeerRoot(XDI3Segment xri) {

		XdiPeerRoot selfPeerRoot = this.getSelfPeerRoot();
		if (selfPeerRoot != null) selfPeerRoot.getContextNode().delete();

		if (xri == null) return null;

		selfPeerRoot = this.findPeerRoot(xri, true);

		ContextNode localRootContextNode = this.getContextNode();
		ContextNode selfPeerRootContextNode = selfPeerRoot.getContextNode();

		localRootContextNode.setRelation(XDIDictionaryConstants.XRI_S_IS_REF, selfPeerRootContextNode);
		selfPeerRootContextNode.setRelation(XDIDictionaryConstants.XRI_S_REF, localRootContextNode);

		return selfPeerRoot;
	}

	public XdiPeerRoot getSelfPeerRoot() {

		Relation relation = this.getContextNode().getRelation(XDIDictionaryConstants.XRI_S_IS_REF);
		if (relation == null) return null;

		return XdiPeerRoot.fromContextNode(relation.follow());
	}

	public ReadOnlyIterator<XdiPeerRoot> getPeerRoots() {

		return new NotNullIterator<XdiPeerRoot> (new MappingIterator<ContextNode, XdiPeerRoot> (this.getContextNode().getContextNodes()) {

			@Override
			public XdiPeerRoot map(ContextNode contextNode) {

				return XdiPeerRoot.fromContextNode(contextNode);
			}
		});
	}

	/*
	 * Methods for XDI local root XRIs
	 */

	/**
	 * Returns the local root XRI.
	 * @return The local root XRI.
	 */
	public static XDI3SubSegment createLocalRootXri() {

		return XDIConstants.XRI_SS_ROOT;
	}

	/**
	 * Checks if a given XRI is a local root XRI.
	 * @param xri A local root XRI.
	 * @return True, if the XRI is a local root XRI.
	 */
	public static boolean isLocalRootXri(XDI3SubSegment xri) {

		return XDIConstants.XRI_SS_ROOT.equals(xri);
	}
}
