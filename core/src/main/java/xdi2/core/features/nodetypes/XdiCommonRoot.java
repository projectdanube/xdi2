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
 * An XDI common root, represented as a context node.
 * 
 * @author markus
 */
public class XdiCommonRoot extends XdiAbstractRoot {

	private static final long serialVersionUID = 2956364705721958108L;

	protected XdiCommonRoot(ContextNode contextNode) {

		super(contextNode);
	}

	/*
	 * Static methods
	 */

	/**
	 * Given a graph, finds and returns the XDI common root.
	 * @param graph The graph.
	 * @return The XDI common root.
	 */
	public static XdiCommonRoot findCommonRoot(Graph graph) {

		ContextNode commonRootContextNode = graph.getRootContextNode(false);

		return new XdiCommonRoot(commonRootContextNode);
	}

	/**
	 * Checks if a context node is a valid XDI common root.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI common root.
	 */
	public static boolean isValid(ContextNode contextNode) {

		if (contextNode == null) throw new NullPointerException();

		if (! contextNode.isRootContextNode()) return false;

		return true;
	}

	/**
	 * Factory method that creates an XDI common root bound to a given context node.
	 * @param contextNode The context node that is an XDI common root.
	 * @return The XDI common root.
	 */
	public static XdiCommonRoot fromContextNode(ContextNode contextNode) {

		if (contextNode == null) throw new NullPointerException();

		if (! isValid(contextNode)) return null;

		return new XdiCommonRoot(contextNode);
	}

	/*
	 * Instance methods
	 */

	public XdiPeerRoot setSelfPeerRoot(XDIAddress XDIaddress) {

		XdiPeerRoot selfPeerRoot = this.getSelfPeerRoot();
		if (selfPeerRoot != null) selfPeerRoot.getContextNode().delete();

		if (XDIaddress == null) return null;

		selfPeerRoot = this.getPeerRoot(XDIaddress, true);

		ContextNode commonRootContextNode = this.getContextNode();
		ContextNode selfPeerRootContextNode = selfPeerRoot.getContextNode();

		commonRootContextNode.delRelations(XDIDictionaryConstants.XDI_ADD_IS_REF);
		commonRootContextNode.setRelation(XDIDictionaryConstants.XDI_ADD_IS_REF, selfPeerRootContextNode);

		selfPeerRootContextNode.delRelations(XDIDictionaryConstants.XDI_ADD_REF);
		selfPeerRootContextNode.setRelation(XDIDictionaryConstants.XDI_ADD_REF, commonRootContextNode);

		return selfPeerRoot;
	}

	public XdiPeerRoot getSelfPeerRoot() {

		Relation relation = this.getContextNode().getRelation(XDIDictionaryConstants.XDI_ADD_IS_REF);
		if (relation == null) return null;

		ContextNode targetContextNode = relation.followContextNode();
		if (targetContextNode == null) return null;

		return XdiPeerRoot.fromContextNode(targetContextNode);
	}

	public ReadOnlyIterator<XdiPeerRoot> getPeerRoots() {

		return new MappingContextNodePeerRootIterator(this.getContextNode().getContextNodes());
	}

	public ReadOnlyIterator<XdiInnerRoot> getInnerRoots() {

		return new MappingContextNodeInnerRootIterator(this.getContextNode().getContextNodes());
	}
}
