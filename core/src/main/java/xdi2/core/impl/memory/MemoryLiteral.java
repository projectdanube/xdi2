package xdi2.core.impl.memory;

import xdi2.core.Literal;
import xdi2.core.impl.AbstractLiteral;

public class MemoryLiteral extends AbstractLiteral implements Literal {

	private static final long serialVersionUID = -7857969624385707741L;

	private Object literalData;

	MemoryLiteral(MemoryContextNode contextNode, Object literalData) {

		super(contextNode);

		this.literalData = literalData;
	}

	@Override
	public Object getLiteralData() {

		return this.literalData;
	}

	@Override
	public void setLiteralData(Object literalData) {

		this.literalData = literalData;
	}
}
