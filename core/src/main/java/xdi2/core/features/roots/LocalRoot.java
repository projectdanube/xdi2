package xdi2.core.features.roots;

import xdi2.core.ContextNode;
import xdi2.core.Relation;
import xdi2.core.constants.XDIDictionaryConstants;
import xdi2.core.xri3.XDI3Segment;

/**
 * An XDI local root, represented as a context node.
 * 
 * @author markus
 */
public class LocalRoot extends AbstractRoot {

	private static final long serialVersionUID = 2956364705721958108L;

	protected LocalRoot(ContextNode contextNode) {

		super(contextNode);
	}

	/*
	 * Static methods
	 */

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
	public static LocalRoot fromContextNode(ContextNode contextNode) {

		if (! isValid(contextNode)) return null;

		return new LocalRoot(contextNode);
	}

	/*
	 * Instance methods
	 */

	public RemoteRoot setSelfRemoteRoot(XDI3Segment xri) {

		RemoteRoot selfRemoteRoot = this.getSelfRemoteRoot();
		if (selfRemoteRoot != null) selfRemoteRoot.getContextNode().delete();

		if (xri == null) return null;

		selfRemoteRoot = this.findRemoteRoot(xri, true);

		ContextNode localRootContextNode = this.getContextNode();
		ContextNode selfRemoteRootContextNode = selfRemoteRoot.getContextNode();

		localRootContextNode.createRelation(XDIDictionaryConstants.XRI_S_IS_REF, selfRemoteRootContextNode);
		selfRemoteRootContextNode.createRelation(XDIDictionaryConstants.XRI_S_REF, localRootContextNode);

		return selfRemoteRoot;
	}

	public RemoteRoot getSelfRemoteRoot() {

		Relation relation = this.getContextNode().getRelation(XDIDictionaryConstants.XRI_S_IS_REF);
		if (relation == null) return null;

		return RemoteRoot.fromContextNode(relation.follow());
	}
}
