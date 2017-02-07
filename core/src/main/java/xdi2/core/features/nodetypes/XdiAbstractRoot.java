package xdi2.core.features.nodetypes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.features.nodetypes.XdiInnerRoot.MappingContextNodeInnerRootIterator;
import xdi2.core.features.nodetypes.XdiPeerRoot.MappingContextNodePeerRootIterator;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.syntax.XDIStatement;
import xdi2.core.util.GraphUtil;
import xdi2.core.util.XDIAddressUtil;
import xdi2.core.util.XDIStatementUtil;
import xdi2.core.util.iterators.ReadOnlyIterator;

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

	public static XdiRoot fromXDIAddress(XDIAddress XDIaddress) {

		return fromContextNode(GraphUtil.contextNodeFromComponents(XDIaddress));
	}

	/*
	 * Roots related to this root
	 */

	@Override
	public ReadOnlyIterator<XdiPeerRoot> getPeerRoots() {

		return new MappingContextNodePeerRootIterator(this.getContextNode().getContextNodes());
	}

	@Override
	public ReadOnlyIterator<XdiInnerRoot> getInnerRoots() {

		return new MappingContextNodeInnerRootIterator(this.getContextNode().getContextNodes());
	}

	@Override
	public XdiPeerRoot getPeerRoot(XDIAddress XDIaddress, boolean create) {

		if (log.isTraceEnabled()) log.trace("getPeerRoot(" + XDIaddress + "," + create + ")");

		XDIArc peerRootXDIArc = XdiPeerRoot.createPeerRootXDIArc(XDIaddress);

		ContextNode peerRootContextNode = create ? this.getContextNode().setContextNode(peerRootXDIArc) : this.getContextNode().getContextNode(peerRootXDIArc, false);
		if (peerRootContextNode == null) return null;

		return new XdiPeerRoot(peerRootContextNode);
	}

	@Override
	public XdiPeerRoot getPeerRoot(XDIArc peerRootXDIArc, boolean create) {

		if (log.isTraceEnabled()) log.trace("getPeerRoot(" + peerRootXDIArc + "," + create + ")");

		ContextNode peerRootContextNode = create ? this.getContextNode().setContextNode(peerRootXDIArc) : this.getContextNode().getContextNode(peerRootXDIArc, false);
		if (peerRootContextNode == null) return null;

		return XdiPeerRoot.fromContextNode(peerRootContextNode);
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
	public XdiInnerRoot getInnerRoot(XDIArc innerRootXDIArc, boolean create) {

		if (log.isTraceEnabled()) log.trace("getInnerRoot(" + innerRootXDIArc + "," + create + ")");

		ContextNode innerRootContextNode = create ? this.getContextNode().setContextNode(innerRootXDIArc) : this.getContextNode().getContextNode(innerRootXDIArc, false);
		if (innerRootContextNode == null) return null;

		return XdiInnerRoot.fromContextNode(innerRootContextNode);
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
				if (peerRootContextNode == null) return null;

				nextRoot = new XdiPeerRoot(peerRootContextNode);
			} else if (XdiInnerRoot.isValidXDIArc(XDIarc)) {

				ContextNode innerRootContextNode = create ? root.getContextNode().setContextNode(XDIarc) : root.getContextNode().getContextNode(XDIarc, false);
				if (innerRootContextNode == null) return null;

				nextRoot = new XdiInnerRoot(innerRootContextNode);
			} else {

				break;
			}

			root = nextRoot;
		}

		return root;
	}

	/*
	 * Addresses and statements relative to this root
	 */

	@Override
	public XDIAddress absoluteToRelativeXDIAddress(XDIAddress absoluteAddress) {

		XDIAddress relativeAddress = XDIAddressUtil.removeStartXDIAddress(absoluteAddress, this.getContextNode().getXDIAddress());

		if (log.isTraceEnabled()) log.trace("absoluteToRelativeAddress(" + absoluteAddress + " --> " + relativeAddress + ")");

		return relativeAddress;
	}

	@Override
	public XDIAddress relativeToAbsoluteXDIAddress(XDIAddress relativeAddress) {

		XDIAddress absoluteAddress = XDIAddressUtil.concatXDIAddresses(this.getContextNode().getXDIAddress(), relativeAddress);

		if (log.isTraceEnabled()) log.trace("relativeToAbsoluteAddress(" + relativeAddress + " --> " + absoluteAddress + ")");

		return absoluteAddress;
	}

	@Override
	public XDIStatement absoluteToRelativeXDIStatement(XDIStatement absoluteStatementAddress) {

		XDIStatement relativeStatementAddress = XDIStatementUtil.removeStartXDIStatement(absoluteStatementAddress, this.getContextNode().getXDIAddress());

		if (log.isTraceEnabled()) log.trace("absoluteToRelativeStatementAddress(" + absoluteStatementAddress + " --> " + relativeStatementAddress + ")");

		return relativeStatementAddress;
	}

	@Override
	public XDIStatement relativeToAbsoluteXDIStatement(XDIStatement relativeStatementAddress) {

		XDIStatement absoluteStatementAddress = XDIStatementUtil.concatXDIStatement(this.getContextNode().getXDIAddress(), relativeStatementAddress);

		if (log.isTraceEnabled()) log.trace("relativeToAbsoluteStatementAddress(" + relativeStatementAddress + " --> " + absoluteStatementAddress + ")");

		return absoluteStatementAddress;
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
