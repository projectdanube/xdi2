package xdi2.core.features.nodetypes;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.Relation;
import xdi2.core.Statement;
import xdi2.core.util.StatementUtil;
import xdi2.core.util.XDI3Util;
import xdi2.core.util.iterators.SelectingMappingIterator;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;
import xdi2.core.xri3.XDI3SubSegment;

public abstract class XdiAbstractRoot extends XdiAbstractContext implements XdiRoot {

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

		if (log.isTraceEnabled()) log.trace("isValid(" + contextNode + ")");

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

		if (log.isTraceEnabled()) log.trace("fromContextNode(" + contextNode + ")");

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
		
		return new XdiLocalRoot(this.getContextNode().getGraph().getRootContextNode());
	}

	@Override
	public XdiPeerRoot findPeerRoot(XDI3Segment xri, boolean create) {

		if (log.isTraceEnabled()) log.trace("findPeerRoot(" + xri + "," + create + ")");

		XDI3SubSegment peerRootArcXri = XdiPeerRoot.createPeerRootArcXri(xri);

		ContextNode peerRootContextNode = create ? this.getContextNode().setContextNode(peerRootArcXri) : this.getContextNode().getContextNode(peerRootArcXri);
		if (peerRootContextNode == null) return null;

		return new XdiPeerRoot(peerRootContextNode);
	}

	@Override
	public XdiInnerRoot findInnerRoot(XDI3Segment subject, XDI3Segment predicate, boolean create) {

		if (log.isTraceEnabled()) log.trace("findInnerRoot(" + subject + "," + predicate + "," + create + ")");

		XDI3SubSegment innerRootArcXri = XdiInnerRoot.createInnerRootArcXri(subject, predicate);

		ContextNode innerRootContextNode = create ? this.getContextNode().setContextNode(innerRootArcXri) : this.getContextNode().getContextNode(innerRootArcXri);
		if (innerRootContextNode == null) return null;

		ContextNode subjectContextNode = create ? this.getContextNode().setDeepContextNode(subject) : this.getContextNode().getDeepContextNode(subject);
		if (subjectContextNode == null) return null;

		Relation predicateRelation = create ? subjectContextNode.setRelation(predicate, innerRootContextNode.getXri()) : subjectContextNode.getRelation(predicate, innerRootContextNode.getXri());
		if (predicateRelation == null) return null;

		return new XdiInnerRoot(innerRootContextNode);
	}

	@Override
	public XdiRoot findRoot(XDI3Segment xri, boolean create) {

		if (log.isTraceEnabled()) log.trace("findRoot(" + xri + "," + create + ")");

		XdiRoot root = this;

		for (int i=0; i<xri.getNumSubSegments(); i++) {

			XDI3SubSegment subSegment = xri.getSubSegment(i);

			XdiRoot nextRoot = root.findRoot(subSegment, create);
			if (nextRoot == null) break;

			root = nextRoot;
		}

		return root;
	}

	@Override
	public XdiRoot findRoot(XDI3SubSegment arcXri, boolean create) {

		if (log.isTraceEnabled()) log.trace("findRoot(" + arcXri + "," + create + ")");

		if (XdiPeerRoot.isPeerRootArcXri(arcXri)) {

			ContextNode peerRootContextNode = create ? this.getContextNode().setContextNode(arcXri) : this.getContextNode().getContextNode(arcXri);
			if (peerRootContextNode == null) return null;

			return new XdiPeerRoot(peerRootContextNode);
		}

		if (XdiInnerRoot.isInnerRootArcXri(arcXri)) {

			ContextNode innerRootContextNode = create ? this.getContextNode().setContextNode(arcXri) : this.getContextNode().getContextNode(arcXri);
			if (innerRootContextNode == null) return null;

			ContextNode contextNode = create ? this.getContextNode().setDeepContextNode(XdiInnerRoot.getSubjectOfInnerRootXri(arcXri)) : this.getContextNode().getDeepContextNode(XdiInnerRoot.getSubjectOfInnerRootXri(arcXri));
			if (contextNode == null) return null;

			Relation relation = create ? contextNode.setRelation(XdiInnerRoot.getPredicateOfInnerRootXri(arcXri), innerRootContextNode.getXri()) : contextNode.getRelation(XdiInnerRoot.getPredicateOfInnerRootXri(arcXri), innerRootContextNode.getXri());
			if (relation == null) return null;

			return new XdiInnerRoot(innerRootContextNode);
		}

		return null;
	}

	/*
	 * Statements relative to this root
	 */

	@Override
	public XDI3Segment getRelativePart(XDI3Segment xri) {

		if (log.isTraceEnabled()) log.trace("getRelativePart(" + xri + ")");

		return XDI3Util.reduceXri(xri, this.getContextNode().getXri());
	}

	@Override
	public Statement createRelativeStatement(XDI3Statement statementXri) {

		if (log.isTraceEnabled()) log.trace("createRelativeStatement(" + statementXri + ")");

		statementXri = StatementUtil.expandStatement(statementXri, this.getContextNode().getXri());

		return this.getContextNode().getGraph().createStatement(statementXri);
	}

	@Override
	public Statement setRelativeStatement(XDI3Statement statementXri) {

		if (log.isTraceEnabled()) log.trace("setRelativeStatement(" + statementXri + ")");

		statementXri = StatementUtil.expandStatement(statementXri, this.getContextNode().getXri());

		return this.getContextNode().getGraph().setStatement(statementXri);
	}

	@Override
	public Statement getRelativeStatement(XDI3Statement statementXri) {

		if (log.isTraceEnabled()) log.trace("getRelativeStatement(" + statementXri + ")");

		statementXri = StatementUtil.expandStatement(statementXri, this.getContextNode().getXri());

		return this.getContextNode().getGraph().getStatement(statementXri);
	}

	@Override
	public boolean containsRelativeStatement(XDI3Statement statementXri) {

		if (log.isTraceEnabled()) log.trace("containsRelativeStatement(" + statementXri + ")");

		statementXri = StatementUtil.expandStatement(statementXri, this.getContextNode().getXri());

		return this.getContextNode().getGraph().containsStatement(statementXri);
	}

	@Override
	public Iterator<XDI3Statement> getRelativeStatements(final boolean ignoreImplied) {

		if (log.isTraceEnabled()) log.trace("getRelativeStatements(" + ignoreImplied + ")");

		return new SelectingMappingIterator<Statement, XDI3Statement> (this.getContextNode().getAllStatements()) {

			@Override
			public boolean select(Statement statement) {

				if (ignoreImplied && statement.isImplied()) return false;

				return true;
			}

			@Override
			public XDI3Statement map(Statement statement) {

				return StatementUtil.reduceStatement(statement.getXri(), XdiAbstractRoot.this.getContextNode().getXri());
			}
		};
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

		if (XdiLocalRoot.isLocalRootXri(arcXri)) return true;
		if (XdiPeerRoot.isPeerRootArcXri(arcXri)) return true;
		if (XdiInnerRoot.isInnerRootArcXri(arcXri)) return true;

		return false;
	}
}
