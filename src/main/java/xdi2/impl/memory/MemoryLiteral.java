package xdi2.impl.memory;

import xdi2.ContextNode;
import xdi2.Graph;
import xdi2.Literal;
import xdi2.impl.AbstractLiteral;
import xdi2.xri3.impl.XRI3SubSegment;

public class MemoryLiteral extends AbstractLiteral implements Literal {

	private static final long serialVersionUID = -7857969624385707741L;

	private XRI3SubSegment arcXri;
	private String literalData;

	MemoryLiteral(Graph graph, ContextNode contextNode, XRI3SubSegment arcXri, String literalData) {

		super(graph, contextNode);

		this.arcXri = arcXri;
		this.literalData = literalData;
	}

	@Override
	public XRI3SubSegment getArcXri() {

		return this.arcXri;
	}

	@Override
	public String getLiteralData() {

		return this.literalData;
	}

	@Override
	public void setLiteralData(String literalData) {

		this.literalData = literalData;
	}
}
