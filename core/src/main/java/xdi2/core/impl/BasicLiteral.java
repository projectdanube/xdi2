package xdi2.core.impl;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Literal;

public class BasicLiteral extends AbstractLiteral implements Literal {

	private static final long serialVersionUID = 3101871056623742994L;

	private String literalData;

	public BasicLiteral(Graph graph, ContextNode contextNode, String literalData) {

		super(graph, contextNode);

		this.literalData = literalData;
	}

	public BasicLiteral(String literalData) {

		this(null, null, literalData);
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
