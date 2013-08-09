package xdi2.core.impl;

import xdi2.core.ContextNode;
import xdi2.core.Literal;

public class BasicLiteral extends AbstractLiteral implements Literal {

	private static final long serialVersionUID = 3101871056623742994L;

	private Object literalData;

	public BasicLiteral(ContextNode contextNode, String literalData) {

		super(contextNode);

		this.literalData = literalData;
	}

	public BasicLiteral(String literalData) {

		this(null, literalData);
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
