package xdi2.core.impl.memory;

import xdi2.core.LiteralNode;
import xdi2.core.impl.AbstractLiteralNode;

public class MemoryLiteralNode extends AbstractLiteralNode implements LiteralNode {

	private static final long serialVersionUID = -7857969624385707741L;

	private Object literalData;

	MemoryLiteralNode(MemoryContextNode contextNode, Object literalData) {

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
