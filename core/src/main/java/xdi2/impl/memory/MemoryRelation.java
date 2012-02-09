package xdi2.impl.memory;

import xdi2.ContextNode;
import xdi2.Graph;
import xdi2.Relation;
import xdi2.impl.AbstractRelation;
import xdi2.xri3.impl.XRI3Segment;

public class MemoryRelation extends AbstractRelation implements Relation {

	private static final long serialVersionUID = -2979718490345210876L;

	private XRI3Segment arcXri;
	private XRI3Segment relationXri;

	MemoryRelation(Graph graph, ContextNode contextNode, XRI3Segment arcXri, XRI3Segment relationXri) {

		super(graph, contextNode);
		
		this.arcXri = arcXri;
		this.relationXri = relationXri;
	}

	public XRI3Segment getArcXri() {

		return this.arcXri;
	}

	public XRI3Segment getRelationXri() {

		return this.relationXri;
	}

	public void setRelationXri(XRI3Segment relationXri) {

		this.relationXri = relationXri;
	}
}
