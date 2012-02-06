package xdi2.impl.memory;

import xdi2.ContextNode;
import xdi2.Graph;
import xdi2.Literal;
import xdi2.impl.AbstractLiteral;
import xdi2.xri3.impl.XRI3SubSegment;

public class MemoryLiteral extends AbstractLiteral implements Literal {

	private static final long serialVersionUID = -7857969624385707741L;

	XRI3SubSegment arcXri;
	String literalData;

	public MemoryLiteral(Graph graph, ContextNode contextNode) {

		super(graph, contextNode);
	}

	@Override
	public XRI3SubSegment getArcXri() {

		return arcXri;
	}

	@Override
	public String getLiteralData() {

		return literalData;
	}

	@Override
	public void setLiteralData(String literalData) {

		this.literalData = literalData;
	}
}
