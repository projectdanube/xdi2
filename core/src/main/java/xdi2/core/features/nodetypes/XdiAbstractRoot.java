package xdi2.core.features.nodetypes;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.Relation;
import xdi2.core.util.StatementUtil;
import xdi2.core.util.XDI3Util;
import xdi2.core.util.iterators.MappingIterator;
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
	 * Finding roots related to this root
	 */

	@Override
	public XdiLocalRoot findLocalRoot() {

		if (log.isTraceEnabled()) log.trace("findLocalRoot()");

		return new XdiLocalRoot(this.getContextNode().getGraph().getRootContextNode(false));
	}

	@Override
	public XdiPeerRoot findPeerRoot(XDI3Segment xri, boolean create) {

		if (log.isTraceEnabled()) log.trace("findPeerRoot(" + xri + "," + create + ")");

		XDI3SubSegment peerRootArcXri = XdiPeerRoot.createPeerRootArcXri(xri);

		ContextNode peerRootContextNode = create ? this.getContextNode().setContextNode(peerRootArcXri) : this.getContextNode().getContextNode(peerRootArcXri, false);
		if (peerRootContextNode == null) return null;

		return new XdiPeerRoot(peerRootContextNode);
	}

	@Override
	public XdiInnerRoot findInnerRoot(XDI3Segment subject, XDI3Segment predicate, boolean create) {

		if (log.isTraceEnabled()) log.trace("findInnerRoot(" + subject + "," + predicate + "," + create + ")");

		XDI3SubSegment innerRootArcXri = XdiInnerRoot.createInnerRootArcXri(subject, predicate);

		ContextNode innerRootContextNode = create ? this.getContextNode().setContextNode(innerRootArcXri) : this.getContextNode().getContextNode(innerRootArcXri, false);
		if (innerRootContextNode == null) return null;

		return new XdiInnerRoot(innerRootContextNode);
	}

	@Override
	public XdiRoot findRoot(XDI3Segment xri, boolean create) {

		if (log.isTraceEnabled()) log.trace("findRoot(" + xri + "," + create + ")");

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
	public XDI3Segment absoluteToRelativeXri(XDI3Segment xri) {

		if (log.isTraceEnabled()) log.trace("absoluteToRelativeXri(" + xri + ")");

		return XDI3Util.removeStartXri(xri, this.getContextNode().getXri());
	}

	@Override
	public XDI3Segment relativeToAbsoluteXri(XDI3Segment xri) {

		if (log.isTraceEnabled()) log.trace("relativeToAbsoluteXri(" + xri + ")");

		return XDI3Util.concatXris(xri, this.getContextNode().getXri());
	}

	@Override
	public XDI3Statement relativeToAbsoluteStatementXri(XDI3Statement statementXri) {

		if (log.isTraceEnabled()) log.trace("relativeToAbsoluteStatementXri(" + statementXri + ")");

		return StatementUtil.concatXriStatement(this.getContextNode().getXri(), statementXri.fromInnerRootNotation(true), true);
	}

	@Override
	public XDI3Statement absoluteToRelativeStatementXri(XDI3Statement statementXri) {

		if (log.isTraceEnabled()) log.trace("absoluteToRelativeStatementXri(" + statementXri + ")");

		return StatementUtil.removeStartXriStatement(statementXri, this.getContextNode().getXri(), true);
	}

	/*
	 * Helper classes
	 */

	public class MappingAbsoluteToRelativeStatementXriIterator extends MappingIterator<XDI3Statement, XDI3Statement> {

		public MappingAbsoluteToRelativeStatementXriIterator(Iterator<? extends XDI3Statement> iterator) {

			super(iterator);
		}

		@Override
		public XDI3Statement map(XDI3Statement statementXri) {

			return XdiAbstractRoot.this.absoluteToRelativeStatementXri(statementXri);
		}
	}

	public class MappingRelativeToAbsoluteStatementXriIterator extends MappingIterator<XDI3Statement, XDI3Statement> {

		public MappingRelativeToAbsoluteStatementXriIterator(Iterator<? extends XDI3Statement> iterator) {

			super(iterator);
		}

		@Override
		public XDI3Statement map(XDI3Statement statementXri) {

			return XdiAbstractRoot.this.relativeToAbsoluteStatementXri(statementXri);
		}
	}

	public class MappingAbsoluteToRelativeXriIterator extends MappingIterator<XDI3Segment, XDI3Segment> {

		public MappingAbsoluteToRelativeXriIterator(Iterator<? extends XDI3Segment> iterator) {

			super(iterator);
		}

		@Override
		public XDI3Segment map(XDI3Segment xri) {

			return XdiAbstractRoot.this.absoluteToRelativeXri(xri);
		}
	}

	public class MappingRelativeToAbsoluteXriIterator extends MappingIterator<XDI3Segment, XDI3Segment> {

		public MappingRelativeToAbsoluteXriIterator(Iterator<? extends XDI3Segment> iterator) {

			super(iterator);
		}

		@Override
		public XDI3Segment map(XDI3Segment xri) {

			return XdiAbstractRoot.this.relativeToAbsoluteXri(xri);
		}
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
