package xdi2.core.impl;

import java.util.Arrays;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Literal;
import xdi2.core.Relation;
import xdi2.core.util.iterators.ReadOnlyIterator;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3SubSegment;

public class BasicContextNode extends AbstractContextNode implements ContextNode {

	private static final long serialVersionUID = -3684616841641336596L;

	private XDI3SubSegment arcXri;
	private Iterable<ContextNode> contextNodes;
	private Iterable<Relation> relations;
	private Literal literal;

	public BasicContextNode(Graph graph, ContextNode contextNode, XDI3SubSegment arcXri, Iterable<ContextNode> contextNodes, Iterable<Relation> relations, Literal literal) {

		super(graph, contextNode);

		this.arcXri = arcXri;
		this.contextNodes = contextNodes == null ? Arrays.asList(new ContextNode[0]) : contextNodes;
		this.relations = relations == null ? Arrays.asList(new Relation[0]) : relations;
		this.literal = literal;
	}

	@Override
	public XDI3SubSegment getArcXri() {

		return this.arcXri;
	}

	@Override
	public ContextNode createContextNode(XDI3SubSegment arcXri) {

		throw new UnsupportedOperationException("Not supported.");
	}

	@Override
	public ReadOnlyIterator<ContextNode> getContextNodes() {

		return new ReadOnlyIterator<ContextNode> (this.contextNodes.iterator());
	}

	@Override
	public void deleteContextNode(XDI3SubSegment arcXri) {

		throw new UnsupportedOperationException("Not supported.");
	}

	@Override
	public void deleteContextNodes() {

		throw new UnsupportedOperationException("Not supported.");
	}

	@Override
	public Relation createRelation(XDI3Segment arcXri, ContextNode targetContextNode) {

		throw new UnsupportedOperationException("Not supported.");
	}

	@Override
	public ReadOnlyIterator<Relation> getRelations() {

		return new ReadOnlyIterator<Relation> (this.relations.iterator());
	}

	@Override
	public void deleteRelation(XDI3Segment arcXri, XDI3Segment targetContextNodeXri) {

		throw new UnsupportedOperationException("Not supported.");
	}

	@Override
	public void deleteRelations(XDI3Segment arcXri) {

		throw new UnsupportedOperationException("Not supported.");
	}

	@Override
	public void deleteRelations() {

		throw new UnsupportedOperationException("Not supported.");
	}

	@Override
	public Literal createLiteral(Object literalData) {

		throw new UnsupportedOperationException("Not supported.");
	}

	@Override
	public Literal getLiteral() {

		return this.literal;
	}

	@Override
	public void deleteLiteral() {

		throw new UnsupportedOperationException("Not supported.");
	}
}
