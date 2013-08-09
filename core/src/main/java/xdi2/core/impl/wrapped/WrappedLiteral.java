package xdi2.core.impl.wrapped;

import xdi2.core.Literal;
import xdi2.core.impl.AbstractLiteral;
import xdi2.core.impl.memory.MemoryLiteral;

public class WrappedLiteral extends AbstractLiteral implements Literal {

	private static final long serialVersionUID = 1602689543198362042L;

	private MemoryLiteral memoryLiteral;

	WrappedLiteral(WrappedContextNode contextNode, MemoryLiteral memoryLiteral) {

		super(contextNode);

		this.memoryLiteral = memoryLiteral;
	}

	@Override
	public Object getLiteralData() {

		return this.memoryLiteral.getLiteralData();
	}

	@Override
	public void setLiteralData(Object literalData) {

		this.memoryLiteral.setLiteralData(literalData);
	}
}
