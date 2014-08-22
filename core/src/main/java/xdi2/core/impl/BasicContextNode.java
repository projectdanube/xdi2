package xdi2.core.impl;

import java.util.Arrays;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Literal;
import xdi2.core.Relation;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.util.iterators.ReadOnlyIterator;

public class BasicContextNode extends AbstractContextNode implements ContextNode {

	private static final long serialVersionUID = -3684616841641336596L;

	private XDIArc arc;
	private Iterable<ContextNode> contextNodes;
	private Iterable<Relation> relations;
	private Literal literal;

	public BasicContextNode(Graph graph, ContextNode contextNode, XDIArc arc, Iterable<ContextNode> contextNodes, Iterable<Relation> relations, Literal literal) {

		super(graph, contextNode);

		this.arc = arc;
		this.contextNodes = contextNodes == null ? Arrays.asList(new ContextNode[0]) : contextNodes;
		this.relations = relations == null ? Arrays.asList(new Relation[0]) : relations;
		this.literal = literal;
	}

	@Override
	public XDIArc getArc() {

		return this.arc;
	}

	@Override
	public ContextNode setContextNode(XDIArc arc) {

		throw new UnsupportedOperationException("Not supported.");
	}

	@Override
	public ReadOnlyIterator<ContextNode> getContextNodes() {

		return new ReadOnlyIterator<ContextNode> (this.contextNodes.iterator());
	}

	@Override
	public void delContextNode(XDIArc arc) {

		throw new UnsupportedOperationException("Not supported.");
	}

	@Override
	public void delContextNodes() {

		throw new UnsupportedOperationException("Not supported.");
	}

	@Override
	public Relation setRelation(XDIAddress arc, ContextNode targetContextNode) {

		throw new UnsupportedOperationException("Not supported.");
	}

	@Override
	public ReadOnlyIterator<Relation> getRelations() {

		return new ReadOnlyIterator<Relation> (this.relations.iterator());
	}

	@Override
	public void delRelation(XDIAddress arc, XDIAddress targetContextNodeAddress) {

		throw new UnsupportedOperationException("Not supported.");
	}

	@Override
	public void delRelations(XDIAddress arc) {

		throw new UnsupportedOperationException("Not supported.");
	}

	@Override
	public void delRelations() {

		throw new UnsupportedOperationException("Not supported.");
	}

	@Override
	public Literal setLiteral(Object literalData) {

		throw new UnsupportedOperationException("Not supported.");
	}

	@Override
	public Literal getLiteral() {

		return this.literal;
	}

	@Override
	public void delLiteral() {

		throw new UnsupportedOperationException("Not supported.");
	}
}
