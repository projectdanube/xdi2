package xdi2.core.impl.json;

import xdi2.core.ContextNode;
import xdi2.core.Relation;
import xdi2.core.impl.AbstractRelation;
import xdi2.core.xri3.XDI3Segment;

public class JSONRelation extends AbstractRelation implements Relation {

	private static final long serialVersionUID = 60162590532295669L;

	private XDI3Segment arcXri;
	private XDI3Segment targetContextNodeXri;

	public JSONRelation(ContextNode contextNode, XDI3Segment arcXri, XDI3Segment targetContextNodeXri) {

		super(contextNode);

		this.arcXri = arcXri;
		this.targetContextNodeXri = targetContextNodeXri;
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
