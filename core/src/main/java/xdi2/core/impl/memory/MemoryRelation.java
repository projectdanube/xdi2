package xdi2.core.impl.memory;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Relation;
import xdi2.core.impl.AbstractRelation;
import xdi2.core.xri3.impl.XRI3Segment;

public class MemoryRelation extends AbstractRelation implements Relation {

	private static final long serialVersionUID = -2979718490345210876L;

	private XRI3Segment arcXri;
	private XRI3Segment targetContextNodeXri;

	MemoryRelation(Graph graph, ContextNode contextNode, XRI3Segment arcXri, XRI3Segment targetContextNodeXri) {

		super(graph, contextNode);

		this.arcXri = arcXri;
		this.targetContextNodeXri = targetContextNodeXri;
	}

	@Override
	public XRI3Segment getArcXri() {

		return this.arcXri;
	}

	@Override
	public XRI3Segment getTargetContextNodeXri() {

		return this.targetContextNodeXri;
	}
}
