package xdi2.core.impl;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Relation;
import xdi2.core.xri3.XDI3Segment;

@Deprecated
public class BasicRelation extends AbstractRelation implements Relation {

	private static final long serialVersionUID = -8757473050724884998L;

	private XDI3Segment arcXri;
	private XDI3Segment targetContextNodeXri;

	public BasicRelation(Graph graph, ContextNode contextNode, XDI3Segment arcXri, XDI3Segment targetContextNodeXri) {

		super(graph, contextNode);

		this.arcXri = arcXri;
		this.targetContextNodeXri = targetContextNodeXri;
	}

	public BasicRelation(XDI3Segment arcXri, XDI3Segment targetContextNodeXri) {

		this(null, null, arcXri, targetContextNodeXri);
	}

	@Override
	public XDI3Segment getArcXri() {

		return this.arcXri;
	}

	@Override
	public XDI3Segment getTargetContextNodeXri() {

		return this.targetContextNodeXri;
	}
}
