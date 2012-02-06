package xdi2.impl.memory;

import xdi2.ContextNode;
import xdi2.Graph;
import xdi2.Relation;
import xdi2.impl.AbstractRelation;
import xdi2.xri3.impl.XRI3Segment;
import xdi2.xri3.impl.XRI3SubSegment;

public class MemoryRelation extends AbstractRelation implements Relation {

	private static final long serialVersionUID = -2979718490345210876L;

	XRI3SubSegment arcXri;
	XRI3Segment relationXri;

	MemoryRelation(Graph graph, ContextNode contextNode) {

		super(graph, contextNode);
	}

	@Override
	public XRI3SubSegment getArcXri() {

		return arcXri;
	}

	@Override
	public XRI3Segment getRelationXri() {

		return relationXri;
	}

	@Override
	public void setRelationXri(XRI3Segment relationXri) {

		this.relationXri = relationXri;
	}
}
