package xdi2.core.features.nodetypes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.Relation;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.syntax.XDIStatement;
import xdi2.core.util.StatementUtil;
import xdi2.core.util.AddressUtil;

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
				XdiLocalRoot.isValid(contextNode) ||
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

		if ((xdiRoot = XdiLocalRoot.fromContextNode(contextNode)) != null) return xdiRoot;
		if ((xdiRoot = XdiPeerRoot.fromContextNode(contextNode)) != null) return xdiRoot;
		if ((xdiRoot = XdiInnerRoot.fromContextNode(contextNode)) != null) return xdiRoot;

		return null;
	}

	/*
	 * Roots related to this root
	 */

	@Override
	public XdiPeerRoot getPeerRoot(XDIAddress xri, boolean create) {

		if (log.isTraceEnabled()) log.trace("getPeerRoot(" + xri + "," + create + ")");

		XDIArc peerRootarc = XdiPeerRoot.createPeerRootArc(xri);

		ContextNode peerRootContextNode = create ? this.getContextNode().setContextNode(peerRootarc) : this.getContextNode().getContextNode(peerRootarc, false);
		if (peerRootContextNode == null) return null;

		return new XdiPeerRoot(peerRootContextNode);
	}

	@Override
	public XdiInnerRoot getInnerRoot(XDIAddress subject, XDIAddress predicate, boolean create) {

		if (log.isTraceEnabled()) log.trace("getInnerRoot(" + subject + "," + predicate + "," + create + ")");

		XDIArc innerRootarc = XdiInnerRoot.createInnerRootarc(subject, predicate);

		ContextNode innerRootContextNode = create ? this.getContextNode().setContextNode(innerRootarc) : this.getContextNode().getContextNode(innerRootarc, false);
		if (innerRootContextNode == null) return null;

		return new XdiInnerRoot(innerRootContextNode);
	}

	@Override
	public XdiRoot getRoot(XDIAddress xri, boolean create) {

		if (log.isTraceEnabled()) log.trace("getRoot(" + xri + "," + create + ")");

		XdiRoot root = this;

		for (int i=0; i<xri.getNumArcs(); i++) {

			XDIArc subSegment = xri.getArc(i);

			XdiRoot nextRoot;

			if (XdiPeerRoot.isPeerRootArc(subSegment)) {

				ContextNode peerRootContextNode = create ? root.getContextNode().setContextNode(subSegment) : root.getContextNode().getContextNode(subSegment, false);
				if (peerRootContextNode == null) break;

				nextRoot = new XdiPeerRoot(peerRootContextNode);
			} if (XdiInnerRoot.isInnerRootarc(subSegment)) {

				ContextNode innerRootContextNode = create ? root.getContextNode().setContextNode(subSegment) : root.getContextNode().getContextNode(subSegment, false);
				if (innerRootContextNode == null) break;

				ContextNode contextNode = create ? root.getContextNode().setDeepContextNode(XdiInnerRoot.getSubjectOfInnerRootAddress(subSegment)) : root.getContextNode().getDeepContextNode(XdiInnerRoot.getSubjectOfInnerRootAddress(subSegment), false);
				if (contextNode == null) break;

				Relation relation = create ? contextNode.setRelation(XdiInnerRoot.getPredicateOfInnerRootAddress(subSegment), innerRootContextNode.getAddress()) : contextNode.getRelation(XdiInnerRoot.getPredicateOfInnerRootAddress(subSegment), innerRootContextNode.getAddress());
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
	public XDIAddress absoluteToRelativeAddress(XDIAddress absoluteAddress) {

		XDIAddress relativeAddress = AddressUtil.removeStartAddress(absoluteAddress, this.getContextNode().getAddress());

		if (log.isTraceEnabled()) log.trace("absoluteToRelativeAddress(" + absoluteAddress + " --> " + relativeAddress + ")");

		return relativeAddress;
	}

	@Override
	public XDIAddress relativeToAbsoluteAddress(XDIAddress relativeAddress) {

		XDIAddress absoluteAddress = AddressUtil.concatAddresses(this.getContextNode().getAddress(), relativeAddress);

		if (log.isTraceEnabled()) log.trace("relativeToAbsoluteAddress(" + relativeAddress + " --> " + absoluteAddress + ")");

		return absoluteAddress;
	}

	@Override
	public XDIStatement absoluteToRelativeStatement(XDIStatement absoluteStatementAddress) {

		XDIStatement relativeStatementAddress = StatementUtil.removeStartAddressStatement(absoluteStatementAddress, this.getContextNode().getAddress());

		if (log.isTraceEnabled()) log.trace("absoluteToRelativeStatementAddress(" + absoluteStatementAddress + " --> " + relativeStatementAddress + ")");

		return relativeStatementAddress;
	}

	@Override
	public XDIStatement relativeToAbsoluteStatement(XDIStatement relativeStatementAddress) {

		XDIStatement absoluteStatementAddress = StatementUtil.concatAddressStatement(this.getContextNode().getAddress(), relativeStatementAddress);

		if (log.isTraceEnabled()) log.trace("relativeToAbsoluteStatementAddress(" + relativeStatementAddress + " --> " + absoluteStatementAddress + ")");

		return absoluteStatementAddress;
	}

	/*
	 * Methods for XDI root XRIs
	 */

	/**
	 * Checks if a given XRI is an XDI root XRI.
	 * @param arc An XDI root XRI.
	 * @return True, if the XRI is an XDI root XRI.
	 */
	public static boolean isRootarc(XDIArc arc) {

		if (log.isTraceEnabled()) log.trace("isRootarc(" + arc + ")");

		if (XdiPeerRoot.isPeerRootArc(arc)) return true;
		if (XdiInnerRoot.isInnerRootarc(arc)) return true;

		return false;
	}
}
