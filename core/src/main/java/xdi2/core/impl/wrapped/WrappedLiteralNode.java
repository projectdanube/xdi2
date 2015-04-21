package xdi2.core.impl.wrapped;

import xdi2.core.LiteralNode;
import xdi2.core.impl.AbstractLiteralNode;
import xdi2.core.impl.memory.MemoryLiteralNode;

public class WrappedLiteralNode extends AbstractLiteralNode implements LiteralNode {

	private static final long serialVersionUID = 1602689543198362042L;

	private MemoryLiteralNode memoryLiteral;

	WrappedLiteralNode(WrappedContextNode contextNode, MemoryLiteralNode memoryLiteral) {

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
