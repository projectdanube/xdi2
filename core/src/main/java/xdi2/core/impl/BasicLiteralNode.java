package xdi2.core.impl;

import xdi2.core.ContextNode;
import xdi2.core.LiteralNode;

public class BasicLiteralNode extends AbstractLiteralNode implements LiteralNode {

	private static final long serialVersionUID = 3101871056623742994L;

	private Object literalData;

	public BasicLiteralNode(ContextNode contextNode, Object literalData) {

		super(contextNode);

		this.literalData = literalData;
	}

	public BasicLiteralNode(Object literalData) {

		this(new BasicContextNode(new BasicGraph(null, null), null, null, null, null, null), literalData);
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
