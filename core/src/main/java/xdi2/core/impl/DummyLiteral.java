package xdi2.core.impl;

import xdi2.core.ContextNode;
import xdi2.core.Literal;

public class DummyLiteral extends AbstractLiteral implements Literal {

	private static final long serialVersionUID = 3101871056623742994L;

	private Object literalData;

	public DummyLiteral(ContextNode contextNode, Object literalData) {

		super(contextNode);

		this.literalData = literalData;
	}

	public DummyLiteral(Object literalData) {

		this(new DummyContextNode(new DummyGraph(null, null), null, null, null, null, null), literalData);
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
