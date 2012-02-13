package xdi2.core.impl.memory;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Literal;
import xdi2.core.impl.AbstractLiteral;

public class MemoryLiteral extends AbstractLiteral implements Literal {

	private static final long serialVersionUID = -7857969624385707741L;

	private String literalData;

	MemoryLiteral(Graph graph, ContextNode contextNode, String literalData) {

		super(graph, contextNode);

		this.literalData = literalData;
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
