package xdi2.core.features.nodetypes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;

public abstract class XdiAbstractRoot extends XdiAbstractContext<XdiRoot> implements XdiRoot {

	private static final long serialVersionUID = 8157589883719452790L;

	private static final Logger log = LoggerFactory.getLogger(XdiAbstractRoot.class);

	public XdiAbstractRoot(ContextNode contextNode) {

		super(contextNode);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI root.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI root.
	 */
	public static boolean isValid(ContextNode contextNode) {

		if (contextNode == null) throw new NullPointerException();

		if (XdiCommonRoot.isValid(contextNode)) return true;
		if (XdiPeerRoot.isValid(contextNode)) return true;
		if (XdiInnerRoot.isValid(contextNode)) return true;

		return false;
	}

	/**
	 * Factory method that creates an XDI root bound to a given context node.
	 * @param contextNode The context node that is an XDI root.
	 * @return The XDI root.
	 */
	public static XdiRoot fromContextNode(ContextNode contextNode) {

		if (contextNode == null) throw new NullPointerException();

		XdiRoot xdiRoot;

		if ((xdiRoot = XdiCommonRoot.fromContextNode(contextNode)) != null) return xdiRoot;
		if ((xdiRoot = XdiPeerRoot.fromContextNode(contextNode)) != null) return xdiRoot;
		if ((xdiRoot = XdiInnerRoot.fromContextNode(contextNode)) != null) return xdiRoot;

		return null;
	}

	/*
	 * Getting roots under this root
	 */

	@Override
	public XdiPeerRoot getPeerRoot(XDIAddress XDIaddress, boolean create) {

		if (log.isTraceEnabled()) log.trace("getPeerRoot(" + XDIaddress + "," + create + ")");

		XDIArc peerRootarc = XdiPeerRoot.createPeerRootXDIArc(XDIaddress);

		ContextNode peerRootContextNode = create ? this.getContextNode().setContextNode(peerRootarc) : this.getContextNode().getContextNode(peerRootarc, false);
		if (peerRootContextNode == null) return null;

		return new XdiPeerRoot(peerRootContextNode);
	}

	@Override
	public XdiInnerRoot getInnerRoot(XDIAddress subject, XDIAddress predicate, boolean create) {

		if (log.isTraceEnabled()) log.trace("getInnerRoot(" + subject + "," + predicate + "," + create + ")");

		XDIArc innerRootXDIArc = XdiInnerRoot.createInnerRootXDIArc(subject, predicate);

		ContextNode innerRootContextNode = create ? this.getContextNode().setContextNode(innerRootXDIArc) : this.getContextNode().getContextNode(innerRootXDIArc, false);
		if (innerRootContextNode == null) return null;

		return new XdiInnerRoot(innerRootContextNode);
	}

	@Override
	public XdiRoot getRoot(XDIAddress XDIaddress, boolean create) {

		if (log.isTraceEnabled()) log.trace("getRoot(" + XDIaddress + "," + create + ")");

		XdiRoot root = this;

		for (int i=0; i<XDIaddress.getNumXDIArcs(); i++) {

			XDIArc XDIarc = XDIaddress.getXDIArc(i);

			XdiRoot nextRoot;

			if (XdiPeerRoot.isValidXDIArc(XDIarc)) {

				ContextNode peerRootContextNode = create ? root.getContextNode().setContextNode(XDIarc) : root.getContextNode().getContextNode(XDIarc, false);
				if (peerRootContextNode == null) break;

				nextRoot = new XdiPeerRoot(peerRootContextNode);
			} else if (XdiInnerRoot.isValidXDIArc(XDIarc)) {

				ContextNode innerRootContextNode = create ? root.getContextNode().setContextNode(XDIarc) : root.getContextNode().getContextNode(XDIarc, false);
				if (innerRootContextNode == null) break;

				nextRoot = new XdiInnerRoot(innerRootContextNode);
			} else {

				break;
			}

			root = nextRoot;
		}

		return root;
	}

	/*
	 * Methods for arcs
	 */

	public static boolean isValidXDIArc(XDIArc XDIarc) {

		if (XDIarc == null) throw new NullPointerException();

		if (XdiPeerRoot.isValidXDIArc(XDIarc)) return true;
		if (XdiInnerRoot.isValidXDIArc(XDIarc)) return true;

		return false;
	}
}
