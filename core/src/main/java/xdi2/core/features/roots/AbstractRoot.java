package xdi2.core.features.roots;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.Relation;
import xdi2.core.Statement;
import xdi2.core.util.StatementUtil;
import xdi2.core.util.XRIUtil;
import xdi2.core.util.iterators.SelectingMappingIterator;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;
import xdi2.core.xri3.XDI3SubSegment;

public abstract class AbstractRoot implements Root {

	private static final long serialVersionUID = 8157589883719452790L;

	private ContextNode contextNode;

	public AbstractRoot(ContextNode contextNode) {

		this.contextNode = contextNode;
	}

	@Override
	public ContextNode getContextNode() {

		return this.contextNode;
	}

	/*
	 * Finding roots related to this root
	 */

	@Override
	public LocalRoot findLocalRoot() {

		return new LocalRoot(this.getContextNode().getGraph().getRootContextNode());
	}

	@Override
	public PeerRoot findPeerRoot(XDI3Segment xri, boolean create) {

		XDI3SubSegment peerRootXri = PeerRoot.createPeerRootXri(xri);

		ContextNode peerRootContextNode = this.getContextNode().getContextNode(peerRootXri);
		if (peerRootContextNode == null && create) peerRootContextNode = this.getContextNode().createContextNode(peerRootXri);
		if (peerRootContextNode == null) return null;

		return new PeerRoot(peerRootContextNode);
	}

	@Override
	public InnerRoot findInnerRoot(XDI3Segment subject, XDI3Segment predicate, boolean create) {

		XDI3SubSegment innerRootXri = InnerRoot.createInnerRootXri(subject, predicate);

		ContextNode innerRootContextNode = this.getContextNode().getContextNode(innerRootXri);
		if (innerRootContextNode == null && create) innerRootContextNode = this.getContextNode().createContextNode(innerRootXri);
		if (innerRootContextNode == null) return null;

		ContextNode subjectContextNode = this.getContextNode().findContextNode(subject, create);
		if (subjectContextNode == null) return null;

		Relation predicateRelation = subjectContextNode.getRelation(predicate, innerRootContextNode.getXri());
		if (predicateRelation == null && create) predicateRelation = subjectContextNode.createRelation(predicate, innerRootContextNode.getXri());
		if (predicateRelation == null) return null;

		return new InnerRoot(innerRootContextNode);
	}

	@Override
	public Root findRoot(XDI3Segment xri, boolean create) {

		Root root = this;

		for (int i=0; i<xri.getNumSubSegments(); i++) {

			XDI3SubSegment subSegment = xri.getSubSegment(i);

			Root nextRoot = root.findRoot(subSegment, create);
			if (nextRoot == null) break;

			root = nextRoot;
		}

		return root;
	}

	@Override
	public Root findRoot(XDI3SubSegment arcXri, boolean create) {

		if (PeerRoot.isPeerRootXri(arcXri)) {

			ContextNode peerRootContextNode = this.getContextNode().getContextNode(arcXri);
			if (peerRootContextNode == null && create) peerRootContextNode = this.getContextNode().createContextNode(arcXri);
			if (peerRootContextNode == null) return null;

			return new PeerRoot(peerRootContextNode);
		}

		if (InnerRoot.isInnerRootXri(arcXri)) {

			ContextNode innerRootContextNode = this.getContextNode().getContextNode(arcXri);
			if (innerRootContextNode == null && create) innerRootContextNode = this.getContextNode().createContextNode(arcXri);
			if (innerRootContextNode == null) return null;

			ContextNode contextNode = this.getContextNode().findContextNode(InnerRoot.getSubjectOfInnerRootXri(arcXri), create);
			if (contextNode == null) return null;

			Relation relation = contextNode.getRelation(InnerRoot.getPredicateOfInnerRootXri(arcXri), innerRootContextNode.getXri());
			if (relation == null && create) relation = contextNode.createRelation(InnerRoot.getPredicateOfInnerRootXri(arcXri), innerRootContextNode.getXri());
			if (relation == null) return null;

			return new InnerRoot(innerRootContextNode);
		}

		return null;
	}

	/*
	 * Statements relative to this root.
	 */

	@Override
	public XDI3Segment getRelativePart(XDI3Segment xri) {

		if (this.getContextNode().isRootContextNode()) return xri;

		return XRIUtil.reduceXri(xri, this.getContextNode().getXri());
	}

	@Override
	public Statement createRelativeStatement(XDI3Statement statementXri) {

		statementXri = StatementUtil.expandStatement(statementXri, this.getContextNode().getXri());

		return this.getContextNode().getGraph().createStatement(statementXri);
	}

	@Override
	public Statement findRelativeStatement(XDI3Statement statementXri) {

		statementXri = StatementUtil.expandStatement(statementXri, this.getContextNode().getXri());

		return this.getContextNode().getGraph().findStatement(statementXri);
	}

	@Override
	public boolean containsRelativeStatement(XDI3Statement statementXri) {

		statementXri = StatementUtil.expandStatement(statementXri, this.getContextNode().getXri());

		return this.getContextNode().getGraph().containsStatement(statementXri);
	}

	@Override
	public Iterator<XDI3Statement> getRelativeStatements(final boolean ignoreImplied) {

		return new SelectingMappingIterator<Statement, XDI3Statement> (this.getContextNode().getAllStatements()) {

			@Override
			public boolean select(Statement statement) {

				if (ignoreImplied && statement.isImplied()) return false;

				return true;
			}

			@Override
			public XDI3Statement map(Statement statement) {

				return StatementUtil.reduceStatement(statement.getXri(), AbstractRoot.this.getContextNode().getXri());
			}
		};
	}

	/*
	 * Object methods
	 */

	@Override
	public String toString() {

		return this.getContextNode().toString();
	}

	@Override
	public boolean equals(Object object) {

		if (object == null || !(object instanceof Root)) return false;
		if (object == this) return true;

		Root other = (Root) object;

		return this.getContextNode().equals(other.getContextNode());
	}

	@Override
	public int hashCode() {

		int hashCode = 1;

		hashCode = (hashCode * 31) + this.getContextNode().hashCode();

		return hashCode;
	}

	@Override
	public int compareTo(Root other) {

		if (other == this || other == null) return 0;

		return this.getContextNode().compareTo(other.getContextNode());
	}
}
