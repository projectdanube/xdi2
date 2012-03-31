package xdi2.core.impl;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Literal;
import xdi2.core.Relation;
import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.core.xri3.impl.XRI3SubSegment;

public class BasicContextNode extends AbstractContextNode implements ContextNode {

	private static final long serialVersionUID = -3684616841641336596L;

	private XRI3SubSegment arcXri;
	private Iterable<ContextNode> contextNodes;
	private Iterable<Relation> relations;
	private Literal literal;
	
	public BasicContextNode(Graph graph, ContextNode contextNode, XRI3SubSegment arcXri, Iterable<ContextNode> contextNodes, Iterable<Relation> relations, Literal literal) {

		super(graph, contextNode);
		
		this.arcXri = arcXri;
		this.contextNodes = contextNodes;
		this.relations = relations;
		this.literal = literal;
	}

	@Override
	public XRI3SubSegment getArcXri() {
		
		return this.arcXri;
	}

	@Override
	public ContextNode createContextNode(XRI3SubSegment arcXri) {
		
		throw new Xdi2RuntimeException("Not supported");
	}

	@Override
	public Iterator<ContextNode> getContextNodes() {
		
		return this.contextNodes.iterator();
	}

	@Override
	public void deleteContextNode(XRI3SubSegment arcXri) {
		
		throw new Xdi2RuntimeException("Not supported");
	}

	@Override
	public void deleteContextNodes() {
		
		throw new Xdi2RuntimeException("Not supported");
	}

	@Override
	public Relation createRelation(XRI3Segment arcXri, XRI3Segment relationXri) {
		
		throw new Xdi2RuntimeException("Not supported");
	}

	@Override
	public Iterator<Relation> getRelations() {
		
		return this.relations.iterator();
	}

	@Override
	public void deleteRelation(XRI3Segment arcXri, XRI3Segment relationXri) {
		
		throw new Xdi2RuntimeException("Not supported");
	}

	@Override
	public void deleteRelations(XRI3Segment arcXri) {
		
		throw new Xdi2RuntimeException("Not supported");
	}

	@Override
	public void deleteRelations() {
		
		throw new Xdi2RuntimeException("Not supported");
	}

	@Override
	public Literal createLiteral(String literalData) {
		
		throw new Xdi2RuntimeException("Not supported");
	}

	@Override
	public Literal getLiteral() {
		
		return this.literal;
	}

	@Override
	public void deleteLiteral() {
		
		throw new Xdi2RuntimeException("Not supported");
	}
}
