package xdi2.core.features.nodetypes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.Relation;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.syntax.XDIStatement;
import xdi2.core.util.XDIStatementUtil;
import xdi2.core.util.XDIAddressUtil;

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

		if (contextNode == null) return false;

		return
				XdiCommonRoot.isValid(contextNode) ||
				XdiPeerRoot.isValid(contextNode) ||
				XdiInnerRoot.isValid(contextNode);
	}

	/**
	 * Factory method that creates an XDI root bound to a given context node.
	 * @param contextNode The context node that is an XDI root.
	 * @return The XDI root.
	 */
	public static XdiRoot fromContextNode(ContextNode contextNode) {

		XdiRoot xdiRoot;

		if ((xdiRoot = XdiCommonRoot.fromContextNode(contextNode)) != null) return xdiRoot;
		if ((xdiRoot = XdiPeerRoot.fromContextNode(contextNode)) != null) return xdiRoot;
		if ((xdiRoot = XdiInnerRoot.fromContextNode(contextNode)) != null) return xdiRoot;

		return null;
	}

	/*
	 * Roots related to this root
	 */

	@Override
	public XdiPeerRoot getPeerRoot(XDIAddress address, boolean create) {

		if (log.isTraceEnabled()) log.trace("getPeerRoot(" + address + "," + create + ")");

		XDIArc peerRootarc = XdiPeerRoot.createPeerRootXDIArc(address);

		ContextNode peerRootContextNode = create ? this.getContextNode().setContextNode(peerRootarc) : this.getContextNode().getContextNode(peerRootarc, false);
		if (peerRootContextNode == null) return null;

		return new XdiPeerRoot(peerRootContextNode);
	}

	@Override
	public XdiInnerRoot getInnerRoot(XDIAddress subject, XDIAddress predicate, boolean create) {

		if (log.isTraceEnabled()) log.trace("getInnerRoot(" + subject + "," + predicate + "," + create + ")");

		XDIArc innerRootarc = XdiInnerRoot.createInnerRootXDIArc(subject, predicate);

		ContextNode innerRootContextNode = create ? this.getContextNode().setContextNode(innerRootarc) : this.getContextNode().getContextNode(innerRootarc, false);
		if (innerRootContextNode == null) return null;

		return new XdiInnerRoot(innerRootContextNode);
	}

	@Override
	public XdiRoot getRoot(XDIAddress address, boolean create) {

		if (log.isTraceEnabled()) log.trace("getRoot(" + address + "," + create + ")");

		XdiRoot root = this;

		for (int i=0; i<address.getNumXDIArcs(); i++) {

			XDIArc arc = address.getXDIArc(i);

			XdiRoot nextRoot;

			if (XdiPeerRoot.isPeerRootXDIArc(arc)) {

				ContextNode peerRootContextNode = create ? root.getContextNode().setContextNode(arc) : root.getContextNode().getContextNode(arc, false);
				if (peerRootContextNode == null) break;

				nextRoot = new XdiPeerRoot(peerRootContextNode);
			} if (XdiInnerRoot.isInnerRootXDIArc(arc)) {

				ContextNode innerRootContextNode = create ? root.getContextNode().setContextNode(arc) : root.getContextNode().getContextNode(arc, false);
				if (innerRootContextNode == null) break;

				ContextNode contextNode = create ? root.getContextNode().setDeepContextNode(XdiInnerRoot.getSubjectOfInnerRootXDIArc(arc)) : root.getContextNode().getDeepContextNode(XdiInnerRoot.getSubjectOfInnerRootXDIArc(arc), false);
				if (contextNode == null) break;

				Relation relation = create ? contextNode.setRelation(XdiInnerRoot.getPredicateOfInnerRootXDIArc(arc), innerRootContextNode.getXDIAddress()) : contextNode.getRelation(XdiInnerRoot.getPredicateOfInnerRootXDIArc(arc), innerRootContextNode.getXDIAddress());
				if (relation == null) break;

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
	 * Methods for XDI root addresses
	 */

	/**
	 * Checks if a given address is an XDI root address.
	 * @param arc An XDI root address.
	 * @return True, if the address is an XDI root address.
	 */
	public static boolean isRootArc(XDIArc arc) {

		if (log.isTraceEnabled()) log.trace("isRootarc(" + arc + ")");

		if (XdiPeerRoot.isPeerRootXDIArc(arc)) return true;
		if (XdiInnerRoot.isInnerRootXDIArc(arc)) return true;

		return false;
	}
}
