package xdi2.core.features.nodetypes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.Relation;
import xdi2.core.util.StatementUtil;
import xdi2.core.util.XDI3Util;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;
import xdi2.core.xri3.XDI3SubSegment;

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
	public XdiPeerRoot getPeerRoot(XDI3Segment xri, boolean create) {

		if (log.isTraceEnabled()) log.trace("getPeerRoot(" + xri + "," + create + ")");

		XDI3SubSegment peerRootArcXri = XdiPeerRoot.createPeerRootArcXri(xri);

		ContextNode peerRootContextNode = create ? this.getContextNode().setContextNode(peerRootArcXri) : this.getContextNode().getContextNode(peerRootArcXri, false);
		if (peerRootContextNode == null) return null;

		return new XdiPeerRoot(peerRootContextNode);
	}

	@Override
	public XdiInnerRoot getInnerRoot(XDI3Segment subject, XDI3Segment predicate, boolean create) {

		if (log.isTraceEnabled()) log.trace("getInnerRoot(" + subject + "," + predicate + "," + create + ")");

		XDI3SubSegment innerRootArcXri = XdiInnerRoot.createInnerRootArcXri(subject, predicate);

		ContextNode innerRootContextNode = create ? this.getContextNode().setContextNode(innerRootArcXri) : this.getContextNode().getContextNode(innerRootArcXri, false);
		if (innerRootContextNode == null) return null;

		return new XdiInnerRoot(innerRootContextNode);
	}

	@Override
	public XdiRoot getRoot(XDI3Segment xri, boolean create) {

		if (log.isTraceEnabled()) log.trace("getRoot(" + xri + "," + create + ")");

		XdiRoot root = this;

		for (int i=0; i<xri.getNumSubSegments(); i++) {

			XDI3SubSegment subSegment = xri.getSubSegment(i);

			XdiRoot nextRoot;

			if (XdiPeerRoot.isPeerRootArcXri(subSegment)) {

				ContextNode peerRootContextNode = create ? this.getContextNode().setContextNode(subSegment) : this.getContextNode().getContextNode(subSegment, false);
				if (peerRootContextNode == null) break;

				nextRoot = new XdiPeerRoot(peerRootContextNode);
			} if (XdiInnerRoot.isInnerRootArcXri(subSegment)) {

				ContextNode innerRootContextNode = create ? this.getContextNode().setContextNode(subSegment) : this.getContextNode().getContextNode(subSegment, false);
				if (innerRootContextNode == null) break;

				ContextNode contextNode = create ? this.getContextNode().setDeepContextNode(XdiInnerRoot.getSubjectOfInnerRootXri(subSegment)) : this.getContextNode().getDeepContextNode(XdiInnerRoot.getSubjectOfInnerRootXri(subSegment), false);
				if (contextNode == null) break;

				Relation relation = create ? contextNode.setRelation(XdiInnerRoot.getPredicateOfInnerRootXri(subSegment), innerRootContextNode.getXri()) : contextNode.getRelation(XdiInnerRoot.getPredicateOfInnerRootXri(subSegment), innerRootContextNode.getXri());
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
	public XDI3Segment absoluteToRelativeXri(XDI3Segment absoluteXri) {

		XDI3Segment relativeXri = XDI3Util.removeStartXri(absoluteXri, this.getContextNode().getXri());

		if (log.isTraceEnabled()) log.trace("absoluteToRelativeXri(" + absoluteXri + " --> " + relativeXri + ")");

		return relativeXri;
	}

	@Override
	public XDI3Segment relativeToAbsoluteXri(XDI3Segment relativeXri) {

		XDI3Segment absoluteXri = XDI3Util.concatXris(this.getContextNode().getXri(), relativeXri);

		if (log.isTraceEnabled()) log.trace("relativeToAbsoluteXri(" + relativeXri + " --> " + absoluteXri + ")");

		return absoluteXri;
	}

	@Override
	public XDI3Statement absoluteToRelativeStatementXri(XDI3Statement absoluteStatementXri) {

		XDI3Statement relativeStatementXri = StatementUtil.removeStartXriStatement(absoluteStatementXri, this.getContextNode().getXri());

		if (log.isTraceEnabled()) log.trace("absoluteToRelativeStatementXri(" + absoluteStatementXri + " --> " + relativeStatementXri + ")");

		return relativeStatementXri;
	}

	@Override
	public XDI3Statement relativeToAbsoluteStatementXri(XDI3Statement relativeStatementXri) {

		XDI3Statement absoluteStatementXri = StatementUtil.concatXriStatement(this.getContextNode().getXri(), relativeStatementXri);

		if (log.isTraceEnabled()) log.trace("relativeToAbsoluteStatementXri(" + relativeStatementXri + " --> " + absoluteStatementXri + ")");

		return absoluteStatementXri;
	}

	/*
	 * Methods for XDI root XRIs
	 */

	/**
	 * Checks if a given XRI is an XDI root XRI.
	 * @param arcXri An XDI root XRI.
	 * @return True, if the XRI is an XDI root XRI.
	 */
	public static boolean isRootArcXri(XDI3SubSegment arcXri) {

		if (log.isTraceEnabled()) log.trace("isRootArcXri(" + arcXri + ")");

		if (XdiPeerRoot.isPeerRootArcXri(arcXri)) return true;
		if (XdiInnerRoot.isInnerRootArcXri(arcXri)) return true;

		return false;
	}
}
