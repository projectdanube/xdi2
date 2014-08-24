package xdi2.core.features.nodetypes;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Relation;
import xdi2.core.constants.XDIDictionaryConstants;
import xdi2.core.features.nodetypes.XdiInnerRoot.MappingContextNodeInnerRootIterator;
import xdi2.core.features.nodetypes.XdiPeerRoot.MappingContextNodePeerRootIterator;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.util.iterators.ReadOnlyIterator;

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

		ContextNode localRootContextNode = graph.getRootContextNode(false);

		return new XdiLocalRoot(localRootContextNode);
	}

	/**
	 * Checks if a context node is a valid XDI local root.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI local root.
	 */
	public static boolean isValid(ContextNode contextNode) {

		if (contextNode == null) return false;

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

	public XdiPeerRoot setSelfPeerRoot(XDIAddress address) {

		XdiPeerRoot selfPeerRoot = this.getSelfPeerRoot();
		if (selfPeerRoot != null) selfPeerRoot.getContextNode().delete();

		if (address == null) return null;

		selfPeerRoot = this.getPeerRoot(address, true);

		ContextNode localRootContextNode = this.getContextNode();
		ContextNode selfPeerRootContextNode = selfPeerRoot.getContextNode();

		localRootContextNode.delRelations(XDIDictionaryConstants.XDI_ADD_IS_REF);
		localRootContextNode.setRelation(XDIDictionaryConstants.XDI_ADD_IS_REF, selfPeerRootContextNode);

		selfPeerRootContextNode.delRelations(XDIDictionaryConstants.XDI_ADD_REF);
		selfPeerRootContextNode.setRelation(XDIDictionaryConstants.XDI_ADD_REF, localRootContextNode);

		return selfPeerRoot;
	}

	public XdiPeerRoot getSelfPeerRoot() {

		Relation relation = this.getContextNode().getRelation(XDIDictionaryConstants.XDI_ADD_IS_REF);
		if (relation == null) return null;

		return XdiPeerRoot.fromContextNode(relation.follow());
	}

	public ReadOnlyIterator<XdiPeerRoot> getPeerRoots() {

		return new MappingContextNodePeerRootIterator(this.getContextNode().getContextNodes());
	}

	public ReadOnlyIterator<XdiInnerRoot> getInnerRoots() {

		return new MappingContextNodeInnerRootIterator(this.getContextNode().getContextNodes());
	}
}
