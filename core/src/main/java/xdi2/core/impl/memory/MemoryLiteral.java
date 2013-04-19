package xdi2.core.impl.memory;

import xdi2.core.Literal;
import xdi2.core.impl.AbstractLiteral;

public class MemoryLiteral extends AbstractLiteral implements Literal {

	private static final long serialVersionUID = -7857969624385707741L;

	private String literalData;

	MemoryLiteral(MemoryContextNode contextNode, String literalData) {

		super(contextNode);

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
